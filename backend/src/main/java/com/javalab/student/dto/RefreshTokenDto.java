package com.javalab.student.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class RefreshTokenDto {
    private String email;
    private String refreshToken;
}
