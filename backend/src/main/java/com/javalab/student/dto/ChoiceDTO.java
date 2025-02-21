package com.javalab.student.dto;

import com.javalab.student.entity.Choice;
import lombok.*;

/**
 * 선택지 DTO
 * 질문에 대한 선택지 정보를 클라이언트와 주고 받을 때 사용하는 객체
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChoiceDTO {
    private Long choiceId;   // 선택지 ID
    private String choiceText; // 선택지 텍스트
    private Integer seq; // 선택지 순서
    private Integer score; // 선택지 점수

    /**
     * ✅ Choice 엔티티 → ChoiceDTO 변환 메서드
     */
    public static ChoiceDTO fromEntity(Choice choice) {
        return ChoiceDTO.builder()
                .choiceId(choice.getChoiceId())
                .choiceText(choice.getChoiceText())
                .seq(choice.getSeq())
                .score(choice.getScore())
                .build();
    }
}