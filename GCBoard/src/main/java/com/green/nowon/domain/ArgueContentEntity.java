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
@Table(name = "argue_content")
@Entity
public class ArgueContentEntity extends BaseDateEntity {
	//토론 게시글
	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private long acno;
	
	//주장,논거
	@Column(columnDefinition = "VARCHAR(5000)", nullable = false)
	private String arguments;
	@Column(columnDefinition = "MEDIUMTEXT", nullable = false)
	private String reason;
	
	//gpt 점수와 평가, 반박첨언 타입
	@Column(nullable = false)
	private double gptScore;
	@Column(name = "gpt_evaluation" ,columnDefinition = "MEDIUMTEXT", nullable = false)
	private String gptEval;
	@Column(name = "comment_type")
	private String type;//domain : agree, opposite, neutrality
	
	//멤버 테이블 참조
	@ManyToOne
	@JoinColumn(name = "mno")
	private MemberEntity member;
	
	//토론 게시판 테이블 참조
	@ManyToOne
	@JoinColumn(name = "abno")
	private ArgueBoardEntity argueBoard;
	
	//닉네임 테이블 참조
	@ManyToOne
	@JoinColumn(name = "nno")
	private NickEntity nickName;
	
	//반박첨언대상 주장
	@ManyToOne
	@JoinColumn(name = "parent_acno")
	private ArgueContentEntity parent;
	
}














