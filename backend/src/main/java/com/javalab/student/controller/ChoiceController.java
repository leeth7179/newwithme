package com.javalab.student.controller;

import com.javalab.student.entity.Choice;
import com.javalab.student.service.ChoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // ✅ 권한 체크 추가
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 선택지 컨트롤러
 * 설문 질문에 대한 선택지 요청을 처리하는 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/choices")
public class ChoiceController {

    private final ChoiceService choiceService;

    @Autowired
    public ChoiceController(ChoiceService choiceService) {
        this.choiceService = choiceService;
    }

    /**
     * ✅ 모든 선택지 조회 (모든 사용자 접근 가능)
     */
    @GetMapping
    public ResponseEntity<List<Choice>> getAllChoices() {
        return ResponseEntity.ok(choiceService.getAllChoices());
    }

    /**
     * ✅ 선택지 ID로 선택지 조회 (모든 사용자 접근 가능)
     */
    @GetMapping("/{choiceId}")
    public ResponseEntity<Choice> getChoiceById(@PathVariable Long choiceId) {
        Optional<Choice> choice = choiceService.getChoiceById(choiceId);
        return choice.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * ✅ 특정 질문 ID에 해당하는 선택지 조회 (모든 사용자 접근 가능)
     */
    @GetMapping("/question/{questionId}")
    public ResponseEntity<List<Choice>> getChoicesByQuestionId(@PathVariable Long questionId) {
        return ResponseEntity.ok(choiceService.getChoicesByQuestionId(questionId));
    }

    /**
     * ✅ 새로운 선택지 생성 (관리자만 가능)
     */
    @PreAuthorize("hasRole('ADMIN')") // ✅ 관리자 권한 필요
    @PostMapping
    public ResponseEntity<Choice> createChoice(@RequestBody Choice choice) {
        return ResponseEntity.ok(choiceService.createChoice(choice));
    }

    /**
     * ✅ 선택지 삭제 (관리자만 가능)
     */
    @PreAuthorize("hasRole('ADMIN')") // ✅ 관리자 권한 필요
    @DeleteMapping("/{choiceId}")
    public ResponseEntity<Void> deleteChoice(@PathVariable Long choiceId) {
        choiceService.deleteChoice(choiceId);
        return ResponseEntity.noContent().build();
    }
}

