package com.javalab.student.dto;

import com.javalab.student.entity.UserQuestionProgress;
import lombok.*;

/**
 * 유저 문진 진행 DTO
 * 유저가 진행 중인 문진의 각 질문에 대한 진행 상태를 나타내는 객체
 * 유저의 진행 상태, 진행 비율등을 포함
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserQuestionProgressDTO {
    private Long userId;  // 유저 ID
    private Long questionnaireId;  // 문진 ID
    private Long questionId;  // 질문 ID
    private ProgressStatus status;  // 진행 상태 (NOT_STARTED, IN_PROGRESS, COMPLETED)
    private Integer progress;  // 진행 상태 비율 (0-100)

    public enum ProgressStatus {
        NOT_STARTED, IN_PROGRESS, COMPLETED
    }
}
