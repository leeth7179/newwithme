package com.javalab.student.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

/**
 * 📌 문진 진행 상태 (UserQuestionProgress)
 * - 특정 문진(questionnaire)에서 질문(question) 진행 상태 저장
 * - member 기반 문진 진행
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(UserQuestionProgress.UserQuestionProgressId.class) // 복합 키 직접 설정
@Table(name = "user_question_progress")
public class UserQuestionProgress {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;  // User 엔티티와 연결

    @Id
    @ManyToOne
    @JoinColumn(name = "questionnaire_id", nullable = false)
    private Questionnaire questionnaire;  // Questionnaire 엔티티와 연결

    @Id
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;  // Question 엔티티와 연결

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProgressStatus status;

    @Column(name = "progress", nullable = false)
    private Integer progress;

    public enum ProgressStatus {
        NOT_STARTED, IN_PROGRESS, COMPLETED
    }

    /**
     * ✅ 내부 클래스로 복합 키 정의
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class UserQuestionProgressId implements Serializable {
        private Long member;  // `user_id` -> member.getId()를 사용해야 함
        private Long questionnaire;  // `questionnaire_id`
        private Long question;  // `question_id`
    }
}

