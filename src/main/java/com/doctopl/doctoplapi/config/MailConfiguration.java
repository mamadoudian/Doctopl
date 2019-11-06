package com.doctopl.doctoplapi.config;

import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.doctopl.doctoplapi.properties.MailConfigurationProperties;

@ConfigurationProperties(prefix = "notification")
@Configuration
public class MailConfiguration {
	
	@Autowired
	MailConfigurationProperties mailConfigurationProperties;
	
	protected Logger logger = Logger.getLogger(MailConfiguration.class.getName());

	@Bean
	public JavaMailSender getJavaMailSender() {
	    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

	    
	    mailSender.setHost(mailConfigurationProperties.getHost());
	    mailSender.setPort(mailConfigurationProperties.getPort());
	     
	    mailSender.setUsername(mailConfigurationProperties.getUsername());
	    mailSender.setPassword(mailConfigurationProperties.getPassword());
	     
	    Properties props = mailSender.getJavaMailProperties();
	    props.put("mail.transport.protocol", "smtp");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.debug", "true");
	     
	    return mailSender;
	}

}
