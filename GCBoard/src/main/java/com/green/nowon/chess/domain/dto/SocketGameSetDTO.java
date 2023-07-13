package com.green.nowon.chess.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocketGameSetDTO {
	private String requestType;
	
	private String alertTitle;
	private String alertReason;
	private String resetBoard;
}
