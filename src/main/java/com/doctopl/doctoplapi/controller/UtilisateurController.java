package com.doctopl.doctoplapi.controller;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doctopl.doctoplapi.model.PasswordResetToken;
import com.doctopl.doctoplapi.model.Utilisateur;
import com.doctopl.doctoplapi.payload.ApiResponse;
import com.doctopl.doctoplapi.repository.ConfirmationTokenRepository;
import com.doctopl.doctoplapi.repository.PasswordResetTokenRepository;
import com.doctopl.doctoplapi.repository.ProfessionelRepository;
import com.doctopl.doctoplapi.repository.RoleRepository;
import com.doctopl.doctoplapi.repository.UtilisateurRepository;
import com.doctopl.doctoplapi.request.PasswordRequest;
import com.doctopl.doctoplapi.security.JwtTokenProvider;
import com.doctopl.doctoplapi.service.EmailService;
import com.doctopl.doctoplapi.service.UtilisateurService;

@RestController
@RequestMapping("/api/user")
public class UtilisateurController {
	
	@Value("${mail.expiryTimeInMinutes}")
	private String expiryTimeInMinutes;

	@Autowired
	AuthenticationManager authenticationManager;

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
	PasswordEncoder passwordEncoder;

	@Autowired
	JwtTokenProvider tokenProvider;

	@Autowired
	EmailService emailService;
	
	@Autowired
	UtilisateurService utilisateurService;
	
	@GetMapping(value = "/resetPassword")
	public ResponseEntity<?>  resetPassword(@RequestParam("email") String userEmail) {
		
		Utilisateur utilisateur = utilisateurService.getUserByEmail(userEmail);
		if (utilisateur == null) {
			return new ResponseEntity<>(new ApiResponse(false, "Utilisateur non existant"),
					HttpStatus.NOT_FOUND); 
		}

		PasswordResetToken passwordResetToken = utilisateurService.createPasswordToken(utilisateur);
		utilisateurService.sendEmailPasswordReset(passwordResetToken);

		return new ResponseEntity<>(new ApiResponse(true, "Un mail de confirmation a ete envoyé"),HttpStatus.OK);

	}
	
	@PostMapping(value = "/savePassword")
	public ResponseEntity<?> savePassword( @Valid @RequestBody PasswordRequest passwordRequest) {
		
		PasswordResetToken passToken = utilisateurService.getPassworResetToken(passwordRequest.getToken());

		if((passToken==null)) {
			new ResponseEntity<>(new ApiResponse(false, "Token non present pour l'Utilisateur"),
					HttpStatus.NOT_ACCEPTABLE);
		}

		if(passToken.isExpiryDate(Integer.parseInt(expiryTimeInMinutes))) {
			return new ResponseEntity<>(new ApiResponse(false, "Mail de confirmation expiré"),
					HttpStatus.REQUEST_TIMEOUT);
		}

		Utilisateur utilisateur = passToken.getUtilisateur();
		utilisateur.setPassword(passwordEncoder.encode(passwordRequest.getPassword()));
		utilisateurService.saveUser(utilisateur);
		utilisateurService.deletePasswordResetToken(passToken);
	
	    return new ResponseEntity<> (new ApiResponse(true, "Mot de passe changé avec succes")
	    		,HttpStatus.OK);
	}
	
	@GetMapping(value = "/{userName}")
	public ResponseEntity<?> getUserByUserName( @RequestAttribute("userName") String userName) {
		
		return new ResponseEntity<>(new ApiResponse(false, userName),
				HttpStatus.OK);
	}
	
}
