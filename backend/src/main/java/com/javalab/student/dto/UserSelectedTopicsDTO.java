package com.javalab.student.dto;

import lombok.*;

/**
 * 유저가 선택한 주제(UserSelectedTopics) DTO
 * 유저가 선택한 주제에 대한 정보를 클라이언트와 주고받을 때 사용하는 객체
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSelectedTopicsDTO {
    private Long id;  // ✅ userId → id 변경
    private Long topicId;
}