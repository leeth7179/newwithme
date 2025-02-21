package com.javalab.student.dto;

import com.javalab.student.entity.Choice;
import com.javalab.student.entity.Question;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 질문 DTO
 * 설문 질문 정보를 클라이언트와 주고받을 때 사용하는 객체
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDTO {
    private Long questionId;  // 질문 ID
    private Integer seq;  // 문항 순서
    private String questionText;  // 질문 내용
    private String questionType;  // ✅ 질문 유형 (ENUM → String 변환)
    private Long topicId; // ✅ 주제 ID (SurveyTopic을 참조)
    private List<ChoiceDTO> choices; // ✅ 선택지 리스트 추가

    /**
     * ✅ Question 엔티티 → QuestionDTO 변환 메서드
     */
    public static QuestionDTO fromEntity(Question question) {
        return QuestionDTO.builder()
                .questionId(question.getQuestionId())
                .seq(question.getSeq())
                .questionText(question.getQuestionText())
                .questionType(question.getQuestionType().name()) // ENUM → String 변환
                .topicId(question.getSurveyTopic().getTopicId()) // 주제 ID
                .choices(question.getChoices().stream()
                        .map(ChoiceDTO::fromEntity)
                        .collect(Collectors.toList())) // ✅ 선택지 변환
                .build();
    }
}