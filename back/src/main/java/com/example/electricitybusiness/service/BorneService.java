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

import com.example.electricitybusiness.entity.Borne;
import com.example.electricitybusiness.repository.BorneRepository;

@Service
public class BorneService {
    private static final Logger logger = LoggerFactory.getLogger(BorneService.class);
    static {
        logger.info("BorneService class loaded - build-marker=v2");
    }
    private final BorneRepository repository;
    private final com.example.electricitybusiness.service.UtilisateurService utilisateurService;

    public BorneService(BorneRepository repository, com.example.electricitybusiness.service.UtilisateurService utilisateurService) {
        this.repository = repository;
        this.utilisateurService = utilisateurService;
    }

    public List<Borne> findAll() { return repository.findAll(); }
    
    // find bornes owned by a specific utilisateur (owner)
    public List<Borne> findByOwnerId(Long ownerId) { return repository.findByOwnerId(ownerId); }
    public Optional<Borne> findById(Long id) { return repository.findById(id); }
    public Borne save(Borne b) { return repository.save(b); }

    // Guarded delete: require authenticated owner; will throw AccessDeniedException or EmptyResultDataAccessException
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
                logger.info("Error resolving owner in BorneService.deleteById for subject {}: {}", subject, e.getMessage());
            }
        }

        if (ownerId == null) {
            throw new AccessDeniedException("Owner could not be resolved for delete");
        }

        int deleted = repository.deleteByIdAndLieuUtilisateurId(id, ownerId);
        if (deleted == 1) return;

        // deleted == 0 -> either not found or owner mismatch
        if (!repository.existsById(id)) {
            throw new EmptyResultDataAccessException("No Borne entity with id " + id, 1);
        } else {
            throw new AccessDeniedException("Not owner of the resource");
        }
    }

    public int deleteByIdAndOwnerId(Long id, Long ownerId) {
        logger.info("Attempting guarded delete for Borne id={} by ownerId={}", id, ownerId);
        int deleted = repository.deleteByIdAndLieuUtilisateurId(id, ownerId);
        logger.info("Guarded delete result for Borne id={} ownerId={} -> deletedRows={}", id, ownerId, deleted);
        return deleted;
    }
}
