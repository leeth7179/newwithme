package com.javalab.student.dto;

import lombok.*;

/**
 * 응답 DTO
 * 설문 응답 정보를 클라이언트와 주고받을 때 사용하는 객체
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDTO {
    private Long responseId;
    private Long surveyId;
    private Long questionId;
    private Long choiceId;
    private Long id;  // ✅ userId → id 변경
}

