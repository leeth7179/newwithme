package com.javalab.student.dto;

import com.javalab.student.entity.Questionnaire;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 📌 문진 DTO
 * - 유저가 진행한 문진 정보를 담는 객체
 * - 백엔드와 프론트엔드 간의 데이터 전송을 위해 사용
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionnaireDTO {
    private Long questionnaireId;  // ✅ 문진 ID
    private Long surveyId;         // ✅ 설문 ID
    private Long userId;           // ✅ 사용자 ID
    private String surveyType;     // ✅ 설문 유형 (FREE / PAID)
    private String responseStatus; // ✅ 응답 상태 (PENDING, IN_PROGRESS, COMPLETED)
    private Integer score;         // ✅ 총점
    private LocalDateTime createdAt; // ✅ 생성 날짜

    /**
     * ✅ `Questionnaire` 엔티티를 `QuestionnaireDTO`로 변환하는 정적 메서드
     */
    public static QuestionnaireDTO fromEntity(Questionnaire questionnaire) {
        return QuestionnaireDTO.builder()
                .questionnaireId(questionnaire.getQuestionnaireId())
                .surveyId(questionnaire.getSurvey() != null ? questionnaire.getSurvey().getSurveyId() : null) // ✅ Null 체크
                .userId(questionnaire.getUser() != null ? questionnaire.getUser().getId() : null) // ✅ Null 체크
                .surveyType(questionnaire.getSurveyType() != null ? questionnaire.getSurveyType() : "FREE") // ✅ 기본값 설정
                .responseStatus(questionnaire.getResponseStatus() != null ? questionnaire.getResponseStatus().name() : "PENDING") // ✅ ENUM -> String 변환 및 기본값 설정
                .score(questionnaire.getScore() != null ? questionnaire.getScore() : 0) // ✅ Null 체크 및 기본값 0 설정
                .createdAt(questionnaire.getCreatedAt())
                .build();
    }
}