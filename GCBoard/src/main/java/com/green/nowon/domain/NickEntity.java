package com.green.nowon.domain;

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
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "nick_name")
@Entity
public class NickEntity {
	
	@Column(name = "nick_no")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private long nno;
	@Column(name = "nick_name")
	private String nick;
	
	@JoinColumn(name = "mno")
	@ManyToOne
	private MemberEntity member;
	@JoinColumn(name = "abno")
	@ManyToOne
	private ArgueBoardEntity argueBoard;
}
