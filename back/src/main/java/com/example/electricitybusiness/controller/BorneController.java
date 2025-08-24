package com.example.electricitybusiness.controller;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
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

import com.example.electricitybusiness.entity.Adresse;
import com.example.electricitybusiness.entity.Borne;
import com.example.electricitybusiness.entity.Lieu;
import com.example.electricitybusiness.entity.Media;
import com.example.electricitybusiness.service.AdresseService;
import com.example.electricitybusiness.service.BorneService;
import com.example.electricitybusiness.service.LieuService;
import com.example.electricitybusiness.service.MediaService;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/bornes")
public class BorneController {
    private static final Logger logger = LoggerFactory.getLogger(BorneController.class);
    private final BorneService service;
    private final LieuService lieuService;
    private final AdresseService adresseService;
    private final MediaService mediaService;
    private final com.example.electricitybusiness.service.UtilisateurService utilisateurService;

    public BorneController(BorneService service, LieuService lieuService, AdresseService adresseService, MediaService mediaService, com.example.electricitybusiness.service.UtilisateurService utilisateurService) {
        this.service = service;
        this.lieuService = lieuService;
        this.adresseService = adresseService;
        this.mediaService = mediaService;
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public List<Borne> all() { return service.findAll(); }

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
            logger.info("Error resolving owner for /api/bornes/mine subject={}: {}", subject, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Borne> getById(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Borne> create(@RequestBody JsonNode node) {
        // Expected shapes:
        // { "borne": { ... }, "lieu": { id } OR { instructions, utilisateur: { id } , adresse: { ... } }, "medias": [ { url, type, nomMedia, description, taille }, ... ] }
        try {
            JsonNode borneNode = node.has("borne") ? node.get("borne") : node;
            Borne b = new Borne();
            if (borneNode.has("nomBorne")) b.setNomBorne(borneNode.get("nomBorne").asText());
            if (borneNode.has("coordGPS")) b.setCoordGPS(borneNode.get("coordGPS").asText());
            if (borneNode.has("tarif")) b.setTarif(borneNode.get("tarif").asDouble());
            if (borneNode.has("puissance")) b.setPuissance(borneNode.get("puissance").asInt());
            if (borneNode.has("instruction")) b.setInstruction(borneNode.get("instruction").asText());
            if (borneNode.has("surPied")) b.setSurPied(borneNode.get("surPied").asBoolean());
            if (borneNode.has("etat")) b.setEtat(borneNode.get("etat").asText());
            if (borneNode.has("occupee")) b.setOccupee(borneNode.get("occupee").asBoolean());

            // handle lieu
            if (node.has("lieu") && node.get("lieu") != null && !node.get("lieu").isNull()) {
                JsonNode lieuNode = node.get("lieu");
                if (lieuNode.has("id") && !lieuNode.get("id").isNull()) {
                    Long lid = lieuNode.get("id").asLong();
                    // if existing lieu is found and has no utilisateur, set current authenticated user as owner
                    lieuService.findById(lid).ifPresent(l -> {
                        try {
                            if (l.getUtilisateur() == null) {
                                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                                String subject = auth != null ? String.valueOf(auth.getPrincipal()) : null;
                                if (subject != null) {
                                    var uopt = this.utilisateurService.findByPseudo(subject);
                                    if (uopt.isEmpty()) uopt = this.utilisateurService.findByAdresseMail(subject);
                                    if (uopt.isPresent()) {
                                        l.setUtilisateur(uopt.get());
                                        // persist the updated lieu so it becomes owned
                                        this.lieuService.save(l);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            logger.debug("Error setting owner on existing Lieu {}: {}", lid, e.getMessage());
                        }
                        b.setLieu(l);
                    });
                } else {
                    Lieu l = new Lieu();
                    if (lieuNode.has("instructions")) l.setInstructions(lieuNode.get("instructions").asText());
                    if (lieuNode.has("utilisateur")) {
                        JsonNode util = lieuNode.get("utilisateur");
                        if (util.has("id")) {
                            // only set reference by id to avoid loading user fully here
                            com.example.electricitybusiness.entity.Utilisateur u = new com.example.electricitybusiness.entity.Utilisateur();
                            u.setId(util.get("id").asLong());
                            l.setUtilisateur(u);
                        } else if (util.has("pseudo")) {
                            // resolve by pseudo if provided
                            String pseudo = util.get("pseudo").asText();
                            this.utilisateurService.findAll().stream()
                                .filter(u -> pseudo.equals(u.getPseudo()))
                                .findFirst()
                                .ifPresent(l::setUtilisateur);
                        }
                    }
                    // if no utilisateur attached, try to set authenticated user as owner
                    if (l.getUtilisateur() == null) {
                        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                        String subject = auth != null ? String.valueOf(auth.getPrincipal()) : null;
                        if (subject != null) {
                            try {
                                var uopt = this.utilisateurService.findByPseudo(subject);
                                if (uopt.isEmpty()) uopt = this.utilisateurService.findByAdresseMail(subject);
                                if (uopt.isPresent()) l.setUtilisateur(uopt.get());
                            } catch (Exception e) {
                                logger.debug("Error resolving owner for nested Lieu by subject {}: {}", subject, e.getMessage());
                            }
                        }
                    }
                    Lieu savedLieu = lieuService.save(l);

                    // handle adresse attached to new lieu
                    if (lieuNode.has("adresse") && !lieuNode.get("adresse").isNull()) {
                        JsonNode adr = lieuNode.get("adresse");
                        Adresse a = new Adresse();
                        if (adr.has("nomAdresse")) a.setNomAdresse(adr.get("nomAdresse").asText());
                        if (adr.has("numeroEtRue")) a.setNumeroEtRue(adr.get("numeroEtRue").asText());
                        if (adr.has("codePostal")) a.setCodePostal(adr.get("codePostal").asText());
                        if (adr.has("ville")) a.setVille(adr.get("ville").asText());
                        if (adr.has("pays")) a.setPays(adr.get("pays").asText());
                        if (adr.has("region")) a.setRegion(adr.get("region").asText());
                        if (adr.has("complement")) a.setComplement(adr.get("complement").asText());
                        if (adr.has("etage")) a.setEtage(adr.get("etage").asText());
                        a.setLieu(savedLieu);
                        adresseService.save(a);
                    }
                    b.setLieu(savedLieu);
                }
            }

            Borne saved = service.save(b);

            // handle medias list
            if (node.has("medias") && node.get("medias").isArray()) {
                for (JsonNode mnode : node.get("medias")) {
                    Media m = new Media();
                    if (mnode.has("url")) m.setUrl(mnode.get("url").asText());
                    if (mnode.has("type")) m.setType(mnode.get("type").asText());
                    if (mnode.has("nomMedia")) m.setNomMedia(mnode.get("nomMedia").asText());
                    if (mnode.has("description")) m.setDescription(mnode.get("description").asText());
                    if (mnode.has("taille")) m.setTaille(mnode.get("taille").asLong());
                    m.setBorne(saved);
                    mediaService.save(m);
                }
            }

            return ResponseEntity.created(URI.create("/api/bornes/" + saved.getId())).body(saved);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Borne> update(@PathVariable Long id, @RequestBody Borne b) {
        var opt = service.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Borne existing = opt.get();
        // ownership check
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

            Long resourceOwnerId = null;
            if (existing.getLieu() != null && existing.getLieu().getUtilisateur() != null) resourceOwnerId = existing.getLieu().getUtilisateur().getId();
            logger.debug("Ownership check for Borne {}: subject={} ownerId={} resourceOwnerId={}", id, subject, ownerId, resourceOwnerId);
            if (resourceOwnerId != null) {
                if (ownerId == null || !resourceOwnerId.equals(ownerId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
        }

        b.setId(existing.getId());
        return ResponseEntity.ok(service.save(b));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        var opt = service.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Borne existing = opt.get();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String subject = auth != null ? String.valueOf(auth.getPrincipal()) : null;
        Long ownerId = null;
        Long resourceOwnerId = null;
        if (subject != null) {
            try {
                var uopt = this.utilisateurService.findByPseudo(subject);
                if (uopt.isEmpty()) uopt = this.utilisateurService.findByAdresseMail(subject);
                if (uopt.isPresent()) ownerId = uopt.get().getId();
            } catch (Exception e) {
                logger.info("Error resolving owner by subject {}: {}", subject, e.getMessage());
            }

            try {
                if (existing.getLieu() != null) {
                    if (existing.getLieu().getUtilisateur() != null) {
                        resourceOwnerId = existing.getLieu().getUtilisateur().getId();
                    } else {
                        // try reload lieu from service to ensure we have the utilisateur
                        var lopt = this.lieuService.findById(existing.getLieu().getId());
                        if (lopt.isPresent() && lopt.get().getUtilisateur() != null) resourceOwnerId = lopt.get().getUtilisateur().getId();
                    }
                }
            } catch (Exception e) {
                logger.info("Error resolving resource owner for Borne {}: {}", id, e.getMessage());
            }

            logger.info("Ownership check (delete) for Borne {}: subject={} ownerId={} resourceOwnerId={}", id, subject, ownerId, resourceOwnerId);
            if (resourceOwnerId != null) {
                if (ownerId == null || !resourceOwnerId.equals(ownerId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
        }

        try {
            // require owner id resolution to perform delete; do not fallback to unguarded delete
            if (ownerId == null) {
                logger.info("Delete denied for Borne {} because owner could not be resolved (subject={})", id, subject);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            int deleted = service.deleteByIdAndOwnerId(id, ownerId);
            if (deleted == 1) return ResponseEntity.noContent().build();

            // deleted 0 -> either not found or owner mismatch
            if (service.findById(id).isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (EmptyResultDataAccessException ex) {
            logger.debug("Attempted to delete non-existent Borne {}: {}", id, ex.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
