package com.example.electricitybusiness.controller;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.electricitybusiness.entity.Lieu;
import com.example.electricitybusiness.service.LieuService;
import com.example.electricitybusiness.service.UtilisateurService;

@RestController
@RequestMapping("/api/lieux")
public class LieuController {
    private static final Logger logger = LoggerFactory.getLogger(LieuController.class);
    private final LieuService service;
    private final UtilisateurService utilisateurService;
    public LieuController(LieuService service, UtilisateurService utilisateurService) { this.service = service; this.utilisateurService = utilisateurService; }

    @GetMapping
    public List<Lieu> all() { return service.findAll(); }

    @GetMapping("/mine")
    public ResponseEntity<?> mine() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String subject = auth != null ? String.valueOf(auth.getPrincipal()) : null;
        if (subject == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        try {
            var uopt = this.utilisateurService.findByPseudo(subject);
            if (uopt.isEmpty()) uopt = this.utilisateurService.findByAdresseMail(subject);
            if (uopt.isPresent()) {
                Long ownerId = uopt.get().getId();
                var list = service.findByOwnerId(ownerId);
                return ResponseEntity.ok(list);
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            logger.info("Error resolving owner for /api/lieux/mine subject={}: {}", subject, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lieu> getById(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Lieu> create(@RequestBody Lieu l) {
        // if no owner provided, try to attach the authenticated user as owner
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String subject = auth != null ? String.valueOf(auth.getPrincipal()) : null;
        if (subject != null && (l.getUtilisateur() == null || l.getUtilisateur().getId() == null)) {
            try {
                var uopt = this.utilisateurService.findByPseudo(subject);
                if (uopt.isEmpty()) uopt = this.utilisateurService.findByAdresseMail(subject);
                if (uopt.isPresent()) l.setUtilisateur(uopt.get());
            } catch (Exception e) {
                logger.debug("Error resolving owner for new Lieu by subject {}: {}", subject, e.getMessage());
            }
        }

        Lieu saved = service.save(l);
        return ResponseEntity.created(URI.create("/api/lieux/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Lieu> update(@PathVariable Long id, @RequestBody Lieu l) {
        var opt = service.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Lieu existing = opt.get();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String subject = auth != null ? String.valueOf(auth.getPrincipal()) : null;
        if (subject != null) {
            Long ownerId = null;
            try {
                var uopt = this.utilisateurService.findByPseudo(subject);
                if (uopt.isEmpty()) uopt = this.utilisateurService.findByAdresseMail(subject);
                if (uopt.isPresent()) ownerId = uopt.get().getId();
            } catch (Exception e) {
                logger.debug("Error resolving owner by subject {}: {}", subject, e.getMessage());
            }

            Long resourceOwnerId = existing.getUtilisateur() != null ? existing.getUtilisateur().getId() : null;
            logger.debug("Ownership check for Lieu {}: subject={} ownerId={} resourceOwnerId={}", id, subject, ownerId, resourceOwnerId);
            if (resourceOwnerId != null) {
                if (ownerId == null || !resourceOwnerId.equals(ownerId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
        }

        l.setId(existing.getId());
        return ResponseEntity.ok(service.save(l));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        var opt = service.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Lieu existing = opt.get();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String subject = auth != null ? String.valueOf(auth.getPrincipal()) : null;
        if (subject != null) {
            Long ownerId = null;
            try {
                var uopt = this.utilisateurService.findByPseudo(subject);
                if (uopt.isEmpty()) uopt = this.utilisateurService.findByAdresseMail(subject);
                if (uopt.isPresent()) ownerId = uopt.get().getId();
            } catch (Exception e) {
                logger.info("Error resolving owner by subject {}: {}", subject, e.getMessage());
            }

            Long resourceOwnerId = null;
            try {
                if (existing.getUtilisateur() != null) resourceOwnerId = existing.getUtilisateur().getId();
                else {
                    // attempt reload from service to avoid lazy loading issues
                    var lopt = this.service.findById(id);
                    if (lopt.isPresent() && lopt.get().getUtilisateur() != null) resourceOwnerId = lopt.get().getUtilisateur().getId();
                }
            } catch (Exception e) {
                logger.info("Error resolving resource owner for Lieu {}: {}", id, e.getMessage());
            }

            logger.info("Ownership check (delete) for Lieu {}: subject={} ownerId={} resourceOwnerId={}", id, subject, ownerId, resourceOwnerId);
            if (resourceOwnerId != null) {
                if (ownerId == null || !resourceOwnerId.equals(ownerId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
        }

        try {
            service.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
            logger.debug("Attempted to delete non-existent Lieu {}: {}", id, ex.getMessage());
            return ResponseEntity.notFound().build();
        } catch (org.springframework.security.access.AccessDeniedException ex) {
            logger.info("Delete denied for Lieu {}: {}", id, ex.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // helper endpoint to return adresses for a lieu
    @GetMapping("/{id}/adresses")
    public ResponseEntity<java.util.List<com.example.electricitybusiness.entity.Adresse>> getAdressesForLieu(@PathVariable Long id) {
        try {
            var list = this.service.findById(id).map(l -> l.getAdresses()).orElse(java.util.Collections.emptyList());
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            logger.info("Error fetching adresses for lieu {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
