package com.javalab.student.repository;

import com.javalab.student.constant.Status;
import com.javalab.student.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {



    @Query("SELECT d FROM Doctor d JOIN FETCH d.member")
    List<Doctor> findAllWithMember();

    Doctor findByMemberEmail(String email);
}
