package com.doctopl.doctoplapi.response;

import java.util.Date;

import lombok.Getter;

@Getter
public class ErrorMessage {
	
	private Date timestamp;
	private String message;
	private String details;

	public ErrorMessage(){}

	public ErrorMessage(Date timestamp, String message, String details) {
		this.timestamp = timestamp;
		this.message = message;
		this.details = details;
	}

}
