package com.javalab.student.repository;

import com.javalab.student.entity.Member;
import com.javalab.student.entity.UserQuestionProgress;
import com.javalab.student.entity.Questionnaire;
import com.javalab.student.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserQuestionProgressRepository extends JpaRepository<UserQuestionProgress, UserQuestionProgress.UserQuestionProgressId> {

    /**
     * 특정 회원(userId)의 모든 문진 진행 상태 조회
     */
    List<UserQuestionProgress> findAllByMember_Id(Long userId);

    /**
     * 특정 회원(userId)의 진행 중(IN_PROGRESS) 문진 조회
     */
    List<UserQuestionProgress> findAllByMember_IdAndStatus(Long userId, UserQuestionProgress.ProgressStatus status);

    /**
     * 복합 키를 사용하여 문진 진행 상태 조회
     */
    Optional<UserQuestionProgress> findByMemberAndQuestionnaireAndQuestion(Member member, Questionnaire questionnaire, Question question);
}
