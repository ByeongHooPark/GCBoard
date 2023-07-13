package com.green.nowon.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArgueContentRepo extends JpaRepository<ArgueContentEntity, Long>{

	List<ArgueContentEntity> findByArgueBoard(ArgueBoardEntity abE);

	Optional<List<ArgueContentEntity>> findByParent(ArgueContentEntity acE);

	ArgueContentEntity findByMemberAndArgueBoard(MemberEntity mE, ArgueBoardEntity abE);
	
}
