package com.javalab.student.dto;

import com.javalab.student.constant.Role;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class MemberDto {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Role role;
    private boolean social;
    private String provider;
}

