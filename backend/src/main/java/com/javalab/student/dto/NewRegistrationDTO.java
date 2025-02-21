package com.javalab.student.dto;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class NewRegistrationDTO {
    private String date;  // 가입 날짜 (yyyy-MM-dd 형식)
    private int count;    // 해당 날짜의 신규 가입자 수
}
