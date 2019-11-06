package com.doctopl.doctoplapi.controller;


import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.doctopl.doctoplapi.model.ConfirmationToken;
import com.doctopl.doctoplapi.model.Utilisateur;
import com.doctopl.doctoplapi.payload.ApiResponse;
import com.doctopl.doctoplapi.payload.JwtAuthenticationResponse;
import com.doctopl.doctoplapi.payload.LoginRequest;
import com.doctopl.doctoplapi.payload.SignUpRequest;
import com.doctopl.doctoplapi.security.JwtTokenProvider;
import com.doctopl.doctoplapi.service.EmailService;
import com.doctopl.doctoplapi.service.UtilisateurService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Value("${mail.expiryTimeInMinutes}")
	private String expiryTimeInMinutes;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UtilisateurService utilisateurService;

	@Autowired
	JwtTokenProvider tokenProvider;

	@Autowired
	EmailService emailService;


	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						loginRequest.getUsernameOrEmail(),
						loginRequest.getPassword()
						)
				);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String jwt = tokenProvider.generateToken(authentication);
		return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

		if(utilisateurService.existsByNomUtilisateur(signUpRequest.getUsername())) {
			return new ResponseEntity<>(new ApiResponse(false, "Nom d'Utilsateur déjà existant!"),
					HttpStatus.BAD_REQUEST);
		}

		if(utilisateurService.existsByEmail(signUpRequest.getEmail())) {
			return new ResponseEntity<>(new ApiResponse(false, "adresse mail déjà existant!"),
					HttpStatus.BAD_REQUEST);
		}
		
		Utilisateur utilisateur = utilisateurService.createUser(signUpRequest);
		ConfirmationToken confirmationToken =utilisateurService.createConfirmationToken(utilisateur);
		utilisateurService.sendEmailConfirmation(confirmationToken);

		return new ResponseEntity<> (new ApiResponse(true, "Utilisateur enregistré avec succès.Un mail de confirmation vous a été envoyé"),HttpStatus.OK);
	}

	@GetMapping("/confirm-account")
	ResponseEntity<?> confirmUserAccount(@RequestParam("token") String confirmationToken){

		
		ConfirmationToken token = utilisateurService.getConfirmationToken(confirmationToken);

		if(token == null) {   
			return new ResponseEntity<>(new ApiResponse(false, "token de confirmation non valide!"),
					HttpStatus.BAD_REQUEST);
		}

		if(token.isExpiryDate(Integer.parseInt(expiryTimeInMinutes))) {
			return new ResponseEntity<>(new ApiResponse(false, "Mail de confirmation expiré"),
					HttpStatus.REQUEST_TIMEOUT);
		}
	
		Utilisateur utilisateur = utilisateurService.getUserByEmail(token.getUtilisateur().getEmail());
		if(utilisateur.isActive()) {
			return new ResponseEntity<>(new ApiResponse(false, "Confirmation deja faite"),
					HttpStatus.GONE);
		}
		utilisateur.setActive(true);
		Utilisateur result = utilisateurService.saveUser(utilisateur);

		return new ResponseEntity<> (new ApiResponse(true, "Utilisateur enregistré avec succès"),
				HttpStatus.OK);

	}

	@GetMapping(value = "/resendRegistrationToken")
	public ResponseEntity<?>  resendRegistrationToken(@RequestParam("token") String existingToken) {
		
		ConfirmationToken token = utilisateurService.getConfirmationToken(existingToken);
		token.setConfirmationToken(UUID.fromString(existingToken).toString());
		ConfirmationToken newtoken = utilisateurService.updateConfirmationToken(token);
		
		utilisateurService.sendEmailConfirmation(newtoken);
	
		return new ResponseEntity<> (new ApiResponse(true, "Un mail de confirmation a ete envoyé"),
				HttpStatus.OK);
	}
}
