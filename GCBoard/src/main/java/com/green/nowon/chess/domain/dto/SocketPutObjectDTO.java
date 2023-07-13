package com.green.nowon.chess.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SocketPutObjectDTO {
	//4. 플레이어 말 옮기기
	private String requestType;
	
	private String currentBackUp;//
	private String playerNick;
}
