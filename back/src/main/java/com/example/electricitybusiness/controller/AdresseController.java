package com.example.electricitybusiness.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.electricitybusiness.entity.Adresse;
import com.example.electricitybusiness.service.AdresseService;

@RestController
@RequestMapping("/api/adresses")
public class AdresseController {
    private final AdresseService service;
    public AdresseController(AdresseService service) { this.service = service; }

    @GetMapping
    public List<Adresse> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Adresse> getById(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Adresse> create(@RequestBody Adresse a) {
        Adresse saved = service.save(a);
        return ResponseEntity.created(URI.create("/api/adresses/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Adresse> update(@PathVariable Long id, @RequestBody Adresse a) {
        return service.findById(id).map(existing -> {
            a.setId(existing.getId());
            return ResponseEntity.ok(service.save(a));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
