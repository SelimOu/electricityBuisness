package com.example.electricitybusiness.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.electricitybusiness.entity.Lieu;
import com.example.electricitybusiness.repository.LieuRepository;

@Service
public class LieuService {
    private static final Logger logger = LoggerFactory.getLogger(LieuService.class);
    private final LieuRepository repository;
    private final com.example.electricitybusiness.service.UtilisateurService utilisateurService;

    public LieuService(LieuRepository repository, com.example.electricitybusiness.service.UtilisateurService utilisateurService) {
        this.repository = repository;
        this.utilisateurService = utilisateurService;
    }

    public List<Lieu> findAll() { return repository.findAll(); }
    public Optional<Lieu> findById(Long id) { return repository.findById(id); }
    public Lieu save(Lieu l) { return repository.save(l); }

    // find lieux owned by a given utilisateur
    public java.util.List<Lieu> findByOwnerId(Long ownerId) { return repository.findByUtilisateurId(ownerId); }

    public void deleteById(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String subject = auth != null ? String.valueOf(auth.getPrincipal()) : null;
        Long ownerId = null;
        if (subject != null) {
            try {
                var uopt = this.utilisateurService.findByPseudo(subject);
                if (uopt.isEmpty()) uopt = this.utilisateurService.findByAdresseMail(subject);
                if (uopt.isPresent()) ownerId = uopt.get().getId();
            } catch (Exception e) {
                logger.info("Error resolving owner in LieuService.deleteById for subject {}: {}", subject, e.getMessage());
            }
        }

        if (ownerId == null) throw new AccessDeniedException("Owner could not be resolved for Lieu delete");

        int deleted = repository.deleteByIdAndUtilisateurId(id, ownerId);
        if (deleted == 1) return;

        if (!repository.existsById(id)) {
            throw new EmptyResultDataAccessException("No Lieu entity with id " + id, 1);
        } else {
            throw new AccessDeniedException("Not owner of the Lieu resource");
        }
    }

    public int deleteByIdAndOwnerId(Long id, Long ownerId) { return repository.deleteByIdAndUtilisateurId(id, ownerId); }
}
