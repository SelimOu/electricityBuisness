package com.example.electricitybusiness.controller;

import java.net.URI;
import java.time.LocalDate;
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

import com.example.electricitybusiness.entity.Utilisateur;
import com.example.electricitybusiness.service.UtilisateurService;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {
    private final UtilisateurService service;
    public UtilisateurController(UtilisateurService service) { this.service = service; }

    @GetMapping
    public List<Utilisateur> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> getById(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Utilisateur> create(@RequestBody Utilisateur u) {
        Utilisateur saved = service.save(u);
        return ResponseEntity.created(URI.create("/api/utilisateurs/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Utilisateur> update(@PathVariable Long id, @RequestBody JsonNode patch) {
        return service.findById(id).map(existing -> {
            // apply only fields present in the incoming JSON
            if (patch.hasNonNull("nomUtilisateur")) existing.setNomUtilisateur(patch.get("nomUtilisateur").asText());
            if (patch.hasNonNull("prenom")) existing.setPrenom(patch.get("prenom").asText());
            if (patch.hasNonNull("pseudo")) existing.setPseudo(patch.get("pseudo").asText());
            if (patch.hasNonNull("motDePasse")) existing.setMotDePasse(patch.get("motDePasse").asText());
            if (patch.hasNonNull("role")) existing.setRole(patch.get("role").asText());
            if (patch.hasNonNull("adresseMail")) existing.setAdresseMail(patch.get("adresseMail").asText());
            if (patch.hasNonNull("dateNaissance")) {
                try {
                    existing.setDateNaissance(LocalDate.parse(patch.get("dateNaissance").asText()));
                } catch (Exception ex) { /* ignore parse errors */ }
            }
            if (patch.hasNonNull("vehicule")) existing.setVehicule(patch.get("vehicule").asText());
            if (patch.has("banni")) existing.setBanni(patch.get("banni").asBoolean(false));
            // relationships left unchanged unless explicitly managed via dedicated endpoints
            Utilisateur saved = service.save(existing);
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
