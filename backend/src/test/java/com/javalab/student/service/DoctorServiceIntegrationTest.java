package com.javalab.student.service;
/*
package com.javalab.student.service;

import com.javalab.student.constant.Role;
import com.javalab.student.dto.DoctorFormDto;
import com.javalab.student.entity.Doctor;
import com.javalab.student.repository.DoctorRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Commit
class DoctorServiceIntegrationTest {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    private User testUser;

    @BeforeAll
    void setup() {
        testUser = new User();
        testUser.setUserId("realUser");
        testUser.setUserName("Real User");
        testUser.setPassword("password");
        testUser.setEmail("real@example.com");
        testUser.setPhone("010-1234-5678");
        testUser.setAddress("Seoul");
        testUser.setRole(Role.USER);
        userRepository.save(testUser);
    }

    @Test
    void testDoctorApplicationFlow() {
        // 1. Doctor 신청 (save)
        DoctorFormDto formDto = new DoctorFormDto();
        formDto.setSubject("Neurology");
        formDto.setHospital("Seoul Medical Center");
        formDto.setDoctorNumber("D67890");

        Doctor savedDoctor = doctorService.saveDoctorApplication(formDto, "realUser");
        assertNotNull(savedDoctor);
        assertEquals("Neurology", savedDoctor.getSubject());

        // 2. Doctor 조회 (get)
        Doctor foundDoctor = doctorService.getDoctorApplication("realUser");
        assertNotNull(foundDoctor);
        assertEquals("Seoul Medical Center", foundDoctor.getHospital());

        // 3. Doctor 승인 (approve)

        */
/*doctorService.approveDoctorApplication("realUser");
        foundDoctor = doctorService.getDoctorApplication("realUser");
        assertEquals(Status.APPROVED, foundDoctor.getStatus());
        assertEquals(Role.DOCTOR, foundDoctor.getUser().getRole());*//*

    }
}
*/
