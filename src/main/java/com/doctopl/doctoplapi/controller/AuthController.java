package com.doctopl.doctoplapi.controller;

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

import com.doctopl.doctoplapi.exception.AppException;
import com.doctopl.doctoplapi.model.Professionel;
import com.doctopl.doctoplapi.model.Role;
import com.doctopl.doctoplapi.model.RoleName;
import com.doctopl.doctoplapi.model.Utilisateur;
import com.doctopl.doctoplapi.payload.ApiResponse;
import com.doctopl.doctoplapi.payload.JwtAuthenticationResponse;
import com.doctopl.doctoplapi.payload.LoginRequest;
import com.doctopl.doctoplapi.payload.SignUpRequest;
import com.doctopl.doctoplapi.repository.ProfessionelRepository;
import com.doctopl.doctoplapi.repository.RoleRepository;
import com.doctopl.doctoplapi.repository.UtilisateurRepository;
import com.doctopl.doctoplapi.security.JwtTokenProvider;

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
    ProfessionelRepository professionelRepository;

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
  
        if(utilisateurRepository.existsByNomUtilisateur(signUpRequest.getUsername())) {
            return new ResponseEntity(new ApiResponse(false, "Nom d'Utilsateur déjà existant!"),
                    HttpStatus.BAD_REQUEST);
        }

        if(utilisateurRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "adresse mail déjà existant!"),
                    HttpStatus.BAD_REQUEST);
        }
        
        //creation d'un compte professionnel
        Professionel professionel =  new Professionel();
        professionel.setNomUtilisateur(signUpRequest.getUsername());
        professionel.setNom(signUpRequest.getName());
        professionel.setPrenom(signUpRequest.getPrenom());
        professionel.setEmail(signUpRequest.getEmail());
        professionel.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        Role userRole = roleRepository.findByNom(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException("Le role de l'utilisateur non renseigné."));

        professionel.setRoles(Collections.singleton(userRole));
        Professionel result = professionelRepository.save(professionel);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getNomUtilisateur()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "Utilisateur enregistré avec succès"));
    }
}
