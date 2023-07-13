package com.green.nowon.chess.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SocketSelectPlayerDTO {
	private String requestType;
	private String myNick;
}
