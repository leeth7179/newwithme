package com.javalab.student.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 생성일(regTime), 수정일(updateTime)을 관리하는 엔티티
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class) // JPA Auditing 활성화
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(name = "reg_time", updatable = false) // 등록 시간, 수정 불가
    private LocalDateTime regTime;

    @LastModifiedDate
    @Column(name = "update_time") // 수정 시간
    private LocalDateTime updateTime;
}
