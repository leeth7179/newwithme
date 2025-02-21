package com.javalab.student.repository;

import com.javalab.student.entity.SurveyTopic;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 설문 주제(SurveyTopic) Repository
 * 설문 주제에 대한 CRUD 작업을 처리하는 리포지토리
 */
@Repository
public interface SurveyTopicRepository extends JpaRepository<SurveyTopic, Long> {

    /**
     * 특정 설문(surveyId)에 속한 유료 문진(PAID) 주제 목록 조회
     */
    @Query("SELECT st FROM SurveyTopic st WHERE st.survey.surveyId = :surveyId")
    List<SurveyTopic> findAllBySurveyId(@Param("surveyId") Long surveyId);


}