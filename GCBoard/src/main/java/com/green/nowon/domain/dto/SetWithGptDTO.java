package com.green.nowon.domain.dto;

import com.green.nowon.domain.ArgueBoardEntity;
import com.green.nowon.domain.ArgueContentEntity;
import com.green.nowon.domain.MemberEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SetWithGptDTO {
	//gpt 채점,평가를 위한 정보가 담긴 DTO
	
	//이전 주장, 논거
	private String pastArgument;
	private String pastReason;
	
	//현재 주장, 논거
	private String presentArgument;
	private String presentReason;
	
	private MemberEntity mE;
	private ArgueBoardEntity abE;
	
	private String type;
	private ArgueContentEntity pAcE;
}
















