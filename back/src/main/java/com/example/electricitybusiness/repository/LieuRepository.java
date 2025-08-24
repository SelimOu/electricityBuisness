package com.example.electricitybusiness.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.electricitybusiness.entity.Lieu;

@Repository
public interface LieuRepository extends JpaRepository<Lieu, Long> {
	@Modifying
	@Transactional
	// Guarded delete: only delete the Lieu when it belongs to the specified utilisateur
	@Query("delete from Lieu l where l.id = :id and l.utilisateur.id = :utilisateurId")
	int deleteByIdAndUtilisateurId(@Param("id") Long id, @Param("utilisateurId") Long utilisateurId);

	@Query("select l from Lieu l where l.utilisateur.id = :utilisateurId")
	java.util.List<Lieu> findByUtilisateurId(@Param("utilisateurId") Long utilisateurId);
}
