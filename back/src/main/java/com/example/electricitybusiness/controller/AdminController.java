package com.example.electricitybusiness.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.electricitybusiness.service.SeedService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final SeedService seedService;
    public AdminController(SeedService seedService) { this.seedService = seedService; }

    @PostMapping("/seed")
    @GetMapping("/seed")
    public ResponseEntity<String> seed() {
        seedService.seed(true);
        return ResponseEntity.ok("seeded");
    }
}
