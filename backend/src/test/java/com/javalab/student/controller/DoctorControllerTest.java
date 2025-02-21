package com.javalab.student.controller;
/*
package com.javalab.student.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javalab.student.constant.Status;
import com.javalab.student.dto.DoctorFormDto;
import com.javalab.student.entity.Doctor;
import com.javalab.student.service.DoctorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
//@WebMvcTest(DoctorController.class)
@AutoConfigureMockMvc
class DoctorControllerTest {
    @Autowired
    private MockMvc mockMvc;

//    @MockBean
    @Autowired
    private DoctorService doctorService;

//    @MockBean
    @Autowired
    private UserDetailsService userDetailsService;


    private DoctorFormDto doctorFormDto;
    private Doctor doctor;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        doctorFormDto = new DoctorFormDto();
        doctorFormDto.setSubject("내과");
        doctorFormDto.setHospital("테스트병원");
        doctorFormDto.setDoctorNumber("12345");

        User user = new User();
        user.setUserId("test123");

        doctor = Doctor.builder()
                .user(user)
                .subject("내과")
                .hospital("테스트병원")
                .status(Status.PENDING)
                .build();
    }

    @Test
    @WithMockUser(username = "test123")
    @DisplayName("의사 신청 API 테스트")
    void applyDoctorTest() throws Exception {
        // given
        when(doctorService.saveDoctorApplication(any(DoctorFormDto.class), anyString()))
                .thenReturn(doctor);

        // when & then
        mockMvc.perform(post("/api/doctors/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctorFormDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subject").value("내과"))
                .andExpect(jsonPath("$.hospital").value("테스트병원"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser(username = "test123")
    @DisplayName("의사 신청 정보 조회 API 테스트")
    void getApplicationTest() throws Exception {
        // given
        when(doctorService.getDoctorApplication(anyString())).thenReturn(doctor);

        // when & then
        mockMvc.perform(get("/api/doctors/application"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subject").value("내과"))
                .andExpect(jsonPath("$.hospital").value("테스트병원"));
    }


}*/
