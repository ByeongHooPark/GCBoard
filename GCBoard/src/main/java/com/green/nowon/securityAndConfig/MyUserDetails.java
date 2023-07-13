package com.green.nowon.securityAndConfig;

import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.green.nowon.domain.MemberEntity;

import lombok.Getter;

@Getter
public class MyUserDetails extends User {
	
	//페이지에서 접근시 principal로 접근해야한다 >>Authentication 의 principal, credentials, authorities 
									//이 세가지는 페이지에서 쓰인다?
	
	public MyUserDetails(MemberEntity entity) {
		super(entity.getUserId(), entity.getPassword(),entity.getRole().stream()
																.map(r -> new SimpleGrantedAuthority(r.roleName()))
																.collect(Collectors.toSet()));
	}
}












