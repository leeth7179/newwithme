package com.javalab.student.service;

import com.javalab.student.entity.ExpertAnswer;
import com.javalab.student.entity.Member;
import com.javalab.student.repository.ExpertAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 전문가 답변 서비스
 * 전문가 답변에 대한 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor  // ✅ 생성자 주입 자동 생성
public class ExpertAnswerService {

    private final ExpertAnswerRepository expertAnswerRepository;
    private final MemberService memberService; // ✅ MemberService 주입 추가

    /**
     * 모든 전문가 답변 조회
     */
    @Transactional(readOnly = true)
    public List<ExpertAnswer> getAllExpertAnswers() {
        return expertAnswerRepository.findAll();
    }

    /**
     * 특정 전문가 답변 조회 (ID 기반)
     */
    @Transactional(readOnly = true)
    public Optional<ExpertAnswer> getExpertAnswerById(Long answerId) {
        return expertAnswerRepository.findById(answerId);
    }

    /**
     * 특정 유저 ID 기반 전문가 답변 조회
     */
    @Transactional(readOnly = true)
    public List<ExpertAnswer> getExpertAnswersByUserId(Long userId) {
        return expertAnswerRepository.findAllByUserId(userId);
    }

    /**
     * 새로운 전문가 답변 생성
     */
    @Transactional
    public ExpertAnswer createExpertAnswer(ExpertAnswer expertAnswer, Long userId) {
        Member expert = memberService.findById(userId);  // ✅ 존재하지 않는 경우 자체적으로 예외 발생
        expertAnswer.setUser(expert);  // ✅ 전문가 설정
        return expertAnswerRepository.save(expertAnswer);
    }

    /**
     * 전문가 답변 삭제
     */
    @Transactional
    public void deleteExpertAnswer(Long answerId) {
        expertAnswerRepository.deleteById(answerId);
    }
}
