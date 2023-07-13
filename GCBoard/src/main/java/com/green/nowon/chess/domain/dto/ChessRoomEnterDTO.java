package com.green.nowon.chess.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ChessRoomEnterDTO {
	//닉네임 만들기 전 임시화면 구성DTO
	
	//진짜 정보
	private long crno;
	private String title;
	
	//임시로 뿌릴 정보들
	private long count;
	private String state;
	
	private String whiteNick;
	private String blackNick;
}
