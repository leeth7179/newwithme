package com.javalab.student.service;

import com.javalab.student.entity.Member;
import com.javalab.student.entity.SurveyTopic;
import com.javalab.student.entity.UserSelectedTopics;
import com.javalab.student.repository.MemberRepository;
import com.javalab.student.repository.SurveyTopicRepository;
import com.javalab.student.repository.UserSelectedTopicsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 📌 유저가 선택한 주제 서비스
 * - userId 기반 주제 저장 및 조회, 삭제 기능 제공
 */
@Service
@RequiredArgsConstructor
public class UserSelectedTopicsService {

    private final UserSelectedTopicsRepository userSelectedTopicsRepository;
    private final SurveyTopicRepository surveyTopicRepository;
    private final MemberRepository memberRepository;

    /**
     * ✅ 특정 userId 기반 선택한 주제 조회
     */
    @Transactional(readOnly = true)
    public List<UserSelectedTopics> getSelectedTopicsByUserId(Long userId) {
        return userSelectedTopicsRepository.findAllByMember_Id(userId);
    }

    /**
     * ✅ 선택한 주제 저장
     */
    @Transactional
    public UserSelectedTopics saveUserSelectedTopic(Long userId, Long topicId) {
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 해당 사용자가 존재하지 않습니다: " + userId));

        SurveyTopic surveyTopic = surveyTopicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 해당 주제가 존재하지 않습니다: " + topicId));

        return userSelectedTopicsRepository.save(new UserSelectedTopics(user, surveyTopic));
    }

    /**
     * ✅ 선택한 주제 삭제
     */
    @Transactional
    public void deleteUserSelectedTopic(Long userId, Long topicId) {
        if (!userSelectedTopicsRepository.existsByMember_IdAndSurveyTopic_TopicId(userId, topicId)) {
            throw new IllegalArgumentException("❌ 선택한 주제가 존재하지 않습니다. userId: " + userId + ", topicId: " + topicId);
        }
        userSelectedTopicsRepository.deleteByMember_IdAndSurveyTopic_TopicId(userId, topicId);
    }
}