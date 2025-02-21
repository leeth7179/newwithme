package com.javalab.student.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * 질문 엔티티
 * 설문에 포함된 각 질문에 대한 정보를 저장하는 테이블과 매핑
 * 질문 내용, 순서, 질문 유형 및 필수 응답 여부를 포함
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;

    @Column(name = "seq", nullable = false)
    private Integer seq;

    @Lob
    @Column(name = "question_text", nullable = false)
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    @Column(name = "is_required", nullable = false)
    private Boolean isRequired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false) // ✅ Survey 엔티티와 관계 설정
    private Survey survey; // ✅ 추가

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private SurveyTopic surveyTopic;

    /** ✅ 질문과 선택지를 연결하는 @OneToMany 관계 추가 */
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("question") // 무한 루프 방지
    @OrderBy("seq ASC") // 선택지 순서 유지
    private List<Choice> choices; // ✅ 추가

    public enum QuestionType {
        SINGLE_CHOICE, MULTIPLE_CHOICE, TEXT, RATING
    }
}