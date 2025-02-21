package com.javalab.student.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 전문가 답변 DTO
 * 전문가가 작성한 답변에 대한 정보를 클라이언트와 주고 받을 때 사용하는 개체
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpertAnswerDTO {
    private Long answerId; // 답변 ID
    private Long expertQuestionId; // 전문가 질문 ID
    private Long userId; // ✅ 전문가(의사) ID (doctorId → userId로 수정)
    private String answerText; // 답변 내용
    private LocalDateTime createdAt; // 답변 작성일
}
