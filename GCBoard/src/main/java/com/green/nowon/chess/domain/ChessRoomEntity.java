package com.green.nowon.chess.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder.Default;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "chess_room")
@Entity
@Setter
public class ChessRoomEntity {
	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private long crno;
	//방 제목
	@Column(nullable = false)
	private String title;
	
	//구독자 수
	@Column(columnDefinition = "bigint default 0")
	private long count;
	//게임상태(게임중, 대기)
	private String state;
	
	//백,흑의 닉네임(진짜 닉네임)
	private String whiteNick;
	private String blackNick;
	
	//체스판 백업
	//구독해서 처음 진행중인 방에 들어온 사람한테 보여줄 백업 1개(currentBackUp)
	//'무르기' 구현을 위한 백업 4개
	@Column(columnDefinition = "varchar(1000)")
	private String currentBackUp;
	@Column(columnDefinition = "varchar(1000)")
	private String blackWaitingBackUp;
	@Column(columnDefinition = "varchar(1000)")
	private String whiteWaitingBackUp;
	@Column(columnDefinition = "varchar(1000)")
	private String blackActiveBackUp;
	@Column(columnDefinition = "varchar(1000)")
	private String whiteActiveBackUp;
}














