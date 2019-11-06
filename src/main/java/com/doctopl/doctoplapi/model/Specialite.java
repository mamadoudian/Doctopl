package com.doctopl.doctoplapi.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Entity
@Data
public class Specialite {
	
	@Id
	private String id;
	@NotNull
	@NotNull
	@Size(max=15)
	private String libelle;
	@Size(max=200)
	private String description;
	
}
