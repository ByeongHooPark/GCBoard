package com.green.nowon.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MemberRole {
	USER("ROLE_USER","-1"),
	ADMIN("ROLE_ADMIN","-1"),
	BRONZE("ROLE_BRONZE","0"),//count: 0~9, score: 0~19
	SILVER("ROLE_SILVER","20"),//count: 10~19, score: 20~39
	GOLD("ROLE_GOLD","40"),//count: 20~29, score: 40~59
	PLATINUM("ROLE_PLATINUM","60"),//count: 30~39, score: 60~79
	DIAMOND("ROLE_DIAMOND","80");//count: 40이상, score: 80~100

	private final String roleName;
	private final String scoreName;
	
	public final String roleName() {
		return roleName;
	}
	
	public final String scoreName() {
		return scoreName;
	}
}

