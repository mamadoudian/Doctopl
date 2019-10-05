package com.doctopl.doctoplapi.controller;

import java.net.URI;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.doctopl.doctoplapi.exception.AppException;
import com.doctopl.doctoplapi.model.ConfirmationToken;
import com.doctopl.doctoplapi.model.PasswordResetToken;
import com.doctopl.doctoplapi.model.Professionel;
import com.doctopl.doctoplapi.model.Role;
import com.doctopl.doctoplapi.model.RoleName;
import com.doctopl.doctoplapi.model.TypeUtilisateur;
import com.doctopl.doctoplapi.model.Utilisateur;
import com.doctopl.doctoplapi.payload.ApiResponse;
import com.doctopl.doctoplapi.payload.JwtAuthenticationResponse;
import com.doctopl.doctoplapi.payload.LoginRequest;
import com.doctopl.doctoplapi.payload.SignUpRequest;
import com.doctopl.doctoplapi.repository.ConfirmationTokenRepository;
import com.doctopl.doctoplapi.repository.PasswordResetTokenRepository;
import com.doctopl.doctoplapi.repository.ProfessionelRepository;
import com.doctopl.doctoplapi.repository.RoleRepository;
import com.doctopl.doctoplapi.repository.UtilisateurRepository;
import com.doctopl.doctoplapi.request.PasswordRequest;
import com.doctopl.doctoplapi.security.JwtTokenProvider;
import com.doctopl.doctoplapi.service.EmailSenderService;
import com.doctopl.doctoplapi.service.EmailService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Value("mail.expiryTimeInMinutes")
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

		if(utilisateurRepository.existsByNomUtilisateur(signUpRequest.getUsername())) {
			return new ResponseEntity(new ApiResponse(false, "Nom d'Utilsateur déjà existant!"),
					HttpStatus.BAD_REQUEST);
		}

		if(utilisateurRepository.existsByEmail(signUpRequest.getEmail())) {
			return new ResponseEntity(new ApiResponse(false, "adresse mail déjà existant!"),
					HttpStatus.BAD_REQUEST);
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
				.orElseThrow(() -> new AppException("Le role de l'utilisateur non renseigné."));

		utilisateur.setRoles(Collections.singleton(userRole));
		Utilisateur result = utilisateurRepository.save(utilisateur);

		ConfirmationToken confirmationToken = new ConfirmationToken(result);
		confirmationTokenRepository.save(confirmationToken);

		URI confirmAccountUrl = ServletUriComponentsBuilder
				.fromCurrentContextPath().path("/api/auth/confirm-account?token={token}")
				.buildAndExpand(confirmationToken.getConfirmationToken()).toUri();

		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(result.getEmail());
		mailMessage.setSubject("Complete Registration!");
		mailMessage.setFrom("chand312902@gmail.com");
		mailMessage.setText("To confirm your account, please click here : "+
				confirmAccountUrl.toString());

		emailService.sendSimpleMessage(mailMessage);


		URI location = ServletUriComponentsBuilder
				.fromCurrentContextPath().path("/api/users/{username}")
				.buildAndExpand(result.getNomUtilisateur()).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "Utilisateur enregistré avec succès.Un mail de confirmation vous a été envoyé"));
	}

	@GetMapping("/confirm-account")
	ResponseEntity<?> confirmUserAccount(@RequestParam("token") String confirmationToken){

		ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

		if(token == null) {   
			return new ResponseEntity(new ApiResponse(false, "token de confirmation non valide!"),
					HttpStatus.BAD_REQUEST);
		}

		if(token.isExpiryDate(Integer.parseInt(expiryTimeInMinutes))) {
			return new ResponseEntity(new ApiResponse(false, "Mail de confirmation expiré"),
					HttpStatus.REQUEST_TIMEOUT);
		}
		Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findByEmail(token.getUtilisateur().getEmail());
		Utilisateur utilisateur = utilisateurOptional.get();
		if(utilisateur.isActive()) {
			return new ResponseEntity(new ApiResponse(false, "Confirmation deja faite"),
					HttpStatus.GONE);
		}
		utilisateur.setActive(true);
		Utilisateur result = utilisateurRepository.save(utilisateur);

		URI location = ServletUriComponentsBuilder
				.fromCurrentContextPath().path("/api/users/{username}")
				.buildAndExpand(result.getNomUtilisateur()).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "Utilisateur enregistré avec succès"));

	}

	@GetMapping(value = "/resendRegistrationToken")
	public ResponseEntity<?>  resendRegistrationToken(@RequestParam("token") String existingToken) {

		ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(existingToken);
		token.setConfirmationToken(UUID.fromString(existingToken).toString());
		ConfirmationToken newtoken = confirmationTokenRepository.save(token);

		Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findByEmail(newtoken.getUtilisateur().getEmail());
		Utilisateur utilisateur = utilisateurOptional.get();

		URI confirmAccountUrl = ServletUriComponentsBuilder
				.fromCurrentContextPath().path("/api/auth/confirm-account?token={token}")
				.buildAndExpand(newtoken.getConfirmationToken()).toUri();

		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(utilisateur.getEmail());
		mailMessage.setSubject("Complete Registration!");
		mailMessage.setFrom("chand312902@gmail.com");
		mailMessage.setText("To confirm your account, please click here : "+
				confirmAccountUrl.toString());

		emailService.sendSimpleMessage(mailMessage);

		URI location = ServletUriComponentsBuilder
				.fromCurrentContextPath().path("/api/users/{username}")
				.buildAndExpand(utilisateur.getNomUtilisateur()).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "Un mail de confirmation a ete envoyé"));
	}

	@GetMapping(value = "/resetPassword")
	public ResponseEntity<?>  resetPassword(@RequestParam("email") String userEmail) {
		Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findByEmail(userEmail);
		Utilisateur utilisateur = utilisateurOptional.get();
		if (utilisateur == null) {
			new ResponseEntity(new ApiResponse(false, "Utilisateur non existant"),
					HttpStatus.NOT_FOUND); 
		}

		PasswordResetToken passwordResetToken = new PasswordResetToken (utilisateur);
		passwordResetTokenRepository.save(passwordResetToken);

		URI confirmAccountUrl = ServletUriComponentsBuilder
				.fromCurrentContextPath().path("/api/auth/changePassword?id={id}&token={token}")
				.buildAndExpand(utilisateur.getId(), passwordResetToken.getPasswordResetToken()).toUri();

		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(utilisateur.getEmail());
		mailMessage.setSubject("Complete Registration!");
		mailMessage.setFrom("chand312902@gmail.com");
		mailMessage.setText("To confirm your account, please click here : "+
				confirmAccountUrl.toString());

		emailService.sendSimpleMessage(mailMessage);

		URI location = ServletUriComponentsBuilder
				.fromCurrentContextPath().path("/api/users/{username}")
				.buildAndExpand(utilisateur.getNomUtilisateur()).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "Un mail de confirmation a ete envoyé"));

	}

	@GetMapping(value = "/changePassword")
	public ResponseEntity<?> showChangePasswordPage(@RequestParam("id") long id, @RequestParam("token") String token) {

		PasswordResetToken passToken = passwordResetTokenRepository.findByPasswordResetToken(token);

		if((passToken==null) || (passToken.getUtilisateur().getId() != id)) {
			new ResponseEntity(new ApiResponse(false, "Token non present pour l'Utilisateur"),
					HttpStatus.NOT_ACCEPTABLE);
		}

		if(passToken.isExpiryDate(Integer.parseInt(expiryTimeInMinutes))) {
			return new ResponseEntity(new ApiResponse(false, "Mail de confirmation expiré"),
					HttpStatus.REQUEST_TIMEOUT);
		}


		Utilisateur utilisateur = passToken.getUtilisateur();
		Authentication auth = new UsernamePasswordAuthenticationToken(
				utilisateur, null, null);
		SecurityContextHolder.getContext().setAuthentication(auth);

		URI location = ServletUriComponentsBuilder
				.fromCurrentContextPath().path("/api/users/{username}")
				.buildAndExpand(utilisateur.getNomUtilisateur()).toUri();
		return ResponseEntity.created(location).body(new ApiResponse(true, "Changement de mot de passe"));
	}
	
	@PostMapping(value = "/savePassword")
	public ResponseEntity<?> savePassword( @Valid PasswordRequest passwordRequest) {
	    Utilisateur utilisateur = 
	      (Utilisateur) SecurityContextHolder.getContext()
	                                  .getAuthentication().getPrincipal();
	     
	    utilisateur.setPassword(passwordEncoder.encode(passwordRequest.getPassword()));
	    utilisateurRepository.save(utilisateur);
	    
	    URI location = ServletUriComponentsBuilder
				.fromCurrentContextPath().path("/api/users/{username}")
				.buildAndExpand(utilisateur.getNomUtilisateur()).toUri();
	   
	    return ResponseEntity.created(location).body(new ApiResponse(true, "Mot de passe change avec succes"));
	}
}
