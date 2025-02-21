package com.javalab.student.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 전문가 답변
 * 전문가가 제공한 답변에 대한 정보를 저장하는 테이블과 매핑
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "expert_answer")
public class ExpertAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long answerId; // 답변 ID

    @ManyToOne
    @JoinColumn(name = "expert_question_id", nullable = false)
    private ExpertQuestion expertQuestion; // 전문가 질문 ID

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // 기존 Long userId -> Member 연관관계로 변경
    private Member user; // 전문가 (Member 엔티티와 매핑)

    @Lob
    @Column(name = "answer_text", nullable = false)
    private String answerText; // 답변 내용

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 답변 작성일
}
