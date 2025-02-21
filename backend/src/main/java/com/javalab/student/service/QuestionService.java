package com.javalab.student.service;

import com.javalab.student.dto.QuestionDTO;
import com.javalab.student.entity.Question;
import com.javalab.student.entity.UserSelectedTopics;
import com.javalab.student.repository.QuestionRepository;
import com.javalab.student.repository.UserSelectedTopicsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 📌 질문 서비스
 * 설문에 포함된 각 질문에 대한 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor  // ✅ 생성자 주입 자동 생성
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserSelectedTopicsRepository userSelectedTopicsRepository; // ✅ 유료 문진을 위한 Repository 추가

    /**
     * ✅ 모든 질문 조회 (선택지 포함)
     */
    @Transactional(readOnly = true)
    public List<QuestionDTO> getAllQuestions() {
        return questionRepository.findAll().stream()
                .map(QuestionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ✅ 질문 ID로 질문 조회 (선택지 포함)
     */
    @Transactional(readOnly = true)
    public Optional<QuestionDTO> getQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .map(QuestionDTO::fromEntity);
    }

    /**
     * ✅ 특정 설문 ID에 해당하는 질문 조회 (무료 문진)
     * 질문과 선택지를 함께 반환하도록 수정
     */
    @Transactional(readOnly = true)
    public List<QuestionDTO> getFreeSurveyQuestions(Long surveyId) {
        return questionRepository.findBySurvey_SurveyIdAndSurvey_Type(surveyId, "FREE").stream()
                .map(QuestionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ✅ 특정 주제 목록(topics)에 해당하는 유료 문진 질문 리스트 반환 (선택지 포함)
     */
    @Transactional(readOnly = true)
    public List<QuestionDTO> getPaidQuestionsByTopics(List<Long> topicIds) {
        return questionRepository.findBySurveyTopic_TopicIdIn(topicIds).stream()
                .map(QuestionDTO::fromEntity)
                .collect(Collectors.toList());
    }

}