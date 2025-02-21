package com.javalab.student.repository;

import com.javalab.student.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 설문 Repository
 * Survey 엔티티에 대한 CRUD 작업을 처리하는 리포지토리입니다.
 */
@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {

    /**
     * ✅ 특정 설문 유형별 설문 조회
     * - FREE(무료) 또는 PAID(유료) 설문을 조회하는 기능 추가
     */
    List<Survey> findByType(String type);
}
