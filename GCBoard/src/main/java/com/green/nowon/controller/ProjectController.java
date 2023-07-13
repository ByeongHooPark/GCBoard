package com.green.nowon.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.green.nowon.domain.dto.CreateACDTO;
import com.green.nowon.domain.dto.CreateArgueDTO;
import com.green.nowon.service.ProjectService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class ProjectController {
	private final ProjectService service;
	
	
//////토론페이지 관련////////
	
	//토론 메인페이지 이동
	@GetMapping("/argue")
	public String argueMain(Model model) {
		service.mainArgueList(model);

		return "argue/argue-main";
	}
	
	//토론 생성페이지로 이동
	@GetMapping("/argue/new-subject/{no}")
	public String argueNewSubject(@PathVariable long no) {
		return "argue/create-subject";
	}
	
	//토론 주제 생성 >> gpt 채점페이지로 이동
	@PostMapping("/argue/new-subject")
	public String argueNewSubject(CreateArgueDTO dto, Principal principal) {
		;
		
		return service.argueNewSubject(dto,principal);
//		return "redirect:/argue/gpt-scoring"+acno;
	}
	//gpt 채점페이지
	@GetMapping("/argue/gpt-scoring/{acno}")
	public String gptScore(@PathVariable long acno, Model model) {
		service.gptScore(acno, model);
		
		return "argue/gpt-scoring";
	}
	
	
	//토론 주제(주장 리스트)로 이동
	@GetMapping("/argue/board/{abno}")
	public String goArgueBoard(@PathVariable long abno, Model model, Principal principal) {
		service.goArgueBoard(abno, model, principal);
		
		return "argue/argue-content";
	}
	
	//토론 주장 상세 페이지로 이동
	@GetMapping("/argue/conetent/{acno}")
	public String goArgueContent(@PathVariable long acno, Model model) {
		service.goArgueContent(acno, model);
//		토론 주제 abno도 가져와야한다! >> '목록보기' 때문에!!
		return "argue/argue-content-detail"; 
	}
	
	
	////개인 정보 관련////
	@GetMapping("/info")
	public String goInfo(Model model, Principal principal) {
		
		service.goInfo(model,principal);
		
		return "info/individual-info";
	}
	
	
	//// 랜덤 주제 뽑기 : 토론 주제 만들기 페이지////
	@GetMapping("/argue/random")
	public ResponseEntity<Map<String, String>> getRandomData() {
				
	    return service.getRandomData();
	}
	
	////새로운 주장 생성 페이지로 이동////
	@GetMapping("/argue/content/write")
	public String writeArgueContent(@RequestParam String abno, 
									@RequestParam(defaultValue = "없음") String parent,
									@RequestParam(defaultValue = "없음") String type,
									Model model, Principal principal) {
		
		service.goWriteArgueContent(abno,parent,type,model,principal);

		return "argue/create-content";
	}
	
	
	////새로운 주장 생성////
	@PostMapping("/argue/content/write")
	public String createArgueContent(CreateACDTO dto, Principal principal) {
		
		return service.createArgueContent(dto, principal);
	}
}


