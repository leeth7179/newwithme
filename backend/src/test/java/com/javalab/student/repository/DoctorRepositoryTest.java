package com.javalab.student.repository;

import com.javalab.student.constant.Role;
import com.javalab.student.constant.Status;
import com.javalab.student.dto.MemberFormDto;
import com.javalab.student.entity.Doctor;
import com.javalab.student.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DoctorRepositoryTest {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        insertDummyData();
    }

    private Member createMember(String name, String email, String password, String address, String phone, Role role) {
        if (memberRepository.existsByEmail(email)) {
            return null; // 중복 데이터 방지
        }
        MemberFormDto memberFormDto = MemberFormDto.builder()
                .name(name)
                .email(email)
                .password(password)
                .address(address)
                .phone(phone)
                .role(role)
                .build();
        return Member.createMember(memberFormDto, passwordEncoder);
    }

    private Doctor createDoctor(Member member, String subject, String hospital, String doctorNumber) {
        if (member == null) {
            return null; // 유효하지 않은 회원은 추가하지 않음
        }
        return Doctor.builder()
                .member(member)
                .subject(subject)
                .hospital(hospital)
                .doctorNumber(doctorNumber)
                .build();
    }

    private void insertDummyData() {
        List<Member> members = generateMembers();
        List<Doctor> doctors = generateDoctors(members);

        members = members.stream().filter(m -> m != null).collect(Collectors.toList());
        doctors = doctors.stream().filter(d -> d != null).collect(Collectors.toList());

        memberRepository.saveAll(members);
        doctorRepository.saveAll(doctors);
    }

    private List<Member> generateMembers() {
        return IntStream.rangeClosed(1, 10)
                .mapToObj(i -> createMember("사용자" + i, "user" + i + "@example.com", "1234",
                        "서울시 어딘가 " + i, "010-1234-56" + String.format("%02d", i), Role.USER))
                .filter(member -> member != null)
                .collect(Collectors.toList());
    }

    private List<Doctor> generateDoctors(List<Member> members) {
        return members.stream()
                .filter(member -> member.getRole() == Role.DOCTOR)
                .map(member -> createDoctor(member, "과목" + (members.indexOf(member) + 1),
                        "병원" + (members.indexOf(member) + 1), "D" + String.format("%03d", members.indexOf(member) + 1)))
                .filter(doctor -> doctor != null)
                .collect(Collectors.toList());
    }

    @Test
    @DisplayName("더미 데이터 삽입 테스트")
    @Commit
    void insertDummyDataTest() {
        insertDummyData();
    }
}
