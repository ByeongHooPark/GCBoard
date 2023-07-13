package com.green.nowon.chess.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ChessListDTO {
	//체스 메인 페이지로 갈때의 DTO
	private long crno;
	private String title;
	private long count;
	private String state;
}
