package com.example.electricitybusiness.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.electricitybusiness.entity.Borne;

@Repository
public interface BorneRepository extends JpaRepository<Borne, Long> {
	// delete only when the linked lieu's utilisateur id matches
	// returns number of rows deleted (0 if none)
	@Modifying
	@Transactional
	// Use a subquery to avoid generating a CROSS JOIN delete which is not valid SQL in MySQL
	@Query("delete from Borne b where b.id = :id and b.lieu.id in (select l.id from Lieu l where l.utilisateur.id = :utilisateurId)")
	int deleteByIdAndLieuUtilisateurId(@Param("id") Long id, @Param("utilisateurId") Long utilisateurId);

	// find all bornes where the associated lieu is owned by the given utilisateur id
	// Use explicit joins to ensure SQL joins the three tables (borne -> lieu -> utilisateur)
	@Query("select b from Borne b join b.lieu l join l.utilisateur u where u.id = :utilisateurId")
	List<Borne> findByOwnerId(@Param("utilisateurId") Long utilisateurId);

	// backwards-compatible alias in case older code calls the previous method name
	default List<Borne> findByLieuUtilisateurId(Long utilisateurId) {
		return findByOwnerId(utilisateurId);
	}
}
