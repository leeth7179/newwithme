package com.javalab.student.controller;

import com.javalab.student.dto.QuestionDTO;
import com.javalab.student.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // ✅ 권한 체크 추가
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 질문(Question) 컨트롤러
 * 설문에 포함된 질문에 대한 요청을 처리하는 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    /**
     * ✅ 모든 질문 조회 (모든 사용자 접근 가능)
     */
    @GetMapping
    public ResponseEntity<List<QuestionDTO>> getAllQuestions() {
        List<QuestionDTO> questionDTOs = questionService.getAllQuestions();
        return ResponseEntity.ok(questionDTOs);
    }

    /**
     * ✅ 질문 ID로 질문 조회 (모든 사용자 접근 가능)
     */
    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionDTO> getQuestionById(@PathVariable Long questionId) {
        Optional<QuestionDTO> question = questionService.getQuestionById(questionId);
        return question.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * ✅ 특정 문진 설문의 질문 목록 조회 (선택지 포함) - 무료 문진 (모든 사용자 접근 가능)
     */
    @GetMapping("/free/{surveyId}")
    public ResponseEntity<List<QuestionDTO>> getFreeQuestionsBySurveyId(@PathVariable("surveyId") Long surveyId) {
        List<QuestionDTO> questionDTOs = questionService.getFreeSurveyQuestions(surveyId);
        return ResponseEntity.ok(questionDTOs);
    }

    /**
     * ✅ 특정 유저 ID(userId)에 해당하는 질문 조회 (유료 문진 진행)
     * ✅ 유료 문진 질문 목록을 topics 파라미터로 가져오기
     * ✅ VIP 사용자만 접근 가능 (보안 강화)
     */
    @PreAuthorize("hasRole('VIP')") // ✅ VIP 사용자만 접근 가능
    @GetMapping("/paid")
    public ResponseEntity<List<QuestionDTO>> getPaidQuestions(@RequestParam List<Long> topics) {
        System.out.println("✅ 요청된 topics: " + topics);
        List<QuestionDTO> questions = questionService.getPaidQuestionsByTopics(topics);
        if (questions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        return ResponseEntity.ok(questions);
    }
}
