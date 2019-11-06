package com.doctopl.doctoplapi.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doctopl.doctoplapi.repository.ProfessionelRepository;
import com.doctopl.doctoplapi.request.ProfessionRequest;
import com.doctopl.doctoplapi.response.ProfessionelResponse;

@RestController
@RequestMapping("/api/professionel")
public class ProfessionelController {
	@Autowired
	ProfessionelRepository professionelRepository;
	
	@PutMapping("/{username}")
	ProfessionelResponse update(@PathVariable String username, @RequestBody ProfessionRequest professionelRequest){
		return new ProfessionelResponse();
	}
}
