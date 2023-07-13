package com.green.nowon.chess.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChessNickRepo extends JpaRepository<ChessNickEntity, Long>{

	List<ChessNickEntity> findByChessRoom(ChessRoomEntity crE);

	ChessNickEntity findByEncodedNick(String myNick);

}
