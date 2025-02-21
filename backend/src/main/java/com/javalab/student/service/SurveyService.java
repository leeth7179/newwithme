package com.javalab.student.service;

import com.javalab.student.entity.Survey;
import com.javalab.student.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 설문 서비스
 * 설문에 대한 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor  // ✅ 생성자 주입 자동 생성
public class SurveyService {

    private final SurveyRepository surveyRepository;

    /**
     * ✅ 모든 설문 조회
     */
    @Transactional(readOnly = true)
    public List<Survey> getAllSurveys() {
        return surveyRepository.findAll();
    }

    /**
     * ✅ 특정 설문 ID로 설문 조회
     */
    @Transactional(readOnly = true)
    public Optional<Survey> getSurveyById(Long surveyId) {
        return surveyRepository.findById(surveyId);
    }

    /**
     * ✅ 새로운 설문 생성
     */
    @Transactional
    public Survey createSurvey(Survey survey) {
        return surveyRepository.save(survey);
    }

    /**
     * ✅ 설문 삭제
     */
    @Transactional
    public void deleteSurvey(Long surveyId) {
        surveyRepository.deleteById(surveyId);
    }
}
