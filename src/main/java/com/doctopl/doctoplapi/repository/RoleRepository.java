package com.doctopl.doctoplapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doctopl.doctoplapi.model.Role;
import com.doctopl.doctoplapi.model.RoleName;

@Repository
public interface RoleRepository extends JpaRepository<Role, String>{
	Optional<Role> findByNom(RoleName roleName);

}
