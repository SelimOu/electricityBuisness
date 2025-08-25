package com.example.electricitybusiness.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.electricitybusiness.entity.Adresse;
import com.example.electricitybusiness.repository.AdresseRepository;

@Service
public class AdresseService {
    private final AdresseRepository repository;
    public AdresseService(AdresseRepository repository) { this.repository = repository; }
    public List<Adresse> findAll() { return repository.findAll(); }
    public Optional<Adresse> findById(Long id) { return repository.findById(id); }
    public Adresse save(Adresse a) { return repository.save(a); }
    public void deleteById(Long id) { repository.deleteById(id); }
    public java.util.List<Adresse> findByLieuId(Long lieuId) { return repository.findByLieuId(lieuId); }
}
