package com.javalab.student.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 설문(Survey) 엔티티
 * 설문 제목, 설명, 유형 및 생성일 저장
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "survey")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_id")
    private Long surveyId;

    @Column(name = "survey_title", nullable = false, length = 255)
    private String surveyTitle;

    // @Lob을 사용하지 않고 기본 String 타입을 사용하여 데이터베이스 처리
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // SurveyTopic과의 양방향 참조 방지
    @OneToMany(mappedBy = "survey", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore  // 순환 참조 방지를 위해 추가
    private List<SurveyTopic> surveyTopics;

    // 엔티티 생성 시 현재 시간으로 createdAt 값을 설정
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
