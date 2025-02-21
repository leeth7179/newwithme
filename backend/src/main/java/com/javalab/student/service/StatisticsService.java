package com.javalab.student.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.javalab.student.dto.NewRegistrationDTO;
import com.javalab.student.entity.DoctorApplication;
import com.javalab.student.entity.Member;
import com.javalab.student.repository.DoctorApplicationRepository;
import com.javalab.student.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final MemberRepository memberRepository;
    private final DoctorApplicationRepository doctorApplicationRepository;

    /**
     * 신규 멤버 집계
     * @return
     */
    public List<NewRegistrationDTO> getNewRegistrationsPerDay() {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<Member> members = memberRepository.findRecentMembers(threeMonthsAgo);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, Long> newRegistrationsCount = members.stream()
                .collect(Collectors.groupingBy(
                        member -> member.getRegTime().format(formatter),
                        Collectors.counting()
                ));

        return newRegistrationsCount.entrySet().stream()
                .map(entry -> new NewRegistrationDTO(entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());
    }

    /**
     * 신규 전문가 신청 집계
     * @return
     */
    public List<NewRegistrationDTO> getNewDoctorApplicationsPerDay() {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<DoctorApplication> applications = doctorApplicationRepository.findRecentApplications(threeMonthsAgo);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, Long> applicationsCount = applications.stream()
                .collect(Collectors.groupingBy(
                        app -> app.getRegTime().format(formatter),
                        Collectors.counting()
                ));

        return applicationsCount.entrySet().stream()
                .map(entry -> new NewRegistrationDTO(entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());
    }
}
