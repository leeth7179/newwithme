package com.javalab.student.dto;

import lombok.*;

/**
 * 설문 주제 DTO
 * 설문에 포함된 각 주제의 ID와 이름을 클라이언트와 주고받을 때 사용하는 객체
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyTopicDTO {
    private Long topicId; // ✅ 주제 ID
    private String topicName; // ✅ 주제명
    private Long surveyId; // ✅ 연관된 설문 ID
}