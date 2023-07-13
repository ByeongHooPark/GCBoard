package com.green.nowon.chess.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SocketSendMsgDTO {
	//2. 메세지 보낼때
	//msg 
	private String requestType;
	
	private String myNick;
	private String sendMassage;
}
