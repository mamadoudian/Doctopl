package com.doctopl.doctoplapi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.NaturalId;

import lombok.Data;

@Data
@Entity
public class Role {
	
	@Enumerated(EnumType.STRING)
	@Id
    @Column(length = 60)
	private RoleName nom;
	
	public Role() {

    }

    public Role(RoleName nom) {
        this.nom = nom;
    }


}
