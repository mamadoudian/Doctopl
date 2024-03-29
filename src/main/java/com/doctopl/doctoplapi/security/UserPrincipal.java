package com.doctopl.doctoplapi.security;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.doctopl.doctoplapi.model.Utilisateur;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;

@Getter
public class UserPrincipal implements UserDetails{
	
	private Long id;

    private String name;

    private String username;

    @JsonIgnore
    private String email;

    @JsonIgnore
    private String password;
    
	private String prenom;
	
	private Date dateNaissance;
	
	private String lieu;
	
	private String sexe;
	
	private String typeUtilisateur;
    
    private Collection<? extends GrantedAuthority> authorities;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
	
	public Long getId() {
        return id;
    }

	
	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
	 public UserPrincipal(Long id, String name, String username,String prenom, String email, String password,Date dateNaissance,String lieu, String sexe,String typeUtilisateur, Collection<? extends GrantedAuthority> authorities) {
	        this.id = id;
	        this.name = name;
	        this.prenom=prenom;
	        this.username = username;
	        this.email = email;
	        this.password = password;
	        this.dateNaissance=dateNaissance;
	        this.lieu=lieu;
	        this.sexe=sexe;
	        this.typeUtilisateur=typeUtilisateur;
	        this.authorities = authorities;
	    }

	    public static UserPrincipal create(Utilisateur user) {
	        List<GrantedAuthority> authorities = user.getRoles().stream().map(role ->
	                new SimpleGrantedAuthority(role.getNom().name())
	        ).collect(Collectors.toList());

	        return new UserPrincipal(
	                user.getId(),
	                user.getNom(),
	                user.getPrenom(),
	                user.getNomUtilisateur(),
	                user.getEmail(),
	                user.getPassword(),
	                user.getDateNaissance(),
	                user.getLieu(),
	                user.getSexe(),
	                user.getTypeUtilisateur(),
	                authorities
	        );
	    }

	    @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (o == null || getClass() != o.getClass()) return false;
	        UserPrincipal that = (UserPrincipal) o;
	        return Objects.equals(id, that.id);
	    }

	    @Override
	    public int hashCode() {
	        return Objects.hash(id);
	    }

	    
	    

}
