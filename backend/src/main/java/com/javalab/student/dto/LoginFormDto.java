package com.javalab.student.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 로그인 요청 데이터를 담는 DTO 클래스
 */
@Getter
@Setter
public class LoginFormDto {
    private String email;
    private String password;
}
