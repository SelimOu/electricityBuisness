package com.example.electricitybusiness.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.electricitybusiness.entity.Utilisateur;
import com.example.electricitybusiness.repository.UtilisateurRepository;

@Service
public class UtilisateurService {
    private final UtilisateurRepository repository;

    public UtilisateurService(UtilisateurRepository repository) { this.repository = repository; }

    public List<Utilisateur> findAll() { return repository.findAll(); }
    public Optional<Utilisateur> findById(Long id) { return repository.findById(id); }
    public Utilisateur save(Utilisateur u) { return repository.save(u); }
    public void deleteById(Long id) { repository.deleteById(id); }
    public Optional<Utilisateur> findByPseudo(String pseudo) { return repository.findFirstByPseudo(pseudo); }
    public Optional<Utilisateur> findByAdresseMail(String mail) { return repository.findFirstByAdresseMail(mail); }
}
