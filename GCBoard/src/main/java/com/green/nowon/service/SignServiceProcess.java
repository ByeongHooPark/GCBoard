package com.green.nowon.service;

import java.util.Random;

import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.green.nowon.domain.MemberEntity;
import com.green.nowon.domain.MemberRepo;
import com.green.nowon.domain.MemberRole;
import com.green.nowon.domain.dto.SignUpFormDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SignServiceProcess implements SignService {
	
	private final JavaMailSender sender;
	private final ProjectServiceUtil redis;
	private final PasswordEncoder encoder;
	private final MemberRepo memberRepo;
	
/////////회원가입 관련//////////
	
	//비동기-인증메일 보내고, 인증번호 redis에 저장//
	@Override
	public void mailSend(String toEmail) {
		
		//인증문자 생성
		String code = createCode();
		
		//메일 객체 생성
		SimpleMailMessage message = new SimpleMailMessage();
		
		//메일 정보 설정,메일 보내기
		message.setFrom("ddogi93@naver.com");//발신자
		message.setTo(toEmail);//수신자
		message.setSubject("테스트 이메일 제목입니다");//메일 제목
		message.setText("인증번호 : " + code);//메일 내용
		sender.send(message);
		
		//redis에 저장. key,value,만료시간(초)
		redis.setDataExpire(toEmail, code, 60*3);
	}
	
	//인증번호 생성 : 랜덤 숫자 6개 문자열
	private String createCode() {
		return new Random().ints(48, 58).limit(6)
				.collect(StringBuilder::new, 
						 StringBuilder::appendCodePoint, 
						 StringBuilder::append)
				.toString();
	}

	//비동기-인증번호 검사하기, 인증 성공시 인증 증거 저장
	@Override
	public ResponseEntity<Integer> ahthCheck(String email,String code) {
		
		//인증 성공여부 flag
		int authFlag;
		
		//key 존재여부 && value 일치여부
		if(redis.existData(email)&&redis.valueData(email).equals(code)) {
			authFlag = 0;
			//인증했다는 증거 저장
			redis.setDataExpire(email, "ok", 60*30);
		}else {
			authFlag = 1;
		}
		return ResponseEntity.ok().body(authFlag);
	}
	
	
	//회원가입+권한설정
	@Override
	public void signup(SignUpFormDTO dto) {
		
		//1)인증을 했으면 -> score 20, count 10, 권한 silver 까지
		if(redis.existData(dto.getEmail())&&redis.valueData(dto.getEmail()).equals("ok")) {
			memberRepo.save(MemberEntity.builder()
								.userId(dto.getUserId())
								.password(encoder.encode(dto.getPass()))
								.score(70)
								.count(10)
								.build()
								.addRole(MemberRole.USER)
								.addRole(MemberRole.BRONZE)
								.addRole(MemberRole.SILVER)
								);
		
		//2)인증안 안했으면 -> score 0, count 0, 권한 bronze 까지
		} else {
			memberRepo.save(MemberEntity.builder()
					.userId(dto.getUserId())
					.password(encoder.encode(dto.getPass()))
					.score(0)
					.count(0)
					.build()
					.addRole(MemberRole.USER)
					.addRole(MemberRole.BRONZE)
					);
	
		}
	}
}















