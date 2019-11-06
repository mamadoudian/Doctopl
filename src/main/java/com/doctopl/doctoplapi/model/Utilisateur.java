package com.doctopl.doctoplapi.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.NaturalId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.doctopl.doctoplapi.model.audit.DateAudit;
import com.doctopl.doctoplapi.security.UserPrincipal;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "UTILISATEUR", uniqueConstraints = {
		@UniqueConstraint(columnNames = {
		    "nomUtilisateur"
		}),
        @UniqueConstraint(columnNames = {
            "email"
        })
})
@Data
@AllArgsConstructor
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
    @NotNull
    @NotBlank
	private String prenom;
	
	@NotNull
    @Size(max = 100)
	private String password;
	
	@JsonFormat(pattern="dd-MM-yyyy")
	@NotNull
	private Date dateNaissance;
	
	@Size(max = 50)
	@NotBlank
	private String lieu;
	
	@Size(max = 1)
	@NotBlank
	private String sexe;
	
	//@Enumerated(EnumType.STRING)
	@Size(max = 50)
	private String typeUtilisateur;
	
	@ManyToMany(fetch = FetchType.EAGER)
	private Set<Role> roles = new HashSet<>();
	
    private boolean compteNonExpire;

    private boolean compteNonBloque;

    private boolean credentialsNonExpired;

    private boolean active;
    
    public Utilisateur() {
    	this.compteNonExpire = true;
		this.compteNonBloque = true;
		this.credentialsNonExpired = true;
		this.active = false;
    }
    
	public Utilisateur(String nomUtilisateur, String nom,String prenom,String email,
			String password,Date dateNaissance,String lieu, String sexe,String typeUtilisateur
			) {
		super();
		this.nomUtilisateur = nomUtilisateur;
		this.email = email;
		this.nom = nom;
		this.prenom = prenom;
		this.password = password;
		this.dateNaissance=dateNaissance;
		this.lieu = lieu;
		this.sexe=sexe;
		this.typeUtilisateur=typeUtilisateur;
		this.compteNonExpire = true;
		this.compteNonBloque = true;
		this.credentialsNonExpired = true;
		this.active = false;
	}
	
	public static Utilisateur create(UserPrincipal user) {
		
//        List<Role> authorities = user.getAuthorities().stream().map(role ->
//                new Role(role.getAuthority())))
//        ).collect(Collectors.toList());
//       
		Set<Role> roles = new HashSet<Role>();
		for ( GrantedAuthority autority: user.getAuthorities()) {
			if(autority.getAuthority().equals(RoleName.ROLE_USER.name())) {
				roles.add(new Role(RoleName.ROLE_USER));
			}else {
				roles.add(new Role(RoleName.ROLE_ADMIN));
			}
		}
        return new Utilisateur(
        		user.getId(), 
        		user.getUsername(), 
        		user.getName(), 
        		user.getPrenom(), 
        		user.getEmail(), 
        		user.getPassword(),
        		user.getDateNaissance(),
        		user.getLieu(), 
        		user.getSexe(), 
        		user.getTypeUtilisateur(),
        		roles,
        		user.isCredentialsNonExpired(),
        		user.isAccountNonLocked(),
        		user.isAccountNonExpired(),
        		user.isEnabled()
        	);
    }
}
