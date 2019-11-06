package com.doctopl.doctoplapi.service;

import org.springframework.mail.SimpleMailMessage;

public interface EmailService {
	public void sendSimpleMessage(SimpleMailMessage message);
}
