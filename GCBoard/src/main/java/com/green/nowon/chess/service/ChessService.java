package com.green.nowon.chess.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;


public interface ChessService {

	void goMain(Model model);

	ResponseEntity<Long> createRoom(String title);

	void goRoom(long crno, Model model);

	ResponseEntity<Map<String, String>> randomNick(Long crno);

	ResponseEntity<String> startGame(Long crno, String mynick);

	ResponseEntity<String> selectObject(Long crno, String mynick);

	ResponseEntity<Map<String, String>> reGame(Long crno);

}
