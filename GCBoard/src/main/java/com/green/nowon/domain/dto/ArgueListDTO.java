package com.green.nowon.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ArgueListDTO {
	//메인으로 뿌리는 토론 정보. createdDate가 String인것 주의
	private long abno;
	private String title;
	private String createdDate;

}
