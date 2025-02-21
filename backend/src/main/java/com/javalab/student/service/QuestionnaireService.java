package com.javalab.student.service;

import com.javalab.student.dto.QuestionnaireDTO;
import com.javalab.student.entity.Member;
import com.javalab.student.entity.Questionnaire;
import com.javalab.student.entity.Survey;
import com.javalab.student.repository.QuestionnaireRepository;
import com.javalab.student.repository.SurveyRepository;
import com.javalab.student.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 📌 문진(Questionnaire) 서비스
 * - 문진 생성, 조회, 삭제 기능을 제공
 */
@Service
@RequiredArgsConstructor
public class QuestionnaireService {

    private final QuestionnaireRepository questionnaireRepository;
    private final MemberRepository memberRepository;
    private final SurveyRepository surveyRepository;

    /**
     * ✅ 모든 문진 조회
     */
    @Transactional(readOnly = true)
    public List<QuestionnaireDTO> getAllQuestionnaires() {
        return questionnaireRepository.findAll()
                .stream()
                .map(QuestionnaireDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ✅ 특정 문진 ID 조회
     */
    @Transactional(readOnly = true)
    public Optional<QuestionnaireDTO> getQuestionnaireById(Long questionnaireId) {
        return questionnaireRepository.findById(questionnaireId)
                .map(QuestionnaireDTO::fromEntity);
    }

    /**
     * ✅ 특정 유저의 최신 무료 문진 조회
     */
    @Transactional(readOnly = true)
    public Optional<QuestionnaireDTO> getLatestFreeSurvey(Long userId) {
        return questionnaireRepository.findTopByUser_IdAndSurveyTypeOrderByCreatedAtDesc(userId, "FREE")
                .map(QuestionnaireDTO::fromEntity);
    }

    /**
     * ✅ 특정 유저의 최신 유료 문진 조회
     */
    @Transactional(readOnly = true)
    public Optional<QuestionnaireDTO> getLatestPaidSurvey(Long userId) {
        return questionnaireRepository.findTopByUser_IdAndSurveyTypeOrderByCreatedAtDesc(userId, "PAID")
                .map(QuestionnaireDTO::fromEntity);
    }

    /**
     * ✅ 특정 유저의 모든 문진 조회
     */
    @Transactional(readOnly = true)
    public List<QuestionnaireDTO> getQuestionnairesByUserId(Long userId) {
        return questionnaireRepository.findAllByUser_Id(userId)
                .stream()
                .map(QuestionnaireDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ✅ 새로운 무료 문진 생성 (DTO 사용)
     */
    @Transactional
    public QuestionnaireDTO createFreeQuestionnaire(QuestionnaireDTO questionnaireDTO) {
        return createQuestionnaire(questionnaireDTO, "FREE");
    }

    /**
     * ✅ 새로운 유료 문진 생성 (DTO 사용)
     */
    @Transactional
    public QuestionnaireDTO createPaidQuestionnaire(QuestionnaireDTO questionnaireDTO) {
        return createQuestionnaire(questionnaireDTO, "PAID");
    }

    /**
     * ✅ 문진 생성 공통 메서드
     */
    private QuestionnaireDTO createQuestionnaire(QuestionnaireDTO questionnaireDTO, String surveyType) {
        // ✅ DTO에서 userId 및 surveyId 추출
        Long userId = questionnaireDTO.getUserId();
        Long surveyId = questionnaireDTO.getSurveyId();

        // ✅ 존재하는 사용자 조회 (없으면 예외 발생)
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 존재하지 않는 사용자입니다. userId: " + userId));

        // ✅ 존재하는 설문 조회 (없으면 예외 발생)
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 존재하지 않는 설문입니다. surveyId: " + surveyId));

        // ✅ 새로운 문진 생성
        Questionnaire questionnaire = Questionnaire.builder()
                .user(user)
                .survey(survey)
                .surveyType(surveyType)
                .responseStatus(Questionnaire.ResponseStatus.IN_PROGRESS)
                .score(0)
                .build();

        // ✅ 저장 후 DTO 변환하여 반환
        return QuestionnaireDTO.fromEntity(questionnaireRepository.save(questionnaire));
    }

    /**
     * ✅ 문진 삭제
     */
    @Transactional
    public void deleteQuestionnaire(Long questionnaireId) {
        if (!questionnaireRepository.existsById(questionnaireId)) {
            throw new IllegalArgumentException("존재하지 않는 문진입니다. questionnaireId: " + questionnaireId);
        }
        questionnaireRepository.deleteById(questionnaireId);
    }
}