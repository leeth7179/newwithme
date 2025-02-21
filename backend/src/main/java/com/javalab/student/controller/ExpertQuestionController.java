package com.javalab.student.controller;

import com.javalab.student.entity.ExpertQuestion;
import com.javalab.student.service.ExpertQuestionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // ✅ 권한 체크 추가
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 전문가 질문 컨트롤러
 * 전문가 질문에 대한 요청을 처리하는 REST API 컨트롤러
 */

@RestController
@RequestMapping("/api/expert-questions")
public class ExpertQuestionController {

    private final ExpertQuestionService expertQuestionService;

    @Autowired
    public ExpertQuestionController(ExpertQuestionService expertQuestionService) {
        this.expertQuestionService = expertQuestionService;
    }

    /**
     * ✅ 모든 전문가 질문 조회 (모든 사용자 접근 가능)
     */
    @GetMapping
    public ResponseEntity<List<ExpertQuestion>> getAllExpertQuestions() {
        return ResponseEntity.ok(expertQuestionService.getAllExpertQuestions());
    }

    /**
     * ✅ 전문가 질문 ID로 질문 조회 (모든 사용자 접근 가능)
     */
    @GetMapping("/{expertQuestionId}")
    public ResponseEntity<ExpertQuestion> getExpertQuestionById(@PathVariable @NotNull Long expertQuestionId) {
        Optional<ExpertQuestion> expertQuestion = expertQuestionService.getExpertQuestionById(expertQuestionId);
        return expertQuestion.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * ✅ 새로운 전문가 질문 생성 (USER, VIP, ADMIN만 가능)
     */
    @PreAuthorize("hasRole('USER') or hasRole('VIP') or hasRole('ADMIN')") // ✅ 특정 권한만 허용
    @PostMapping
    public ResponseEntity<ExpertQuestion> createExpertQuestion(@Valid @RequestBody ExpertQuestion expertQuestion,
                                                               @RequestParam Long userId) {
        ExpertQuestion savedQuestion = expertQuestionService.createExpertQuestion(expertQuestion, userId);
        return ResponseEntity.ok(savedQuestion);
    }

    /**
     * ✅ 전문가 질문 삭제 (ADMIN 또는 질문 작성자만 가능)
     */
    @PreAuthorize("hasRole('ADMIN') or @expertQuestionService.isQuestionOwner(#expertQuestionId, authentication.name)")
    @DeleteMapping("/{expertQuestionId}")
    public ResponseEntity<Void> deleteExpertQuestion(@PathVariable Long expertQuestionId) {
        expertQuestionService.deleteExpertQuestion(expertQuestionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * ✅ 예외 처리 - 유효하지 않은 전문가 질문 요청
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body("잘못된 요청: " + ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }
}

