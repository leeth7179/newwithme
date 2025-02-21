package com.javalab.student.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

/**
 * 메시지 요청 DTO (클라이언트 → 서버)
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder // ✅ 누락된 @Builder 추가
public class MessageRequestDto {

    @NotNull(message = "발신자 ID는 필수 입력 값입니다.")
    private Long senderId;  // ✅ Member ID를 사용

    @NotNull(message = "수신자 ID는 필수 입력 값입니다.")
    private Long receiverId;

    @NotBlank(message = "메시지 내용은 필수 입력 값입니다.")
    private String content;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("senderId", senderId);
        map.put("receiverId", receiverId);
        map.put("content", content);
        return map;
    }
}
