package com.javalab.student.controller;

import com.javalab.student.entity.Survey;
import com.javalab.student.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // ✅ 권한 체크 추가
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 📌 설문 컨트롤러
 * - 설문 관련 요청을 처리하는 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/surveys")
public class SurveyController {

    private final SurveyService surveyService;

    @Autowired
    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    /**
     * ✅ 모든 설문 조회 (모든 사용자 접근 가능)
     */
    @GetMapping
    public ResponseEntity<List<Survey>> getAllSurveys() {
        return ResponseEntity.ok(surveyService.getAllSurveys());
    }

    /**
     * ✅ 특정 설문 ID로 설문 조회 (모든 사용자 접근 가능)
     */
    @GetMapping("/{surveyId}")
    public ResponseEntity<Survey> getSurveyById(@PathVariable Long surveyId) {
        Optional<Survey> survey = surveyService.getSurveyById(surveyId);
        return survey.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * ✅ 새로운 설문 생성 (관리자만 가능)
     */
    @PreAuthorize("hasRole('ADMIN')") // ✅ 관리자만 가능
    @PostMapping
    public ResponseEntity<Survey> createSurvey(@RequestBody Survey survey) {
        return ResponseEntity.ok(surveyService.createSurvey(survey));
    }

    /**
     * ✅ 설문 삭제 (관리자만 가능)
     */
    @PreAuthorize("hasRole('ADMIN')") // ✅ 관리자만 가능
    @DeleteMapping("/{surveyId}")
    public ResponseEntity<Void> deleteSurvey(@PathVariable Long surveyId) {
        surveyService.deleteSurvey(surveyId);
        return ResponseEntity.noContent().build();
    }
}
