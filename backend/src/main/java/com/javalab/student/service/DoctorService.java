package com.javalab.student.service;

import com.javalab.student.constant.Role;
import com.javalab.student.constant.Status;
import com.javalab.student.dto.DoctorFormDto;
import com.javalab.student.entity.Doctor;
import com.javalab.student.entity.DoctorApplication;
import com.javalab.student.entity.Member;
import com.javalab.student.repository.DoctorApplicationRepository;
import com.javalab.student.repository.DoctorRepository;
import com.javalab.student.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final MemberRepository memberRepository;
    private final DoctorApplicationRepository doctorApplicationRepository;

    /**
     * Doctor 신청정보 저장
     * - DoctorApplication 테이블에 신청정보 저장
     * - 로그인 사용자 정보를 통해 user 정보 저장
     */
    public DoctorApplication saveDoctorApplication(DoctorFormDto doctorFormDto, String email) {
        // 이메일을 통해 회원 정보 조회
        Member member = memberRepository.findByEmail(email);

        log.info("Doctor 서비스- 전문가 신청에서 받은 회원 정보: {}", member);

        // DoctorApplication 에 대기중인 신청 정보가 있으면 예외 처리
        DoctorApplication doctor = doctorApplicationRepository.findByMemberId(member.getId());

        if(doctor != null) {
            throw new RuntimeException("대기중인 신청 정보가 있습니다.");
        }

        // DoctorApplication 객체 생성 및 저장
        DoctorApplication doctorApplication = DoctorApplication.builder()
                .member(member)
                .subject(doctorFormDto.getSubject())
                .hospital(doctorFormDto.getHospital())
                .doctorNumber(doctorFormDto.getDoctorNumber())
                .build();

        try {
            return doctorApplicationRepository.save(doctorApplication);
        } catch (Exception e) {
            throw new RuntimeException("의사 신청 저장 중 오류 발생", e);
        }
    }

    /**
     * Doctor 신청정보 조회
     * - doctor 테이블에서 신청정보 조회
     * - 로그인 사용자의 본인 신청정보만 조회
     */
    public DoctorApplication getDoctorApplication(Long id) {
        return doctorApplicationRepository.findByMemberId(id);
    }

    /**
     * Doctor 신청정보 수정
     * - doctor 테이블에 신청정보 수정
     */
    public DoctorApplication updateDoctorApplication(Long id, DoctorFormDto doctorFormDto) {
        DoctorApplication doctor = getDoctorApplication(id);

        // 현재 상태가 PENDING이 아닌 경우 상태를 재신청으로 변경
        if (doctor.getStatus() != Status.PENDING) {
            doctor.setStatus(Status.RESUBMITTED); // 재신청
        }

        // 신청 정보 수정
        doctor.setSubject(doctorFormDto.getSubject());
        doctor.setHospital(doctorFormDto.getHospital());
        doctor.setDoctorNumber(doctorFormDto.getDoctorNumber());

        // 변경된 정보를 저장
        return doctorApplicationRepository.save(doctor);
    }

    /**
     * Doctor 신청정보 삭제
     * - doctor 테이블에서 신청정보 삭제
     */
    public void deleteDoctorApplication(Long id) {
        log.info("Doctor 신청정보 삭제 서비스 요청 : {}", id);
        DoctorApplication doctor = getDoctorApplication(id);
        log.info("Doctor 신청정보 삭제 서비스 - 신청정보 조회 : {}", doctor);
        doctorApplicationRepository.delete(doctor);
    }

    /**
     * Doctor 권한 변경 (승인, 거절, 보류 , 대기)
     * - doctor 테이블에서 신청 상태 변경
     * - 승인 시 member 테이블에서 권한 DOCTOR로 변경
     * - 승인 시 doctor 테이블에 신청 정보 저장
     * - 거절/보류 상태에서는 사유(reason) 저장
     */
    public void approveDoctorApplication(String email, String status, String reason) {
        // 1. 신청 정보 조회 (예외 처리)
        DoctorApplication doctorApplication = doctorApplicationRepository.findByMemberEmail(email);
        if (doctorApplication == null) {
            throw new IllegalArgumentException("해당 이메일로 등록된 의사 신청이 없습니다: " + email);
        }

        // 2. Member 객체 가져오기
        Member member = doctorApplication.getMember();

        // 3. 상태값 검증 및 변환
        Status doctorStatus;
        try {
            doctorStatus = Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 상태 값입니다: " + status);
        }

        // 4. 승인 상태일 때 reason이 입력되면 예외 처리
        if (doctorStatus.equals(Status.APPROVED) && reason != null && !reason.isBlank()) {
            throw new IllegalArgumentException("승인 상태에서는 사유를 입력할 수 없습니다.");
        }

        // 5. 상태 변경 및 사유 저장
        doctorApplication.setStatus(doctorStatus);

        // 승인일 경우 추가 처리
        if (doctorStatus.equals(Status.APPROVED)) {
            // 멤버 권한을 DOCTOR로 변경
            member.setRole(Role.DOCTOR);
            memberRepository.save(member);

            // 새로운 Doctor 객체 생성 후 저장
            Doctor doctor = new Doctor();
            doctor.setMember(member);
            doctor.setHospital(doctorApplication.getHospital());
            doctor.setDoctorNumber(doctorApplication.getDoctorNumber());
            doctor.setSubject(doctorApplication.getSubject());
            doctorRepository.save(doctor);
        } else {
            // 거절(REJECTED) 또는 보류(ON_HOLD) 상태라면 사유 저장
            if (doctorStatus.equals(Status.REJECTED) || doctorStatus.equals(Status.ON_HOLD)) {
                if (reason == null || reason.isBlank()) {
                    throw new IllegalArgumentException("거절 또는 보류 상태에서는 사유를 입력해야 합니다.");
                }
                doctorApplication.setReason(reason);
            } else {
                // 대기(PENDING) 상태라면 사유를 null로 설정
                doctorApplication.setReason(null);
            }
        }

        // 6. 신청 정보 저장
        doctorApplicationRepository.save(doctorApplication);
    }




    /**
     * Doctor 전체 리스트 조회
     */

    public List<Doctor> getDoctorList() {
        return doctorRepository.findAllWithMember();
    }

    /**
     * 승인 대기중인 신청 리스트 조회
     * - 상태가 대기, 보류, 거절인 신청 리스트 조회
     */
    public List<DoctorApplication> getPendingDoctorApplicationList() {
        // 상태가 대기, 보류, 거절인 신청만 조회
        return doctorApplicationRepository.findByStatusIn(Arrays.asList(Status.PENDING, Status.ON_HOLD, Status.REJECTED));
    }
}
