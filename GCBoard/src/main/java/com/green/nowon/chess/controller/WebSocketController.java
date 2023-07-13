package com.green.nowon.chess.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.green.nowon.chess.domain.ChessNickEntity;
import com.green.nowon.chess.domain.ChessNickRepo;
import com.green.nowon.chess.domain.ChessRoomEntity;
import com.green.nowon.chess.domain.ChessRoomRepo;
import com.green.nowon.chess.domain.dto.SocketSendMsgDTO;

import jakarta.transaction.Transactional;

import com.green.nowon.chess.domain.dto.ByebyeDTO;
import com.green.nowon.chess.domain.dto.SocketGameSetDTO;
import com.green.nowon.chess.domain.dto.SocketHelloDTO;
import com.green.nowon.chess.domain.dto.SocketPutObjectDTO;
import com.green.nowon.chess.domain.dto.SocketSelectPlayerDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class WebSocketController {
	
	private final ChessNickRepo chessNickRepo;
	private final ChessRoomRepo chessRoomRepo;
	
	//1. 접속
	@MessageMapping("/hello/{crno}")
	@SendTo("/topic/chess/{crno}")
	public SocketHelloDTO helloMsg(@DestinationVariable String crno, SocketHelloDTO dto) {
	    System.out.println("구독 >>>> " + crno);
	    return dto;
	}

	//2. 메세지 보내기
	@MessageMapping("/sendMsg/{crno}")
	@SendTo("/topic/chess/{crno}")
	public SocketSendMsgDTO sendMsg(@DestinationVariable String crno, SocketSendMsgDTO dto) {
		
		//닉네임 디코드
		dto.setMyNick(chessNickRepo.findByEncodedNick(dto.getMyNick()).getNomalNick());
		return dto;
	}
	
	//3.플레이어 정하기
	@MessageMapping("/select-player/{crno}")
	@SendTo("/topic/chess/{crno}")
	public SocketSelectPlayerDTO selectPlayerMsg(@DestinationVariable String crno, SocketSelectPlayerDTO dto) {
		
		//닉네임 디코드
		String nomalNick = chessNickRepo.findByEncodedNick(dto.getMyNick()).getNomalNick();
		dto.setMyNick(nomalNick);
		ChessRoomEntity crE = chessRoomRepo.findById(Long.parseLong(crno)).get();
		
		//DB에 플레이어 업데이트
		if(dto.getRequestType().equals("white")) {
			crE.setWhiteNick(nomalNick);
		} else {
			crE.setBlackNick(nomalNick);
		}
		chessRoomRepo.save(crE);
		
		return dto;
	}
	//4.기물 놓기
	@MessageMapping("/putObject/{crno}")
	@SendTo("/topic/chess/{crno}")
	public SocketPutObjectDTO putObject(@DestinationVariable String crno, SocketPutObjectDTO dto) {
		ChessRoomEntity crE = chessRoomRepo.findById(Long.parseLong(crno)).get();
		
		//다음 둘 사람 정하기
		if(dto.getPlayerNick().equals(crE.getBlackNick())) {
			dto.setPlayerNick(crE.getWhiteNick());
		} else if (dto.getPlayerNick().equals(crE.getWhiteNick())) {
			dto.setPlayerNick(crE.getBlackNick());
		}
		
		//보드판 업데이트
		crE.setCurrentBackUp(dto.getCurrentBackUp());
		chessRoomRepo.save(crE);
		
		return dto;
	}
	
	//5. 게임 종료
	@MessageMapping("/gameSet/{crno}")
	@SendTo("/topic/chess/{crno}")
	public synchronized  SocketGameSetDTO gameSet(@DestinationVariable String crno, SocketGameSetDTO dto) {
		System.out.println("=============================================================");
		ChessRoomEntity crE = chessRoomRepo.findById(Long.parseLong(crno)).get();
		System.out.println(crE.toString());
		
		//보드판 업데이트(초기화) - 잘 몰라서 그때그때함..
		crE.setCurrentBackUp(dto.getResetBoard());
		chessRoomRepo.save(crE);
		crE.setWhiteNick("미정");
		chessRoomRepo.save(crE);
		crE.setBlackNick("미정");
		chessRoomRepo.save(crE);
		crE.setState("대기");
		chessRoomRepo.save(crE);
		
		System.out.println("게임 종료게임 종료게임 종료게임 종료게임 종료");
		
		return dto;
	}
	
	//6. 방 나가기
	@MessageMapping("/byebye/{crno}")
	@SendTo("/topic/chess/{crno}")
	public synchronized  ByebyeDTO byebye(@DestinationVariable String crno, ByebyeDTO dto) {
		
		System.out.println("방 나가기방 나가기방 나가기방 나가기방 나가기방 나가기방 나가기방 나가기방 나가기방 나가기");
		
		ChessRoomEntity crE = chessRoomRepo.findById(Long.parseLong(crno)).get();
		ChessNickEntity cnE = chessNickRepo.findByEncodedNick(dto.getTargetNick());
		
		long nowCount = crE.getCount()-1;
		crE.setCount(nowCount);
		chessRoomRepo.save(crE);
		
		//1.방에 남은 사람이 없으면
		if(nowCount == 0) {
			dto.setTargetNick(cnE.getNomalNick());
			chessNickRepo.delete(cnE);
			chessRoomRepo.deleteById(Long.parseLong(crno));
		//2.남은 사람이 있으면
		} else {
			//닉네임 엔티티에서 row삭제
			dto.setTargetNick(cnE.getNomalNick());
			chessNickRepo.delete(cnE);
		}
		
		System.out.println("방 나가기 끝까지 수행!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		return dto;
	}
}














