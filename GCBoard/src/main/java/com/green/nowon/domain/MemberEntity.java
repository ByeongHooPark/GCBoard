package com.green.nowon.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "member")
@Entity
@Setter
public class MemberEntity {
	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private long mno;
	@Column(unique = true, nullable = false)
	private String userId;
	@Column(nullable = false)
	private String password;
	
	//평균 점수, 토론 참여수
	@Column(columnDefinition = "DOUBLE DEFAULT 0")
	private double score;
	@Column(columnDefinition = "INTEGER DEFAULT 0")
	private long count;
	
	@Builder.Default
	@Enumerated(EnumType.STRING)
	@CollectionTable(name="role")
	@ElementCollection(fetch = FetchType.EAGER)
	private Set<MemberRole> role = new HashSet<>();
	
	public MemberEntity addRole(MemberRole mRole) {
		role.add(mRole);
		return this;
	}

	public MemberEntity removeRole(MemberRole mRole) {
		role.remove(mRole);
		return this;
	}
	
	public MemberEntity update(double score, long count, Set<MemberRole> role){
		this.score = score;
		this.count = count;
		this.role = role;
		return this;
	} 
	
}



