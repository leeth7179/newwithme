package com.javalab.student.service;

import com.javalab.student.constant.Role;
import com.javalab.student.dto.*;
import com.javalab.student.entity.Member;
import com.javalab.student.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입 처리
     * @param memberFormDto - 클라이언트에서 전달받은 회원가입 데이터
     */
    @Transactional
    public void registerMember(MemberFormDto memberFormDto) {
        // 이메일 중복 체크
        if (memberRepository.findByEmail(memberFormDto.getEmail()) != null) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }

        // MemberFormDto를 Member 엔티티로 변환
        Member member = Member.createMember(memberFormDto, passwordEncoder);

        // 데이터 저장
        memberRepository.save(member);
    }

    /**
     * 사용자 정보를 ID로 조회
     * @param id - 사용자 ID
     * @return Member 엔티티
     * @throws IllegalArgumentException - 해당 ID의 사용자가 없는 경우 예외 발생
     */
    @Transactional(readOnly = true)
    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자를 찾을 수 없습니다."));
    }
    @Transactional(readOnly = true)
    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }


    // 사용자 정보 수정 메서드
    public void updateMember(Long id, MemberFormDto memberFormDto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        member.setName(memberFormDto.getName());
        member.setPhone(memberFormDto.getPhone());
        member.setAddress(memberFormDto.getAddress());

        memberRepository.save(member); // 변경 사항 저장
    }


    /**
     * 이메일 중복 체크
     * @param email - 클라이언트에서 입력받은 이메일
     * @return true(중복) or false(사용 가능)
     */
    public boolean isEmailDuplicate(String email) {
        Member foundMember = memberRepository.findByEmail(email);
        return foundMember != null;
    }

    /**
     * 로그인 처리
     * @param loginForm 로그인 폼 데이터 (이메일, 비밀번호)
     * @return 로그인 성공 여부 (true: 성공, false: 실패)
     */
    public boolean login(LoginFormDto loginForm) {
        if (loginForm.getEmail() == null || loginForm.getEmail().isEmpty()) {
            throw new IllegalArgumentException("이메일을 입력해주세요.");
        }

        Member member = memberRepository.findByEmail(loginForm.getEmail());
        if (member == null) {
            return false; // 사용자 없음
        }

        if (!passwordEncoder.matches(loginForm.getPassword(), member.getPassword())) {
            return false; // 비밀번호 불일치
        }

        return true; // 로그인 성공
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다"));
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    /*모든 사용자 조회(페이징)*/
    public PageResponseDTO<MemberDto> getAllMembers(PageRequestDTO pageRequestDTO) {
        // Pageable 생성
        Pageable pageable = pageRequestDTO.getPageable("id");

        // 데이터 조회 (Page 객체 사용)
        Page<Member> result = memberRepository.findAll(pageable);

        // Page -> PageResponseDTO 변환
        List<MemberDto> dtoList = result.getContent().stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());

        return PageResponseDTO.<MemberDto>builder()
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .pageRequestDTO(pageRequestDTO)
                .build();
    }

    public List<Member> getMember() {
        return memberRepository.findAll();
    }


    private MemberDto convertEntityToDto(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .phone(member.getPhone())
                .address(member.getAddress())
                .role(member.getRole())
                .social(member.isSocial())
                .provider(member.getProvider())
                .build();
    }

     /**
     * 사용자 인증 메서드
     * @param email 사용자 이메일
     * @param password 사용자 비밀번호
     * @return 인증된 Member 객체
     * @throws IllegalArgumentException 이메일 또는 비밀번호가 잘못된 경우 예외 발생
     */
    public Member authenticate(String email, String password) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("이메일을 입력해주세요.");
        }

        // 이메일로 사용자 조회
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return member; // 인증 성공 시 Member 객체 반환
    }

    /**
     * 일별 신규 가입자 수를 반환하는 메서드
     */
    public List<NewRegistrationDTO> getNewRegistrationsPerDay() {
        List<Member> members = memberRepository.findAll();  // 모든 회원을 가져옴

        // 날짜 포맷 정의 (yyyy-MM-dd)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 가입일(reg_time)을 기준으로 날짜별 그룹화하여 신규 가입자 수 집계
        Map<String, Long> newRegistrationsCount = members.stream()
                .collect(Collectors.groupingBy(
                        member -> member.getRegTime().format(formatter),  // LocalDateTime을 문자열로 변환
                        Collectors.counting()  // 각 날짜별 가입자 수 계산
                ));

        // 결과를 NewRegistrationDTO 형태로 변환
        return newRegistrationsCount.entrySet().stream()
                .map(entry -> new NewRegistrationDTO(entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());
    }

    // 멤버 권한 변경 메소드
    public void updateMemberRole(String email, Role role) {
        Member member = memberRepository.findByEmail(email);
        member.setRole(role);
        memberRepository.save(member);
    }

}