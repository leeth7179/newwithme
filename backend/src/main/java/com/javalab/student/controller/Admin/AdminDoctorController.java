package com.javalab.student.controller.Admin;

import com.javalab.student.entity.Doctor;
import com.javalab.student.entity.DoctorApplication;
import com.javalab.student.service.DoctorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/doctor")
@RequiredArgsConstructor
@Log4j2
public class AdminDoctorController {
    private final DoctorService doctorService;



    /**
     * 전문가 리스트 전체 조회
     */
    @GetMapping("/list")
    public ResponseEntity<List<Doctor>> getDoctorList() {
        return ResponseEntity.ok(doctorService.getDoctorList());
    }

    /**
     * 승인 대기중인 전문가 리스트 조회
     * - 전문가 상태가 대기, 보류, 거절인 전문가 리스트 조회
     */
    @GetMapping("/pending")
    public ResponseEntity<List<DoctorApplication>> getPendingDoctorList() {
        // 승인 대기중인 신청 리스트를 조회
        return ResponseEntity.ok(doctorService.getPendingDoctorApplicationList());
    }

    /**
     * 전문가 상태변경
     */
    @PutMapping("/approve/{email}")
    public ResponseEntity<String> approveDoctor(
            @PathVariable String email,
            @RequestBody Map<String, String> requestBody) {

        // 요청 본문에서 상태(status)와 사유(reason)를 가져옴
        String status = requestBody.get("status");
        String reason = requestBody.get("reason"); // reason 추가

        // 상태 변경 처리
        doctorService.approveDoctorApplication(email, status, reason);

        return ResponseEntity.ok("Doctor application processed successfully");
    }
}