package com.green.nowon.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NickRepo extends JpaRepository<NickEntity, Long>{

	Optional<NickEntity> findByNick(String nick);

	Optional<NickEntity> findByMemberAndArgueBoard(MemberEntity mE, ArgueBoardEntity abE);

}
