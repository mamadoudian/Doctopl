package com.doctopl.authenticationservice.payload;

import javax.validation.constraints.*;

import lombok.Data;

@Data
public class SignUpRequest {
	@NotBlank
	@Size(min = 4, max = 40)
	private String name;
	@Size(min = 4, max = 70)
	private String prenom;
	@NotBlank
	@Size(min = 3, max = 15)
	private String username;

	@NotBlank
	@Size(max = 40)
	@Email
	private String email;

	@NotBlank
	@Size(min = 6, max = 20)
	private String password;

}
