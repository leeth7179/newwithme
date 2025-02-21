package com.javalab.student.controller;

import com.javalab.student.config.jwt.TokenProvider;
import com.javalab.student.dto.DoctorFormDto;
import com.javalab.student.entity.Doctor;
import com.javalab.student.entity.DoctorApplication;
import com.javalab.student.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Log4j2
public class DoctorController {
    private final DoctorService doctorService;
    private final TokenProvider tokenProvider;

    /**
     * 새로운 의사 신청을 저장하는 API
     */
    @PostMapping("/apply/{email}")
    public ResponseEntity<DoctorApplication> applyDoctor(
            @RequestBody DoctorFormDto doctorFormDto,
            @PathVariable String email) {
        log.info("컨트롤러 사용자 신청 요청 수신: {}", doctorFormDto);
        log.info("컨트롤러  사용자 정보: {}", email);
        log.info("신청 컨트롤러 실행");

        DoctorApplication doctor = doctorService.saveDoctorApplication(doctorFormDto, email);
        return ResponseEntity.ok(doctor);
    }

    /**
     * 현재 로그인한 사용자의 의사 신청 정보를 조회하는 API
     */
    @GetMapping("/application/{id}")
    public ResponseEntity<DoctorApplication> getApplication(
            @PathVariable Long id) {

        DoctorApplication doctor = doctorService.getDoctorApplication(id);
        return ResponseEntity.ok(doctor);
    }

    /**
     * 기존 의사 신청 정보를 수정하는 API
     */
    @PutMapping("/application/{id}")
    public ResponseEntity<DoctorApplication> updateApplication(
            @Valid @RequestBody DoctorFormDto doctorFormDto,
            @PathVariable Long id) {
        log.info("전문가 수정 컨트롤러 요청");
        log.info("받아온 정보: {}", doctorFormDto);


        DoctorApplication doctor = doctorService.updateDoctorApplication(id, doctorFormDto);
        return ResponseEntity.ok(doctor);
    }

    /**
     * 기존 의사 신청 정보를 삭제하는 API
     */
    @DeleteMapping("/application/{id}")
    public ResponseEntity<Void> deleteApplication(
            @PathVariable Long id) {
        log.info("전문가 삭제 컨트롤러 요청");

        doctorService.deleteDoctorApplication(id);
        return ResponseEntity.noContent().build();
    }
}