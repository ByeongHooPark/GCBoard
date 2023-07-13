package com.green.nowon.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ACDetailDTO {
	//주장 상세페이지 정보
	//가져올 목록
	//1.abno : '목록으로' 버튼에 넣을 데이터
	//2.acno >> 상위 acno(상위 주장으로->pno), 해당 acno(comment 쓰기/ 비동기 comment 보기) 
	//3.주장, 논거, 생성일, 닉네임
	//4.comment(2번의 pno)+type
	//5.gpt점수, gpt평가
	
	//1
	private long abno;
	
	//2
	private long acno;
	
	//3
	private String arguments;
	private String reason;
	private String createdDate;
	private String nick;
	
	//4
	private Long pno;
	private String type;
	
	//5
	private int gptScore;
	private String gptEval;
}














