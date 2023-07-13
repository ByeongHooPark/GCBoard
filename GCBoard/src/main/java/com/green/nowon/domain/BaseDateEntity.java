package com.green.nowon.domain;

import java.time.LocalDateTime;


import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@MappedSuperclass
@Getter
public class BaseDateEntity {
	
	@CreationTimestamp
	@Column(columnDefinition = "timestamp(6) null")
	LocalDateTime createdDate;
	
	@UpdateTimestamp
	@Column(columnDefinition = "timestamp(6) null")
	LocalDateTime updateDate;
	
}
