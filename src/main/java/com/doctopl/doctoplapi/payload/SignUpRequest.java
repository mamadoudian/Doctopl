package com.doctopl.doctoplapi.payload;

import java.util.Date;

import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class SignUpRequest {
	
	@NotBlank
	@Size( max = 40)
	private String name;
	
	@Size( max = 70)
	@NotBlank
	private String prenom;
	
	@NotBlank
	@Size(min = 3, max = 15)
	private String username;
	
	@NotBlank
	@Size(max = 40)
	@Email
	private String email;
	
	@NotBlank
	@Size(min = 6, max = 20)
	private String password;
	
	@JsonFormat(pattern="dd-MM-yyyy")
	@NotNull
	private Date dateNaissance;
	
	@Size(max = 50)
	@NotNull
	private String lieu;
	
	@Size(max = 1)
	@NotBlank
	private String sexe;
	
	@NotBlank
	@Size(max = 50)
	private String typeUtilisateur;

}
