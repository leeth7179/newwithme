package com.javalab.student.dto;

import com.javalab.student.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorFormDto {

    @NotBlank(message = "담당과목을 입력해주세요.")
    @Size(max = 50, message = "담당과목는 50자 이하로 입력해주세요")
    private String subject;

    @NotBlank(message = "면허 번호를 입력해주세요.")
    @Size(max = 50, message = "면허 번호는 50자 이하로 입력해주세요")
    private String doctorNumber;

    @NotBlank(message = "병원 정보를 입력해주세요.")
    @Size(max = 255, message = "병원 정보는 255자 이하로 입력해주세요.")
    private String hospital;

    @Size(max = 255)
    private String reason;

    private Long userId;



}
