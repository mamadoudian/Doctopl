package com.doctopl.doctoplapi.service.impl;

import java.net.URI;
import java.util.Collections;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.doctopl.doctoplapi.exception.AppException;
import com.doctopl.doctoplapi.model.ConfirmationToken;
import com.doctopl.doctoplapi.model.PasswordResetToken;
import com.doctopl.doctoplapi.model.Role;
import com.doctopl.doctoplapi.model.RoleName;
import com.doctopl.doctoplapi.model.Utilisateur;
import com.doctopl.doctoplapi.payload.ApiResponse;
import com.doctopl.doctoplapi.payload.SignUpRequest;
import com.doctopl.doctoplapi.repository.ConfirmationTokenRepository;
import com.doctopl.doctoplapi.repository.PasswordResetTokenRepository;
import com.doctopl.doctoplapi.repository.ProfessionelRepository;
import com.doctopl.doctoplapi.repository.RoleRepository;
import com.doctopl.doctoplapi.repository.UtilisateurRepository;
import com.doctopl.doctoplapi.service.EmailService;
import com.doctopl.doctoplapi.service.UtilisateurService;

@Service
public class UtilisateurServiceImp implements UtilisateurService{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UtilisateurServiceImp.class);

	@Autowired
	UtilisateurRepository utilisateurRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordResetTokenRepository passwordResetTokenRepository;

	@Autowired
	ProfessionelRepository professionelRepository;

	@Autowired
	ConfirmationTokenRepository confirmationTokenRepository;
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	PasswordEncoder passwordEncoder;

	@Override
	public Utilisateur getUserByEmail(String userEmail) {
		Optional<Utilisateur> result = utilisateurRepository.findByEmail(userEmail);
		if(result==null) {
			LOGGER.error("Utilisateur inexitant :" +userEmail);
		}
		return result.get();
	}

	@Override
	public Utilisateur createUser(SignUpRequest signUpRequest) {
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Debut Création d'un user");
		}
		Utilisateur utilisateur =  new Utilisateur(
				signUpRequest.getUsername(), 
				signUpRequest.getName(), 
				signUpRequest.getPrenom(), 
				signUpRequest.getEmail(), 
				passwordEncoder.encode(signUpRequest.getPassword()),
				signUpRequest.getDateNaissance() ,
				signUpRequest.getLieu(),
				signUpRequest.getSexe(), 
				signUpRequest.getTypeUtilisateur());
		//creation d'un compte professionnel
		Role userRole = roleRepository.findByNom(RoleName.ROLE_USER)
				.orElseThrow(() -> new AppException("Role de l'utilisateur non renseigné."));

		utilisateur.setRoles(Collections.singleton(userRole));
		Utilisateur result = utilisateurRepository.save(utilisateur);
		if(result==null) {
			LOGGER.error("Erreur de création de l'utilisateur");
		}else {
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("Utilisateur créer avec succès");
			}
		}
		
		return result;
	}

	@Override
	public boolean existsByNomUtilisateur(String userName) {
		return utilisateurRepository.existsByNomUtilisateur(userName);
	}

	@Override
	public boolean existsByEmail(String email) {
		return utilisateurRepository.existsByEmail(email);
	}

	@Override
	public boolean sendEmailConfirmation(ConfirmationToken confirmationToken) {
		
		URI confirmAccountUrl = ServletUriComponentsBuilder
				.fromCurrentContextPath().path("/api/auth/confirm-account?token={token}")
				.buildAndExpand(confirmationToken.getConfirmationToken()).toUri();

		Utilisateur utilisateur = confirmationToken.getUtilisateur();
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(utilisateur.getEmail());
		mailMessage.setSubject("Complete Registration!");
		mailMessage.setFrom("chand312902@gmail.com");
		mailMessage.setText("To confirm your account, please click here : "+
				confirmAccountUrl.toString());

		emailService.sendSimpleMessage(mailMessage);
		
		return true;
	}

	@Override
	public ConfirmationToken createConfirmationToken(Utilisateur utilisateur) {
		ConfirmationToken confirmationToken = new ConfirmationToken(utilisateur);
		return confirmationTokenRepository.save(confirmationToken);
	}

	@Override
	public Utilisateur saveUser(Utilisateur utilisateur) {
		return utilisateurRepository.save(utilisateur);
	}

	@Override
	public ConfirmationToken getConfirmationToken(String token) {
		return confirmationTokenRepository.findByConfirmationToken(token);
	}

	@Override
	public ConfirmationToken updateConfirmationToken(ConfirmationToken token) {
		return confirmationTokenRepository.save(token);
	}

	@Override
	public PasswordResetToken createPasswordToken(Utilisateur utilisateur) {
		PasswordResetToken passwordResetToken = new PasswordResetToken(utilisateur);
		return passwordResetTokenRepository.save(passwordResetToken);
	}

	@Override
	public boolean sendEmailPasswordReset(PasswordResetToken passwordResetToken) {
		URI confirmAccountUrl = ServletUriComponentsBuilder
				.fromCurrentContextPath().path("resetPassword?token={token}")
				.buildAndExpand(passwordResetToken.getPasswordResetToken()).toUri();

		Utilisateur utilisateur = passwordResetToken.getUtilisateur();
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(utilisateur.getEmail());
		mailMessage.setSubject("Complete Registration!");
		mailMessage.setFrom("chand312902@gmail.com");
		mailMessage.setText("To confirm your account, please click here : "+
				confirmAccountUrl.toString());

		emailService.sendSimpleMessage(mailMessage);
		
		return true;
	}

	@Override
	public PasswordResetToken getPassworResetToken(String token) {
		return passwordResetTokenRepository.findByPasswordResetToken(token);
	}

	@Override
	public void deletePasswordResetToken(PasswordResetToken passToken) {
		passwordResetTokenRepository.delete(passToken);
		
	}

	
}
