package com.example.electricitybusiness.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.electricitybusiness.security.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthDebugController {
    private final JwtUtil jwtUtil;

    public AuthDebugController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // Non-protected helper to validate a token and return the subject (debug only)
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam("token") String token) {
        Map<String, Object> resp = new HashMap<>();
        boolean valid = jwtUtil.validateToken(token);
        resp.put("valid", valid);
        if (valid) {
            try {
                resp.put("subject", jwtUtil.extractSubject(token));
            } catch (Exception e) {
                resp.put("error", "failed to extract subject: " + e.getMessage());
            }
        } else {
            resp.put("error", "token invalid or expired");
        }
        return ResponseEntity.ok(resp);
    }
}
