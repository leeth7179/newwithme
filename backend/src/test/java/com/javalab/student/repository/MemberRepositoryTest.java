package com.javalab.student.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;

import com.javalab.student.constant.Role;
import com.javalab.student.entity.Member;
import com.javalab.student.repository.MemberRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // PasswordEncoder 주입

    /**
     * 테스트를 위한 Member 객체 생성 메서드
     */
    private Member createMember(String name, String email, String password, String address, String phone, Role role) {
        return Member.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password)) // 비밀번호 암호화
                .address(address)
                .phone(phone)
                .role(role)
                .points(0) // 초기 포인트 설정
                .social(false) // 소셜 로그인 여부 초기화
                .build();
    }

    /**
     * 회원 저장 테스트
     */
    @Test
    @DisplayName("회원 저장 테스트")
    @Commit
    void saveMemberTest() {
        // Given: 새로운 회원 생성
        Member member = createMember("김길동", "test1@test.com", "1234", "경기도 경기", "010-9876-5432", Role.ADMIN);

        // When: 회원 저장
        Member savedMember = memberRepository.save(member);

        // Then: 저장된 회원 정보 검증
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getId()).isNotNull();
        //assertThat(savedMember.getEmail()).isEqualTo("test@test.com");
        assertThat(savedMember.getEmail()).isEqualTo("test1@test.com");
        assertThat(savedMember.getName()).isEqualTo("김길동");
        assertThat(savedMember.getPhone()).isEqualTo("010-9876-5432");
        assertThat(savedMember.getPoints()).isEqualTo(0);
        assertThat(savedMember.isSocial()).isFalse();

        // 비밀번호가 암호화되었는지 확인 (원본과 다르며 매칭되는지 확인)
        assertThat(passwordEncoder.matches("1234", savedMember.getPassword())).isTrue();
    }

    // 테스트 메서드: 50명의 멤버 생성 및 저장
    @Test
    @DisplayName("회원 저장 테스트")
    @Commit
    public void createMultipleMembersForTest() {
        // 1. 관리자 계정 추가
        String adminEmail = "test@test.com";
        String adminPassword = "1234";
        String adminName = "Admin";
        String adminAddress = "Seoul";
        String adminPhone = "01011112222";
        Role adminRole = Role.ADMIN;  // 관리자 역할

        Member adminMember = createMember(adminName, adminEmail, adminPassword, adminAddress, adminPhone, adminRole);
        memberRepository.save(adminMember);  // 관리자 계정 저장
        System.out.println("Created Admin: " + adminMember.getName() + " (" + adminMember.getEmail() + ")");

        // 2. 50명 회원 데이터 생성
        String[] names = {"John", "Jane", "Tom", "Emily", "Michael", "Sarah", "David", "Jessica", "Daniel", "Laura", "James", "Anna", "William", "Sophia", "Benjamin"};
        String[] addresses = {"Seoul", "Busan", "Incheon", "Daegu", "Gwangju", "Daejeon", "Jeju", "Ulsan", "Suwon", "Changwon"};
        String[] phoneNumbers = {"01012345678", "01023456789", "01034567890", "01045678901", "01056789012", "01067890123", "01078901234", "01089012345", "01090123456", "01001234567"};

        for (int i = 1; i <= 50; i++) {
            // 기본 가짜 데이터를 생성
            String name = names[i % names.length] + i;
            String email = "test" + i + "@test.com";
            String password = "1234";  // 가짜 비밀번호
            String address = addresses[i % addresses.length];
            String phone = phoneNumbers[i % phoneNumbers.length];
            Role role = Role.USER;  // 일반 사용자 역할

            // Member 객체 생성
            Member member = createMember(name, email, password, address, phone, role);

            // 생성한 Member 객체를 저장
            memberRepository.save(member);

            // 생성한 회원 정보 출력 (선택사항)
            System.out.println("Created Member: " + member.getName() + " (" + member.getEmail() + ")");
        }
    }
}
