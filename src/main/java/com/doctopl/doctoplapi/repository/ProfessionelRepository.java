package com.doctopl.doctoplapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doctopl.doctoplapi.model.Professionel;

@Repository
public interface ProfessionelRepository extends JpaRepository<Professionel, Long>{

}
