package com.green.nowon.chess.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SocketHelloDTO {
	//1. 맨처음 구독시
	//hello 
	private String requestType;
	
	private String newUser;
	private Long userCount;
	private String chatHello;
	
}
