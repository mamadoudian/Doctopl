package com.doctopl.authenticationservice.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.NaturalId;

import com.doctopl.authenticationservice.model.audit.DateAudit;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "UTILISATEUR", uniqueConstraints = {
		@UniqueConstraint(columnNames = {
		    "nomUtilisateur"
		}),
        @UniqueConstraint(columnNames = {
            "email"
        })
})
@Data
@Getter @Setter
public class Utilisateur extends DateAudit{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotBlank
    @Size(max = 15)
	private String nomUtilisateur;
	
	@NaturalId
    @NotBlank
    @Size(max = 40)
    @Email
	private String email;
	
	@NotBlank
    @Size(max = 40)
	private String nom;
	
    @Size(max = 70)
	private String prenom;
	
	@NotBlank
    @Size(max = 100)
	private String password;
	
	@ManyToMany(fetch = FetchType.LAZY)
	private Set<Role> roles = new HashSet<>();
	
    private boolean compteNonExpire;

    private boolean compteNonBloque;

    private boolean credentialsNonExpired;

    private boolean active;
    
    public Utilisateur() {
    	
    }
    
	public Utilisateur(String nomUtilisateur, String nom,String prenom,String email,
			String password) {
		super();
		this.nomUtilisateur = nomUtilisateur;
		this.email = email;
		this.nom = nom;
		this.prenom = prenom;
		this.password = password;
		this.compteNonExpire = true;
		this.compteNonBloque = true;
		this.credentialsNonExpired = true;
		this.active = true;
	}
	
	
	
}
