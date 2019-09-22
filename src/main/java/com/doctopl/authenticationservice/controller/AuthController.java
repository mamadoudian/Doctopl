package com.doctopl.authenticationservice.controller;

import java.net.URI;
import java.util.Collections;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.doctopl.authenticationservice.exception.AppException;
import com.doctopl.authenticationservice.model.Role;
import com.doctopl.authenticationservice.model.RoleName;
import com.doctopl.authenticationservice.model.Utilisateur;
import com.doctopl.authenticationservice.payload.ApiResponse;
import com.doctopl.authenticationservice.payload.JwtAuthenticationResponse;
import com.doctopl.authenticationservice.payload.LoginRequest;
import com.doctopl.authenticationservice.payload.SignUpRequest;
import com.doctopl.authenticationservice.repository.RoleRepository;
import com.doctopl.authenticationservice.repository.UtilisateurRepository;
import com.doctopl.authenticationservice.security.JwtTokenProvider;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UtilisateurRepository utilisateurRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;

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
    	//roleRepository.save(new Role(RoleName.ROLE_USER));
        if(utilisateurRepository.existsByNomUtilisateur(signUpRequest.getUsername())) {
            return new ResponseEntity(new ApiResponse(false, "Nom d'Utilsateur déjà existant!"),
                    HttpStatus.BAD_REQUEST);
        }

        if(utilisateurRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "adresse mail déjà existant!"),
                    HttpStatus.BAD_REQUEST);
        }

        // Creating user's account
        Utilisateur utilisateur = new Utilisateur(
        		signUpRequest.getUsername(),
        		signUpRequest.getName() ,
        		signUpRequest.getPrenom(),
                signUpRequest.getEmail(), 
                signUpRequest.getPassword()
         );

        utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));

        Role userRole = roleRepository.findByNom(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException("Le role de l'utilisateur non renseigné."));

        utilisateur.setRoles(Collections.singleton(userRole));

        Utilisateur result = utilisateurRepository.save(utilisateur);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getNomUtilisateur()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "Utilisateur enregistré avec succès"));
    }
}
