package com.doctopl.doctoplapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doctopl.doctoplapi.model.Specialite;

@Repository
public interface SpecialiteRepository extends JpaRepository<Specialite, String>{

}
