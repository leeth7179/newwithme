package com.javalab.student.service;

import com.javalab.student.entity.Member;
import com.javalab.student.entity.Questionnaire;
import com.javalab.student.entity.Question;
import com.javalab.student.entity.UserQuestionProgress;
import com.javalab.student.repository.MemberRepository;
import com.javalab.student.repository.UserQuestionProgressRepository;
import com.javalab.student.repository.QuestionnaireRepository;
import com.javalab.student.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserQuestionProgressService {

    private final UserQuestionProgressRepository userQuestionProgressRepository;
    private final MemberRepository memberRepository;
    private final QuestionnaireRepository questionnaireRepository;
    private final QuestionRepository questionRepository;

    /**
     * 특정 userId 기반 문진 진행 상태 조회
     */
    @Transactional(readOnly = true)
    public List<UserQuestionProgress> getUserQuestionProgress(Long userId) {
        return userQuestionProgressRepository.findAllByMember_Id(userId);
    }

    /**
     * 새로운 문진 진행 상태 생성
     */
    @Transactional
    public UserQuestionProgress createUserQuestionProgress(UserQuestionProgress userQuestionProgress) {
        return userQuestionProgressRepository.save(userQuestionProgress);
    }

    /**
     * 특정 userId 기반 문진 진행 상태 삭제
     */
    @Transactional
    public void deleteUserQuestionProgress(Long userId, Long questionnaireId, Long questionId) {
        // Member, Questionnaire, Question 엔티티를 조회
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Questionnaire questionnaire = questionnaireRepository.findById(questionnaireId)
                .orElseThrow(() -> new IllegalArgumentException("문진을 찾을 수 없습니다."));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("질문을 찾을 수 없습니다."));

        // 해당 문진 진행 상태를 조회 후 삭제
        UserQuestionProgress progress = userQuestionProgressRepository
                .findByMemberAndQuestionnaireAndQuestion(member, questionnaire, question)
                .orElseThrow(() -> new IllegalArgumentException("문진 진행 상태를 찾을 수 없습니다."));

        userQuestionProgressRepository.delete(progress);
    }
}
