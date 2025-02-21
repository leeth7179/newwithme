package com.javalab.student.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.javalab.student.dto.NoticeDto;

@Entity
@Table(name = "notices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID

    private String title; // 제목

    private String content; // 내용

    private String category; // 카테고리

    @Column(name = "important", nullable = false, columnDefinition = "boolean default false")
    private Boolean important = false;

    @Column(updatable = false)
    @CreationTimestamp  // 생성 시 자동 설정
    private LocalDateTime createdAt;

    @UpdateTimestamp    // 수정 시 자동 설정
    private LocalDateTime updatedAt;

    // 날짜 포맷팅을 위한 메서드 추가
    public String getFormattedCreatedAt() {
        if (this.createdAt != null) {
            return this.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        return null;
    }

    public String getFormattedUpdatedAt() {
        if (this.updatedAt != null) {
            return this.updatedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        return null;
    }

 public void update(NoticeDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.category = dto.getCategory();
        
        // important 값을 명시적으로 유지
        this.important = Boolean.TRUE.equals(dto.getImportant());
    }

// 카테고리
// 이벤트 및 프로모션
//시스템 점검 및 서비스 안내
// 법률 및 정책 안내
// 운영 공지
// 긴급 공지 (중요) 상단에 고정

}
