package com.green.nowon.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.green.nowon.domain.dto.SignUpFormDTO;
import com.green.nowon.service.SignService;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Controller
public class SignController {
	

	private final SignService service;
	
	//로그인 페이지 이동
	@GetMapping("/signin")
	public String signin(){
		return "sign/signin";
	}
	
	//회원가입 페이지 이동
	@GetMapping("/signup")
	public String signup(){
		return "sign/signup";
	}
	
	//비동기-인증메일 보내기
	@ResponseBody
	@PostMapping("/signup/email")
	public void  mailSend(String toEmail) {
		service.mailSend(toEmail);
	}
	
	//비동기-인증번호 검사하기. 성공 : 0 , 실패 : 1
	@ResponseBody
	@PostMapping("/signup/check")
	public ResponseEntity<Integer> ahthCheck(String email,String code) {
		return service.ahthCheck(email,code);
	}
	
	//회원가입
	@PostMapping("/signup")
	public String signup(SignUpFormDTO dto) {
		
		service.signup(dto);
		
		return "redirect:/signin";
	}
}

















