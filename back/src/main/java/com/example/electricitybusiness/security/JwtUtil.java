package com.example.electricitybusiness.security;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private final String secret = "change_this_secret_for_prod";
    private final long validityMs = 24 * 3600 * 1000; // 24h

    public String generateToken(String subject) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + validityMs);
        return Jwts.builder().setSubject(subject).setIssuedAt(now).setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, secret.getBytes()).compact();
    }

    public String extractSubject(String token) {
        Claims claims = Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            logger.debug("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }
}
