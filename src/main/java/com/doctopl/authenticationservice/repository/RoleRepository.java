package com.doctopl.authenticationservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doctopl.authenticationservice.model.Role;
import com.doctopl.authenticationservice.model.RoleName;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
	Optional<Role> findByNom(RoleName roleName);

}
