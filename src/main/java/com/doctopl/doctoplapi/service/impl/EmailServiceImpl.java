package com.doctopl.doctoplapi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.doctopl.doctoplapi.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {
	  
    @Autowired
    public JavaMailSender emailSender;
 
    public void sendSimpleMessage(SimpleMailMessage message) {
        emailSender.send(message);
        
    }
}

