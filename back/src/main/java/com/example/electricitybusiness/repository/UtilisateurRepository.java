package com.example.electricitybusiness.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.electricitybusiness.entity.Utilisateur;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
	java.util.Optional<com.example.electricitybusiness.entity.Utilisateur> findFirstByPseudo(String pseudo);
	java.util.Optional<com.example.electricitybusiness.entity.Utilisateur> findFirstByAdresseMail(String adresseMail);
}
