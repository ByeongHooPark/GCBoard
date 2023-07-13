package com.green.nowon.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CreateArgueDTO {
	
////토론주제 만들때 정보를 받는 DTO////
	
	/* 토론 게시판 */
	private String title;
	private String detail;
	private long parentNo;
	
	/* 토론 내용 */
	private String arguements;
	private String reason;
}
