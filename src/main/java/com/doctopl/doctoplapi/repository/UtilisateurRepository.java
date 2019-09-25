package com.doctopl.doctoplapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doctopl.doctoplapi.model.Utilisateur;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long>{

	Optional<Utilisateur> findByEmail(String email);

    Optional<Utilisateur> findByNomUtilisateurOrEmail(String nomUtilisateur, String email);

    List<Utilisateur> findByIdIn(List<Long> userIds);

    Optional<Utilisateur> findByNomUtilisateur(String nomUtilisateur);

    Boolean existsByNomUtilisateur(String nomUtilisateur);

    Boolean existsByEmail(String email);

}
