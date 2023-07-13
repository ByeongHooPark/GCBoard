package com.green.nowon.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoCreateACDTO {
	//주장 생성 페이지 이동시 필요한 dto
	private long abno;
	
	private String nick;
	private String parent;
	private String type;
	
	private String parentArguments;
	private String parentReasons;
	
}
