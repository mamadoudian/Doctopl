package com.doctopl.doctoplapi.model.audit;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;


@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(
        value = {"dateCreation", "dateModification"},
        allowGetters = true
)
@Data
public abstract class DateAudit implements Serializable{
	
	@CreatedDate
    @Column(nullable = false, updatable = false)
	private Instant dateCreation;
	
	@LastModifiedDate
    @Column(nullable = false)
	private Instant dateModification;
	
	
}
