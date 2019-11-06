package com.doctopl.doctoplapi.service;

import org.springframework.stereotype.Service;

import com.doctopl.doctoplapi.model.ConfirmationToken;
import com.doctopl.doctoplapi.model.PasswordResetToken;
import com.doctopl.doctoplapi.model.Utilisateur;
import com.doctopl.doctoplapi.payload.SignUpRequest;

@Service
public interface UtilisateurService {
	
	public Utilisateur getUserByEmail(String userEmail);
	public Utilisateur createUser (SignUpRequest signUpRequest);
	public boolean existsByNomUtilisateur(String UserName);
	public boolean existsByEmail(String email);
	public boolean sendEmailConfirmation(ConfirmationToken confirmationToken);
	public ConfirmationToken createConfirmationToken(Utilisateur utilisateur);
	public Utilisateur saveUser(Utilisateur utilisateur);
	public ConfirmationToken getConfirmationToken(String token);
	public ConfirmationToken updateConfirmationToken(ConfirmationToken token);
	public PasswordResetToken createPasswordToken(Utilisateur utilisateur);
	public boolean sendEmailPasswordReset(PasswordResetToken passwordResetToken);
	public PasswordResetToken getPassworResetToken(String token);
	public void deletePasswordResetToken(PasswordResetToken passToken);
}

