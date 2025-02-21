package com.javalab.student.service;

import com.javalab.student.entity.SurveyTopic;
import com.javalab.student.repository.SurveyTopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 설문 주제(SurveyTopic) 서비스
 * 설문 주제에 대한 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor  // ✅ 생성자 주입 자동 생성
public class SurveyTopicService {

    private final SurveyTopicRepository surveyTopicRepository;

    /**
     * ✅ 모든 설문 주제 조회
     */
    @Transactional(readOnly = true)
    public List<SurveyTopic> getAllTopics() {
        return surveyTopicRepository.findAll();
    }

    /**
     * ✅ 특정 주제 ID로 조회
     */
    @Transactional(readOnly = true)
    public Optional<SurveyTopic> getTopicById(Long topicId) {
        return surveyTopicRepository.findById(topicId);
    }

    /**
     * ✅ 특정 설문(surveyId)에 속한 유료 문진(PAID) 주제 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SurveyTopic> getPaidTopics(Long surveyId) {
        return surveyTopicRepository.findAllBySurveyId(surveyId);  // Repository의 실제 메소드명으로 수정
    }


    /**
     * ✅ 새로운 설문 주제 생성
     */
    @Transactional
    public SurveyTopic createTopic(SurveyTopic surveyTopic) {
        return surveyTopicRepository.save(surveyTopic);
    }

    /**
     * ✅ 설문 주제 삭제
     */
    @Transactional
    public void deleteTopic(Long topicId) {
        surveyTopicRepository.deleteById(topicId);
    }
}