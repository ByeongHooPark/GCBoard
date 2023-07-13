package com.green.nowon.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ACBoardDTO {
	
	private long abno;
	private String title;
	private String detail;
	private String nick;

}
