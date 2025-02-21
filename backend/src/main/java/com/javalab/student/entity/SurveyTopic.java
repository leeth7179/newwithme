package com.javalab.student.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "survey_topic")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SurveyTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id")
    private Long topicId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", referencedColumnName = "survey_id")
    private Survey survey;

    @Column(name = "topic_name", nullable = false, length = 255)
    private String topicName;  // 주제명
}