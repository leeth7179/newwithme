package com.javalab.student.repository;

import com.javalab.student.entity.UserSelectedTopics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSelectedTopicsRepository extends JpaRepository<UserSelectedTopics, Long> {

    /**
     * ✅ 특정 회원(userId)이 선택한 모든 주제 조회
     */
    List<UserSelectedTopics> findAllByMember_Id(Long userId); // ✅ userId → id 로 변경

    /**
     * ✅ 특정 회원(userId)이 특정 주제(topicId)를 선택했는지 확인
     */
    boolean existsByMember_IdAndSurveyTopic_TopicId(Long userId, Long topicId);

    /**
     * ✅ 특정 회원(userId)의 특정 주제(topicId) 선택 삭제
     */
    void deleteByMember_IdAndSurveyTopic_TopicId(Long userId, Long topicId); // ✅ userId → id 로 변경
}