package com.doctopl.doctoplapi.request;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.doctopl.doctoplapi.model.Specialite;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ProfessionRequest {
	
	@NotBlank
	@NotNull
    @Size(max = 40)
	private String nom;
	
    @Size(max = 70)
	private String prenom;
	
	@NotBlank
	@NotNull
    @Size(max = 100)
	private String password;
	
	@NotBlank
	@NotNull
	@JsonFormat(pattern="dd-MM-yyyy")
	private Date dateNaissance;
	
	@NotBlank
	@NotNull
	private String lieuNaissance;
	
	@NotBlank
	@NotNull
	@Size(max = 120)
	private String adressePersonelle;
	
	@NotBlank
	@NotNull
	@Size(min=9,max = 10)
	private String telephone;
	
	@Size(max = 20)
	private String pays;
	
	@Size(max = 20)
	private String ville;
	
	@Size(max=20)
	private String adresseProfessionel;
	
	private double tarif;
	
	private Set<Specialite> specialites=new HashSet<>();
	
	@Size(max=200)
	private String presentation;
	
	@Size(max=70)
	private String moyenPaiement;
}
