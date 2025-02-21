package com.javalab.student.controller;

import com.javalab.student.entity.ExpertAnswer;
import com.javalab.student.service.ExpertAnswerService;
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
 * 전문가 답변 컨트롤러
 * 전문가 답변에 대한 요청을 처리하는 REST API 컨트롤러
 */

@RestController
@RequestMapping("/api/expert-answers")
public class ExpertAnswerController {

    private final ExpertAnswerService expertAnswerService;

    @Autowired
    public ExpertAnswerController(ExpertAnswerService expertAnswerService) {
        this.expertAnswerService = expertAnswerService;
    }

    /**
     * ✅ 모든 전문가 답변 조회 (모든 사용자 접근 가능)
     */
    @GetMapping
    public ResponseEntity<List<ExpertAnswer>> getAllExpertAnswers() {
        return ResponseEntity.ok(expertAnswerService.getAllExpertAnswers());
    }

    /**
     * ✅ 전문가 답변 ID 로 답변 조회 (모든 사용자 접근 가능)
     */
    @GetMapping("/{answerId}")
    public ResponseEntity<ExpertAnswer> getExpertAnswerById(@PathVariable @NotNull Long answerId) {
        Optional<ExpertAnswer> expertAnswer = expertAnswerService.getExpertAnswerById(answerId);
        return expertAnswer.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * ✅ 새로운 전문가 답변 생성 (의사 또는 관리자만 가능)
     */
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')") // ✅ DOCTOR 또는 ADMIN 권한 필요
    @PostMapping
    public ResponseEntity<ExpertAnswer> createExpertAnswer(@Valid @RequestBody ExpertAnswer expertAnswer,
                                                           @RequestParam Long userId) {
        ExpertAnswer savedAnswer = expertAnswerService.createExpertAnswer(expertAnswer, userId);
        return ResponseEntity.ok(savedAnswer);
    }

    /**
     * ✅ 전문가 답변 삭제 (의사 또는 관리자만 가능)
     */
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')") // ✅ DOCTOR 또는 ADMIN 권한 필요
    @DeleteMapping("/{answerId}")
    public ResponseEntity<Void> deleteExpertAnswer(@PathVariable Long answerId) {
        expertAnswerService.deleteExpertAnswer(answerId);
        return ResponseEntity.noContent().build();
    }

    /**
     * ✅ 예외 처리 - 유효하지 않은 전문가 답변 요청
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body("잘못된 요청: " + ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }
}
