package com.green.nowon.securityAndConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.green.nowon.domain.MemberEntity;
import com.green.nowon.domain.MemberRepo;

import jakarta.transaction.Transactional;


public class MyUserDetailsService implements UserDetailsService {

	@Autowired 
	private MemberRepo memberRepo;
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		MemberEntity entity = memberRepo.findByUserId(username).orElseThrow(()->new UsernameNotFoundException("!!"));
		
		return new MyUserDetails(entity);
	}
}
