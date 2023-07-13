package com.green.nowon.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ACContentDTO {
	
	//'acno번. arguments'
	private long acno;
	private String arguments;
	
	//닉네임
	private String nick;
	
	//'parent의 no번 주장 대한 type'
	private String pno;
	private String type;
	
	//'gptScore'점
	private int gptScore;
	
	//찬성, 반대, 중립의 수
	private long agree;
	private long opposite;
	private long neutrality;

}
