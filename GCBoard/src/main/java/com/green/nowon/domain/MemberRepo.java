package com.green.nowon.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepo extends JpaRepository<MemberEntity, Long>{

	Optional<MemberEntity> findByUserId(String username);

}
