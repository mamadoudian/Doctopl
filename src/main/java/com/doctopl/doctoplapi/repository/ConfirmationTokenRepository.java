package com.doctopl.doctoplapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.doctopl.doctoplapi.model.ConfirmationToken;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long>{
	
	ConfirmationToken findByConfirmationToken(String confirmationToken);
	
}
