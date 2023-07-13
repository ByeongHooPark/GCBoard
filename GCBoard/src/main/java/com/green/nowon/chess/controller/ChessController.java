package com.green.nowon.chess.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.green.nowon.chess.service.ChessService;
import com.green.nowon.chess.service.ChessServiceProcess;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Controller
@RequestMapping("/chess")
public class ChessController {
	
	private final ChessService service;
	
	//체스 메인페이지로 이동(+체스 방 목록 가져오기)
	@GetMapping("/main")
	public String goMain(Model model) {
		service.goMain(model);
		return "chess/main";
	}
	
	//체스방 생성 페이지로 이동
	@GetMapping("/create")
	public String goCreate() {
		return "chess/create-room";
	}
	
	//비동기 - 방만들기 : 방 테이블 만들고 방 번호 보내기
	@ResponseBody
	@PostMapping("/create")
	public ResponseEntity<Long> createRoom(String title){
		return service.createRoom(title);
	}
	
	//게임방 새창으로 열기
	@GetMapping("/room/{crno}")
	public String goRoom(@PathVariable long crno, Model model) {
		service.goRoom(crno, model);
		return "chess/game-room";
	}
	
/***** 체스방 비동기 메서드 들 *****/
	//랜덤 닉네임 생성 1. 닉네임 생성, 2. 데이터 가져오기
	@ResponseBody
	@PostMapping("/enter")
	public ResponseEntity<Map<String,String>> randomNick(Long crno){
		return service.randomNick(crno);
	}
	
	//게임 시작. 1. 테이블 업데이트, 2. 플레이어 여부(white, black, observer)
	@ResponseBody
	@PostMapping("/start")
	public ResponseEntity<String> startGame(Long crno, String mynick){
		return service.startGame(crno, mynick);
	}
	
	//기물 선택(민트색 가능 영역)
	@ResponseBody
	@GetMapping("/select-object")
	public ResponseEntity<String> selectObject(Long crno, String mynick){
		return service.selectObject(crno, mynick);
	}
	
	//게임 재개
	@ResponseBody
	@GetMapping("/regame")
	public ResponseEntity<Map<String,String>> reGame(Long crno){
		return service.reGame(crno);
	}
	
}


















