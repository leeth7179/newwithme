package com.javalab.student.controller;

import com.javalab.student.entity.SurveyTopic;
import com.javalab.student.service.SurveyTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // ✅ 권한 체크 추가
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 📌 설문 주제 컨트롤러
 * - 설문 주제 관련 요청을 처리하는 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/survey-topics")
@Validated
public class SurveyTopicController {

    private final SurveyTopicService surveyTopicService;

    @Autowired
    public SurveyTopicController(SurveyTopicService surveyTopicService) {
        this.surveyTopicService = surveyTopicService;
    }

    /**
     * ✅ 모든 설문 주제 조회 (모든 사용자 접근 가능)
     */
    @GetMapping
    public ResponseEntity<List<SurveyTopic>> getAllTopics() {
        List<SurveyTopic> topics = surveyTopicService.getAllTopics();
        // Survey 정보를 제외하고 반환
        List<SurveyTopic> filteredTopics = topics.stream()
                .map(this::filterSurveyInfo)
                .collect(Collectors.toList());
        return ResponseEntity.ok(filteredTopics);
    }

    /**
     * ✅ 특정 주제 ID로 조회 (모든 사용자 접근 가능)
     */
    @GetMapping("/{topicId}")
    public ResponseEntity<SurveyTopic> getTopicById(@PathVariable Long topicId) {
        Optional<SurveyTopic> topic = surveyTopicService.getTopicById(topicId);
        return topic.map(t -> ResponseEntity.ok(filterSurveyInfo(t)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * ✅ 유료 설문 주제 조회 (VIP 사용자만 가능)
     */
    @PreAuthorize("hasRole('VIP')") // ✅ VIP 사용자만 접근 가능
    @GetMapping("/paid/{surveyId}")
    public ResponseEntity<?> getPaidTopics(@PathVariable Long surveyId) {
        System.out.println("✅ surveyId 값: " + surveyId); // 👉 디버깅 로그 추가

        List<SurveyTopic> topics = surveyTopicService.getPaidTopics(surveyId);

        if (topics.isEmpty()) {
            System.out.println("❌ survey_id=" + surveyId + "에 해당하는 주제가 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❗ 해당 surveyId에 대한 주제가 없습니다.");
        }

        // Survey 정보를 제외하고 반환
        List<SurveyTopic> filteredTopics = topics.stream()
                .map(this::filterSurveyInfo)
                .collect(Collectors.toList());

        System.out.println("✅ 조회된 주제 개수: " + filteredTopics.size());
        return ResponseEntity.ok(filteredTopics);
    }

    /**
     * ✅ 새로운 설문 주제 생성 (관리자만 가능)
     */
    @PreAuthorize("hasRole('ADMIN')") // ✅ 관리자만 가능
    @PostMapping
    public ResponseEntity<SurveyTopic> createTopic(@RequestBody SurveyTopic surveyTopic) {
        SurveyTopic savedTopic = surveyTopicService.createTopic(surveyTopic);
        return ResponseEntity.ok(filterSurveyInfo(savedTopic));
    }

    /**
     * ✅ 설문 주제 삭제 (관리자만 가능)
     */
    @PreAuthorize("hasRole('ADMIN')") // ✅ 관리자만 가능
    @DeleteMapping("/{topicId}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long topicId) {
        surveyTopicService.deleteTopic(topicId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 🚨 예외 처리 - 유효하지 않은 주제 생성 요청
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body("잘못된 요청: " + ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    /**
     * 🛠️ Survey 정보를 제외한 SurveyTopic 반환 메서드
     */
    private SurveyTopic filterSurveyInfo(SurveyTopic topic) {
        topic.setSurvey(null);  // Survey 필드를 null로 설정하여 JSON 반환 시 순환 참조 방지
        return topic;
    }
}
