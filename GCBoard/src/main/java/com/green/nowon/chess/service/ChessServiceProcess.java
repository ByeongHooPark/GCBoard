package com.green.nowon.chess.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.nowon.chess.domain.ChessNickEntity;
import com.green.nowon.chess.domain.ChessNickRepo;
import com.green.nowon.chess.domain.ChessRoomEntity;
import com.green.nowon.chess.domain.ChessRoomRepo;
import com.green.nowon.chess.domain.dto.ChessListDTO;
import com.green.nowon.chess.domain.dto.ChessRoomEnterDTO;
import com.green.nowon.service.ProjectServiceUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ChessServiceProcess implements ChessService {
	
	private final ChessRoomRepo chessRoomRepo;
	private final ChessNickRepo chessNickRepo;
	private final ProjectServiceUtil pUtil;
	
	//체스리스트방으로 이동
	@Override
	public void goMain(Model model) {
		
		List<ChessRoomEntity> crEList = chessRoomRepo.findAll();
		List<ChessListDTO> dtoList = new ArrayList<>();
		
		for(ChessRoomEntity crE : crEList) {
			dtoList.add(ChessListDTO.builder()
									.crno(crE.getCrno())
									.title(crE.getTitle())
									.count(crE.getCount())
									.state(crE.getState())
									.build());
		}
		model.addAttribute("list", dtoList);
	}

	//비동기 - 방만들기 : 방 테이블 만들고 방 번호 보내기
	@Override
	public ResponseEntity<Long> createRoom(String title) {
		
		//1. 체스방 테이블 저장
		ChessRoomEntity crE = ChessRoomEntity.builder()
										.title(title)
										.state("대기")
										.currentBackUp("[[\"r\",\"n\",\"b\",\"q\",\"k\",\"b\",\"n\",\"r\"],[\"p\",\"p\",\"p\",\"p\",\"p\",\"p\",\"p\",\"p\"],[\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"],[\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"],[\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"],[\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"],[\"P\",\"P\",\"P\",\"P\",\"P\",\"P\",\"P\",\"P\"],[\"R\",\"N\",\"B\",\"Q\",\"K\",\"B\",\"N\",\"R\"]]")
										.whiteNick("미정")
										.blackNick("미정")
										.build();
		chessRoomRepo.save(crE);
		
		return ResponseEntity.ok().body(crE.getCrno());
	}
	
	//게임방 새창으로 열기
	//임시로 보일 정보들을 뿌린다
	@Override
	public void goRoom(long crno, Model model) {
		
		ChessRoomEntity crE = chessRoomRepo.findById(crno).get();
		
		model.addAttribute("dto", ChessRoomEnterDTO.builder()
												.crno(crno)
												.title(crE.getTitle())
												.count(0)
												.state("대기")
												.whiteNick("미정")
												.blackNick("미정")
												.build());
	}

/***** 체스방 비동기 메서드들 *****/
	
	//비동기 - 랜덤 닉네임 생성
	@Override
	public ResponseEntity<Map<String, String>> randomNick(Long crno) {

		ChessRoomEntity crE = chessRoomRepo.findById(crno).get();
		
		//1.닉네임 생성
		String nomalNick = pUtil.randomNickname();
		String encodedNick = nomalNick+UUID.randomUUID().toString();
		
		chessNickRepo.save(ChessNickEntity.builder()
								.nomalNick(nomalNick)
								.encodedNick(encodedNick)
								.chessRoom(crE)
								.build());
		
		//2. ChessRoomEntity 업데이트(구독자수++)
		crE.setCount(crE.getCount()+1);
		chessRoomRepo.save(crE);
		
		//3. 가져갈 목록(1. ChessRoomEntity : count, state, whiteNick, blackNick, currentBackUp
					//2. ChessNickEntity : 내 닉들,  구독자 전부 nomalNick)
		Map<String, String> mapList = new HashMap<>();

		mapList.put("count", Long.toString(crE.getCount()));
		mapList.put("state", crE.getState());
		mapList.put("whiteNick", crE.getWhiteNick());
		mapList.put("blackNick", crE.getBlackNick());
		mapList.put("currentBackUp", crE.getCurrentBackUp());
		
		mapList.put("myNomalNick", nomalNick);
		mapList.put("myEncodedNick", encodedNick);
		
		//구독자들 정보. 직렬화 떄문에 이렇게 따로 가져감
		List<ChessNickEntity> cnEList = chessNickRepo.findByChessRoom(crE);
		List<Long> cnnoList = new ArrayList<>();
		List<String> nickList = new ArrayList<>();
		for(ChessNickEntity cnE : cnEList) {
			cnnoList.add(cnE.getCnno());
			nickList.add(cnE.getNomalNick());
		}
		
		 ObjectMapper mapper = new ObjectMapper();
		 String json = null;
	        try {
	            json = mapper.writeValueAsString(nickList.toString());
	        } catch (JsonProcessingException e) {
	            e.printStackTrace();
	        }
		
		mapList.put("cnnoList", cnnoList.toString());
		mapList.put("nickList", json);
		
		return ResponseEntity.ok().body(mapList);
	}

	//비동기 - 게임 시작
	@Override
	public ResponseEntity<String> startGame(Long crno, String mynick) {
		
		//테이블 업데이트
		ChessRoomEntity crE = chessRoomRepo.findById(crno).get();
		crE.setState("게임중");
		chessRoomRepo.save(crE);
		
		//닉네임 디코드
		String nomalNick = chessNickRepo.findByEncodedNick(mynick).getNomalNick();
		
		
		//플레이어 여부(white, black, observer)
		String flag = null;
		if(crE.getWhiteNick().equals(nomalNick)) {
			flag = "white";
		} else if(crE.getBlackNick().equals(nomalNick)) {
			flag = "black";
		} else {
			flag = "observer";
		}
		
		return ResponseEntity.ok().body(flag);
	}
	
	
	//비동기 - 말 선택
	@Override
	public ResponseEntity<String> selectObject(Long crno, String mynick) {
		
		ChessRoomEntity crE = chessRoomRepo.findById(crno).get();
		
		//닉네임 디코드
		String nomalNick = chessNickRepo.findByEncodedNick(mynick).getNomalNick();
		
		String flag = null;
		
		if(nomalNick.equals(crE.getBlackNick())) {
			flag = "black";
		} else {
			flag = "white";
		}
		
		return ResponseEntity.ok().body(flag);
	}
	
	//비동기 - 게임 재개
	@Override
	public ResponseEntity<Map<String, String>> reGame(Long crno) {
		
		ChessRoomEntity crE = chessRoomRepo.findById(crno).get();
		
		Map<String, String> mapList = new HashMap<>();

		mapList.put("state", crE.getState());
		mapList.put("whiteNick", crE.getWhiteNick());
		mapList.put("blackNick", crE.getBlackNick());
		mapList.put("currentBackUp", crE.getCurrentBackUp());
		
		return  ResponseEntity.ok().body(mapList);
	}
}




























