package com.doctopl.doctoplapi.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class PasswordRequest {
	
	@NotBlank
	@Size(min = 6, max = 20)
	private String password;
	@NotBlank
	private String token;
	
	public PasswordRequest() {}
	
	public PasswordRequest(String password) {
		this.password=password;
	}
	
	public PasswordRequest(String password,String token) {
		this.password=password;
		this.token=token;
	}
}
