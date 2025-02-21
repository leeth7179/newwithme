package com.javalab.student.service;
/*
package com.javalab.student.service;

import com.javalab.student.constant.Status;
import com.javalab.student.dto.DoctorFormDto;
import com.javalab.student.entity.Doctor;
import com.javalab.student.entity.Member;
import com.javalab.student.repository.DoctorRepository;
import com.javalab.student.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.annotation.Commit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Commit
class DoctorServiceTest {

    @InjectMocks
    private DoctorService doctorService;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private MemberRepository memberRepository;

    private User mockUser;
    private Doctor mockDoctor;
    private DoctorFormDto doctorFormDto;

    @BeforeEach
    void setUp() {
        // Mock User 객체 생성
        mockUser = Member.builder()
                .userName("Test User")
                .password("1234")
                .email("test@test.com")
                .phone("010-1234-5678")
                .address("Seoul")
                .points(0)
                .build();

        // Mock DoctorFormDto 객체 생성
        doctorFormDto = DoctorFormDto.builder()
                .subject("Cardiology")
                .hospital("Test Hospital")
                .doctorNumber("12345")
                .build();

        // Mock Doctor 객체 생성
        mockDoctor = Doctor.builder()
                .doctorId(1L)
                .member(mockUser)
                .subject(doctorFormDto.getSubject())
                .hospital(doctorFormDto.getHospital())
                .doctorNumber(doctorFormDto.getDoctorNumber())
                .status(Status.PENDING)
                .build();
    }

    @Test
    @Commit
    void saveDoctorApplication_ShouldSaveDoctor() {
        // Given
        when(userRepository.findByUserId("1")).thenReturn(mockUser);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(mockDoctor);

        // When
        Doctor savedDoctor = doctorService.saveDoctorApplication(doctorFormDto, "testUser");

        // Then
        assertNotNull(savedDoctor);
        assertEquals("Cardiology", savedDoctor.getSubject());
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    @Commit
    void getDoctorApplication_ShouldReturnDoctor() {
        // Given
        when(doctorRepository.findByUser_UserId("testUser")).thenReturn(mockDoctor);

        // When
        Doctor foundDoctor = doctorService.getDoctorApplication("testUser");

        // Then
        assertNotNull(foundDoctor);
        assertEquals("Cardiology", foundDoctor.getSubject());
    }

    @Test
    @Commit
    void updateDoctorApplication_ShouldUpdateDoctorDetails() {
        // Given
        when(doctorRepository.findByUser_UserId("testUser")).thenReturn(mockDoctor);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(mockDoctor);

        // When
        Doctor updatedDoctor = doctorService.updateDoctorApplication("testUser", doctorFormDto);

        // Then
        assertEquals("Cardiology", updatedDoctor.getSubject());
        assertEquals("Test Hospital", updatedDoctor.getHospital());
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    @Commit
    void deleteDoctorApplication_ShouldDeleteDoctor() {
        // Given
        when(doctorRepository.findByUser_UserId("testUser")).thenReturn(mockDoctor);
        doNothing().when(doctorRepository).delete(mockDoctor);

        // When
        doctorService.deleteDoctorApplication("testUser");

        // Then
        verify(doctorRepository, times(1)).delete(mockDoctor);
    }

    */
/*@Test
    @Commit
    void approveDoctorApplication_ShouldUpdateDoctorStatusAndUserRole() {
        // Given
        when(doctorRepository.findByUser_UserId("testUser")).thenReturn(mockDoctor);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(mockDoctor);

        // When
        doctorService.approveDoctorApplication("testUser");

        // Then
        assertEquals(Role.DOCTOR, mockUser.getRole());
        assertEquals(Status.APPROVED, mockDoctor.getStatus());
        verify(userRepository, times(1)).save(mockUser);
        verify(doctorRepository, times(1)).save(mockDoctor);
    }*//*

}
*/
