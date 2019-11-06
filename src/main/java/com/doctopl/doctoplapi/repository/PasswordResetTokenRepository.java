package com.doctopl.doctoplapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.doctopl.doctoplapi.model.ConfirmationToken;
import com.doctopl.doctoplapi.model.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long>{

	PasswordResetToken findByPasswordResetToken(String passwordResetToken);
}
