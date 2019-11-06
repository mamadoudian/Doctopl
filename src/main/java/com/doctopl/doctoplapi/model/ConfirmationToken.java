package com.doctopl.doctoplapi.model;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.doctopl.doctoplapi.model.audit.DateAudit;

import lombok.Data;

@Entity
@Data
public class ConfirmationToken extends DateAudit{
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long tokenid;

    @Column(name="confirmation_token")
    private String confirmationToken;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "utilisateur_id")
    private Utilisateur utilisateur;
    
    public ConfirmationToken() {
    	
    }

    public ConfirmationToken(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
 
        confirmationToken = UUID.randomUUID().toString();
        
        
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
