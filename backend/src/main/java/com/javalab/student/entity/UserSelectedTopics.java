package com.javalab.student.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

/**
 * 📌 사용자 선택 주제 엔티티 (UserSelectedTopics)
 * - 특정 사용자가 선택한 설문 주제를 저장
 * - 복합 키(user_id, topic_id)를 사용
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_selected_topics")
public class UserSelectedTopics {

    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSelectedTopicsId implements Serializable {
        @Column(name = "user_id")
        private Long userId;

        @Column(name = "topic_id")
        private Long topicId;
    }

    @EmbeddedId
    private UserSelectedTopicsId id; // 복합 키 사용

    @ManyToOne
    @MapsId("userId")  // 복합 키의 userId와 매핑
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;  // 기존 user → member로 변경

    @ManyToOne
    @MapsId("topicId")  // 복합 키의 topicId와 매핑
    @JoinColumn(name = "topic_id", nullable = false)
    private SurveyTopic surveyTopic;

    public UserSelectedTopics(Member member, SurveyTopic surveyTopic) {
        this.id = new UserSelectedTopicsId(member.getId(), surveyTopic.getTopicId());
        this.member = member;
        this.surveyTopic = surveyTopic;
    }
}