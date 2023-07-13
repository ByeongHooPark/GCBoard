package com.green.nowon;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.green.nowon.chess.domain.ChessRoomEntity;
import com.green.nowon.chess.domain.ChessRoomRepo;
import com.green.nowon.domain.MemberEntity;
import com.green.nowon.domain.MemberRepo;
import com.green.nowon.domain.MemberRole;

@SpringBootTest
class Project02PbhApplicationTests {
	
	@Autowired
	private MemberRepo repo;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private ChessRoomRepo chessRoomRepo;
	
//	@Test
	void 관리자계정생성() {
		repo.save(MemberEntity.builder()
				.userId("ADMIN")
				.password(encoder.encode("ADMIN123"))
				.score(1000)
				.count(1000)
				.build()
				.addRole(MemberRole.USER)
				.addRole(MemberRole.ADMIN)
				.addRole(MemberRole.BRONZE)
				.addRole(MemberRole.SILVER)
				.addRole(MemberRole.GOLD)
				.addRole(MemberRole.PLATINUM)
				.addRole(MemberRole.DIAMOND)
				);
	}
	
//	@Test
	void 계정생성실버() {
		repo.save(MemberEntity.builder()
				.userId("SILVER_USER")
				.password(encoder.encode("SILVER123"))
				.score(20)
				.count(10)
				.build()
				.addRole(MemberRole.USER)
				.addRole(MemberRole.BRONZE)
				.addRole(MemberRole.SILVER)
				);
	}
//	@Test
	void 계정생성골드() {
		repo.save(MemberEntity.builder()
				.userId("GOLD_USER")
				.password(encoder.encode("GOLD123"))
				.score(40)
				.count(20)
				.build()
				.addRole(MemberRole.USER)
				.addRole(MemberRole.BRONZE)
				.addRole(MemberRole.SILVER)
				.addRole(MemberRole.GOLD)
				);
	}
//	@Test
	void 계정생성플레() {
		repo.save(MemberEntity.builder()
				.userId("PLATINUM_USER")
				.password(encoder.encode("PLATINUM123"))
				.score(60)
				.count(30)
				.build()
				.addRole(MemberRole.USER)
				.addRole(MemberRole.BRONZE)
				.addRole(MemberRole.SILVER)
				.addRole(MemberRole.GOLD)
				.addRole(MemberRole.PLATINUM)
				);
	}

		@Test
	void 계정생성다이아() {
		repo.save(MemberEntity.builder()
				.userId("DIAMOND_USER")
				.password(encoder.encode("DIAMOND123"))
				.score(80)
				.count(40)
				.build()
				.addRole(MemberRole.USER)
				.addRole(MemberRole.BRONZE)
				.addRole(MemberRole.SILVER)
				.addRole(MemberRole.GOLD)
				.addRole(MemberRole.PLATINUM)
				.addRole(MemberRole.DIAMOND)
				);
	}
	
	
	
//	@Test
	void aaa() {
		ChessRoomEntity crE = chessRoomRepo.findById((long)1).get();
		System.out.println("출력 시작");
		System.out.println(crE.getCurrentBackUp());
	}
	
	
	
	
	
	
	
	
	
}
