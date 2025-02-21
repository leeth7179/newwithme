package com.javalab.student.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 📌 문진(Questionnaire) 엔티티
 * - 특정 유저가 수행한 설문 정보 저장
 * - 반려견, 설문, 응답 상태 포함
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "questionnaire")
public class Questionnaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "questionnaire_id")
    private Long questionnaireId; // 문진 ID

    @ManyToOne(fetch = FetchType.LAZY)  // ✅ 유저 정보 (Member 엔티티와 연결)
    @JoinColumn(name = "user_id", nullable = false)
    private Member user;

    @ManyToOne(fetch = FetchType.LAZY)  // ✅ 설문 정보 (Survey 엔티티와 연결)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    @Enumerated(EnumType.STRING) // ✅ ENUM → 문자열 저장
    @Column(name = "response_status", nullable = false)
    private ResponseStatus responseStatus = ResponseStatus.PENDING;

    @Column(name = "survey_type", nullable = false, length = 10) // ✅ FREE / PAID 구분
    private String surveyType;

    @Column(name = "score", nullable = false, columnDefinition = "INT DEFAULT 0") // ✅ 기본값 0 설정
    private Integer score = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(); // ✅ 생성 시간 자동 설정
    }

    public enum ResponseStatus {
        PENDING, IN_PROGRESS, COMPLETED
    }
}