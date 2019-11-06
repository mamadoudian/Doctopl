package com.doctopl.doctoplapi.model;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.doctopl.doctoplapi.model.audit.DateAudit;

import lombok.Data;

@Entity
@Data
public class PasswordResetToken extends DateAudit{
  
    private static final int EXPIRATION = 60 * 24;
  
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
  
    private String passwordResetToken;
  
    @OneToOne(targetEntity = Utilisateur.class, fetch = FetchType.EAGER)
    private Utilisateur utilisateur;
  
    public PasswordResetToken() {
    	
    }

    public PasswordResetToken(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        passwordResetToken = UUID.randomUUID().toString();
    }
    
    private Date calculateExpiryDate(int expiryTimeInMinutes) {
    	ZonedDateTime zdt = ZonedDateTime.ofInstant(getDateModification(), ZoneId.systemDefault());
        Calendar cal = GregorianCalendar.from(zdt);
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        
        return new Date(cal.getTime().getTime());
    }
    
    public boolean isExpiryDate(int expiryTimeInMinutes) {
    	
    	Date expiryDate = calculateExpiryDate(expiryTimeInMinutes);
    	Date now = new Date();
    	
    	return expiryDate.before(now);
    }
}