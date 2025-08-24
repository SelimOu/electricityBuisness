package com.example.electricitybusiness.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.electricitybusiness.entity.Adresse;

@Repository
public interface AdresseRepository extends JpaRepository<Adresse, Long> {
}
