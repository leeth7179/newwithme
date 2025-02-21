package com.javalab.student.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 전문가 질문 DTO
 * 전문가가 작성한 질문에 대한 정보를 클라이언트와 주고 받을 때 사용하는 객체
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpertQuestionDTO {
    private Long expertQuestionId; // 전문가 질문 ID
    private Long questionnaireId; //  해당 문진 ID
    private Long userId; // ✅ 전문가(의사) ID (Member 참조)
    private String questionText; // 질문 내용
    private LocalDateTime createdAt; // 질문 작성일
}
