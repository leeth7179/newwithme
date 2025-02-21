package com.javalab.student.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 선택지 엔티티
 * 각 질문에 대한 선택지를 저장하는 테이블과 매핑
 * 선택지 텍스트와 점수 등을 포함
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "choice")
public class Choice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "choice_id")
    private Long choiceId; // 선택지 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question; // 해당 선택지가 속한 질문

    @Column(name = "choice_text", nullable = false, length = 255)
    private String choiceText; // 선택지 텍스트

    @Column(name = "score", nullable = false)
    private Integer score; // 선택지 점수

    @Column(name = "seq")
    private Integer seq; // 선택지 순서
}