package com.green.nowon.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.green.nowon.domain.dto.SignUpFormDTO;

public interface SignService {

	void mailSend(String toEmail);

	ResponseEntity<Integer> ahthCheck(String email,String code);
	void signup(SignUpFormDTO dto);
	
}
