package com.javalab.student.service;

import com.javalab.student.entity.ExpertQuestion;
import com.javalab.student.entity.Member;
import com.javalab.student.repository.ExpertQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 전문가 질문 서비스
 * 전문가 질문에 대한 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor  // ✅ 생성자 주입 자동 생성
public class ExpertQuestionService {

    private final ExpertQuestionRepository expertQuestionRepository;
    private final MemberService memberService; // ✅ MemberService 주입 추가

    /**
     * 모든 전문가 질문 조회
     */
    @Transactional(readOnly = true)
    public List<ExpertQuestion> getAllExpertQuestions() {
        return expertQuestionRepository.findAll();
    }

    /**
     * 전문가 질문 ID로 질문 조회
     */
    @Transactional(readOnly = true)
    public Optional<ExpertQuestion> getExpertQuestionById(Long expertQuestionId) {
        return expertQuestionRepository.findById(expertQuestionId);
    }

    /**
     * 특정 유저 ID 기반 전문가 질문 조회
     */
    @Transactional(readOnly = true)
    public List<ExpertQuestion> getExpertQuestionsByUserId(Long userId) {
        return expertQuestionRepository.findAllByMember_Id(userId);  // ✅ 필드명 수정
    }

    /**
     * 새로운 전문가 질문 생성
     */
    @Transactional
    public ExpertQuestion createExpertQuestion(ExpertQuestion expertQuestion, Long userId) {
        Member user = memberService.findById(userId);  // ✅ 존재하지 않는 경우 자체적으로 예외 발생
        expertQuestion.setMember(user);  // ✅ 유저 설정
        return expertQuestionRepository.save(expertQuestion);
    }

    /**
     * 전문가 질문 삭제
     */
    @Transactional
    public void deleteExpertQuestion(Long expertQuestionId) {
        expertQuestionRepository.deleteById(expertQuestionId);
    }
}
