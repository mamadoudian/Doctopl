package com.doctopl.doctoplapi;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@SpringBootApplication
@EntityScan(basePackageClasses = { 
		DoctoplApiApplication.class,
		Jsr310JpaConverters.class 
})

public class DoctoplApiApplication {
	
	@PostConstruct
	void init() {
		//TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));
		System.out.println("Date Instant: " + Instant.now().toString());
		System.out.println("Date Date: " +new Date().toString());
		System.out.println("Date DateTime: " +LocalDateTime.now().toString());
	}

	public static void main(String[] args) {
		SpringApplication.run(DoctoplApiApplication.class, args);
	}
}
