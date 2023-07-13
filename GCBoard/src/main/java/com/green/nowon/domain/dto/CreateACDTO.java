package com.green.nowon.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class CreateACDTO {
	
	private Long abno;
	private String nick;
	
	private Long parent;
	private String type;

	//이전 주장, 논거
	private String pastArgument;
	private String pastReason;
	
	//현재 주장, 논거
	private String presentArgument;
	private String presentReason;
	
}
