package com.javalab.student.repository;

import com.javalab.student.entity.Question;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 질문 Repository
 * Question 엔티티에 대한 CRUD 작업을 처리하는 리포지토리
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * ✅ 특정 설문 ID와 설문 타입으로 질문 조회 (무료 문진)
     * 선택지(choices)도 함께 가져오도록 LEFT JOIN FETCH 적용
     */
    @Query("SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.choices WHERE q.survey.surveyId = :surveyId AND q.survey.type = :surveyType")
    List<Question> findBySurvey_SurveyIdAndSurvey_Type(Long surveyId, String surveyType);

    /**
     * ✅ 특정 주제(SurveyTopic)에 해당하는 질문 조회 (유료 회원 문진)
     * 선택지(choices)도 함께 가져오도록 LEFT JOIN FETCH 적용
     */
    @Query("SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.choices WHERE q.surveyTopic.topicId IN :topicIds")
    List<Question> findBySurveyTopic_TopicIdIn(List<Long> topicIds);


}