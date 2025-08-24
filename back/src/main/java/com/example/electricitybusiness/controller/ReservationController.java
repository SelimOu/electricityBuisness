package com.example.electricitybusiness.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.electricitybusiness.entity.Reservation;
import com.example.electricitybusiness.service.BorneService;
import com.example.electricitybusiness.service.ReservationService;
import com.example.electricitybusiness.service.UtilisateurService;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService service;
    private final UtilisateurService utilisateurService;
    private final BorneService borneService;

    public ReservationController(ReservationService service, UtilisateurService utilisateurService, BorneService borneService) {
        this.service = service;
        this.utilisateurService = utilisateurService;
        this.borneService = borneService;
    }

    @GetMapping
    public List<Reservation> all() { return service.findAll(); }

    @GetMapping("/mine")
    public ResponseEntity<?> mine() {
        Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String subject = auth != null ? String.valueOf(auth.getPrincipal()) : null;
        if (subject == null) return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).build();
        try {
            var uopt = this.utilisateurService.findByPseudo(subject);
            if (uopt.isEmpty()) uopt = this.utilisateurService.findByAdresseMail(subject);
            if (uopt.isPresent()) {
                Long ownerId = uopt.get().getId();
                var list = service.findByUtilisateurId(ownerId);
                // map to DTOs exposing borne and utilisateur display fields expected by frontend
                var dtoList = list.stream().map(r -> {
                    Long bid = r.getBorne() != null ? r.getBorne().getId() : null;
                    String bname = null;
                    if (r.getBorne() != null) {
                        bname = r.getBorne().getNomBorne();
                    }
                    Long uid = r.getUtilisateur() != null ? r.getUtilisateur().getId() : null;
                    String uname = r.getUtilisateur() != null ? r.getUtilisateur().getPseudo() : null;
                    return new com.example.electricitybusiness.dto.ReservationDto(r.getId(), bid, bname, uid, uname, r.getDateDebut(), r.getDateFin(), r.getPrixMinuteHisto(), r.getEtat());
                }).collect(Collectors.toList());
                return ResponseEntity.ok(dtoList);
            }
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getById(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Reservation> create(@RequestBody JsonNode node) {
        Reservation r = new Reservation();
        applyJsonToReservation(node, r);
        // if the client didn't provide an utilisateur, set it to the currently authenticated user
        if (r.getUtilisateur() == null) {
            Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            String subject = auth != null ? String.valueOf(auth.getPrincipal()) : null;
            if (subject != null) {
                var uopt = this.utilisateurService.findByPseudo(subject);
                if (uopt.isEmpty()) uopt = this.utilisateurService.findByAdresseMail(subject);
                uopt.ifPresent(r::setUtilisateur);
            }
        }
        Reservation saved = service.save(r);
        return ResponseEntity.created(URI.create("/api/reservations/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> update(@PathVariable Long id, @RequestBody JsonNode node) {
        return service.findById(id).map(existing -> {
            // only change fields present in the JSON
            applyJsonToReservation(node, existing);
            return ResponseEntity.ok(service.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private void applyJsonToReservation(JsonNode node, Reservation r) {
        if (node.has("idUtilisateur")) {
            Long uid = node.get("idUtilisateur").asLong();
            utilisateurService.findById(uid).ifPresent(r::setUtilisateur);
        }
        if (node.has("idBorne")) {
            Long bid = node.get("idBorne").asLong();
            borneService.findById(bid).ifPresent(r::setBorne);
        }
        if (node.has("dateDebut")) {
            try {
                LocalDateTime dt = LocalDateTime.parse(node.get("dateDebut").asText());
                r.setDateDebut(dt);
            } catch (DateTimeParseException e) {
                // ignore invalid format
            }
        }
        if (node.has("dateFin")) {
            try {
                LocalDateTime dt = LocalDateTime.parse(node.get("dateFin").asText());
                r.setDateFin(dt);
            } catch (DateTimeParseException e) {
                // ignore invalid format
            }
        }
        if (node.has("prixMinuteHisto")) {
            r.setPrixMinuteHisto(node.get("prixMinuteHisto").asDouble());
        }
        if (node.has("etat")) {
            r.setEtat(node.get("etat").asText());
        }
    }
}
