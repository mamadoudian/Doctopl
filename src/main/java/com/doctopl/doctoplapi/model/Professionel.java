package com.doctopl.doctoplapi.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Entity
@Data
public class Professionel{
	
	@Id
	private Long id;
	@Size(max=20)
	private String adresseProfessionel;

	@Size(max = 120)
	private String adressePersonelle;
	
	@Size(min=9,max = 10)
	private String telephone;
	
	@Size(max = 20)
	private String pays;
	
	@Size(max = 20)
	private String ville;
	
	private double tarif;
	
	@ManyToMany(fetch = FetchType.LAZY)
	private Set<Specialite> specialites=new HashSet<>();
	
	@Size(max=200)
	private String presentation;
	
	@Size(max=70)
	private String moyenPaiement;
	
	public Professionel() {
		super();
	}
}
