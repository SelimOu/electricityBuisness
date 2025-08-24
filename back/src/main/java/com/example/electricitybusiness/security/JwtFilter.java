package com.example.electricitybusiness.security;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Skip JWT processing for preflight and auth endpoints
        String path = request.getRequestURI();
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            // Short-circuit preflight: set CORS headers and return 200 so browser accepts preflight.
            String origin = request.getHeader("Origin");
            if (origin == null) origin = "http://localhost:4200";
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            String reqHeaders = request.getHeader("Access-Control-Request-Headers");
            response.setHeader("Access-Control-Allow-Headers", reqHeaders != null ? reqHeaders : "*");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        if (path.startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth == null) {
            logger.debug("No Authorization header present for {} {}", request.getMethod(), request.getRequestURI());
        } else if (!auth.startsWith("Bearer ")) {
            logger.debug("Authorization header does not start with 'Bearer ': {}", auth);
        } else {
            String token = auth.substring(7);
            boolean valid = jwtUtil.validateToken(token);
            logger.debug("Token validation result: {} for token starting with {}", valid, token.length() > 10 ? token.substring(0, 10) : token);
            if (valid) {
                try {
                    String username = jwtUtil.extractSubject(token);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                    logger.debug("Failed to extract subject from token: {}", e.getMessage());
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
