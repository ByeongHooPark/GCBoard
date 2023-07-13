package com.green.nowon.chess.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "chess_nick")
@Entity
public class ChessNickEntity {
	//방 삭제시 전부 삭제용 테이블
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private long cnno;
	
	//닉네임(일반 + 변형 닉네임)
	@Column(nullable = false)
	private String nomalNick;
	@Column(nullable = false)
	private String encodedNick;
	
	//room 테이블 참조
	@ManyToOne
	@JoinColumn(name = "crno", nullable = false)
	private ChessRoomEntity chessRoom;
}
