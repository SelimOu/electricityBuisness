package com.example.electricitybusiness.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.electricitybusiness.entity.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	// explicit join to ensure JPQL generates a join between reservation and utilisateur
	@Query("select r from Reservation r join r.utilisateur u where u.id = :utilisateurId")
	java.util.List<Reservation> findByUtilisateurIdJoined(@Param("utilisateurId") Long utilisateurId);

	// backwards-compatible alias
	default java.util.List<Reservation> findByUtilisateurId(Long utilisateurId) {
		return findByUtilisateurIdJoined(utilisateurId);
	}
}
