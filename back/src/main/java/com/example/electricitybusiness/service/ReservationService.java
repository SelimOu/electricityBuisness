package com.example.electricitybusiness.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.electricitybusiness.entity.Reservation;
import com.example.electricitybusiness.repository.ReservationRepository;

@Service
public class ReservationService {
    private final ReservationRepository repository;
    public ReservationService(ReservationRepository repository) { this.repository = repository; }
    public List<Reservation> findAll() { return repository.findAll(); }
    public Optional<Reservation> findById(Long id) { return repository.findById(id); }
    public Reservation save(Reservation r) { return repository.save(r); }
    public void deleteById(Long id) { repository.deleteById(id); }

    // find reservations for a specific utilisateur (uses explicit join)
    public java.util.List<Reservation> findByUtilisateurId(Long utilisateurId) { return repository.findByUtilisateurIdJoined(utilisateurId); }
}
