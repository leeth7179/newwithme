package com.javalab.student.repository;

import com.javalab.student.constant.Status;
import com.javalab.student.entity.Doctor;
import com.javalab.student.entity.DoctorApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DoctorApplicationRepository extends JpaRepository<DoctorApplication, Long> {


    DoctorApplication findByMemberEmail(String email);

    DoctorApplication findByMemberId(Long id);



    /**
     * 특정 상태들(PENDING, ON_HOLD, REJECTED)로 필터링
     */
    List<DoctorApplication> findByStatusIn(List<Status> statuses);

    @Query("SELECT d FROM DoctorApplication d WHERE d.regTime >= :threeMonthsAgo")
    List<DoctorApplication> findRecentApplications(@Param("threeMonthsAgo") LocalDateTime threeMonthsAgo);

}
