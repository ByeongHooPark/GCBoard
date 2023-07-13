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
@Table(name = "argue_board")
@Entity
public class ArgueBoardEntity extends BaseDateEntity {
	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private long abno;
	@Column(columnDefinition = "VARCHAR(5000)", nullable = false)
	private String title;
	@Column(columnDefinition = "MEDIUMTEXT", nullable = false)
	private String detail;
	
//	@JoinColumn(name = "parent_abno")
//	@ManyToOne
//	private ArgueBoardEntity parent;
	
}
