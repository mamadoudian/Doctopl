package com.doctopl.doctoplapi.payload;

import lombok.Data;

@Data
public class ApiResponse {
	
	private Boolean success;
    private String message;
    
    public ApiResponse(boolean success, String message) {
		this.success=success;
		this.message=message;
	}

}
