package com.javalab.student.controller;

import com.javalab.student.entity.UserQuestionProgress;
import com.javalab.student.service.UserQuestionProgressService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // ✅ 권한 체크 추가
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 📌 문진 진행 상태 컨트롤러
 * - userId 기반으로 문진 진행 상태 조회 및 관리
 */
@RestController
@RequestMapping("/api/user-question-progress")
public class UserQuestionProgressController {

    private final UserQuestionProgressService userQuestionProgressService;

    @Autowired
    public UserQuestionProgressController(UserQuestionProgressService userQuestionProgressService) {
        this.userQuestionProgressService = userQuestionProgressService;
    }

    /**
     * ✅ 특정 userId 기반 문진 진행 상태 조회 (본인만 가능)
     */
    @PreAuthorize("#userId == authentication.principal.id") // ✅ 본인만 조회 가능
    @GetMapping("/{userId}")
    public ResponseEntity<List<UserQuestionProgress>> getUserQuestionProgress(@PathVariable @NotNull Long userId) {
        return ResponseEntity.ok(userQuestionProgressService.getUserQuestionProgress(userId));
    }

    /**
     * ✅ 새로운 문진 진행 상태 생성 (로그인된 사용자만 가능)
     */
    @PreAuthorize("isAuthenticated()") // ✅ 로그인된 사용자만 가능
    @PostMapping
    public ResponseEntity<UserQuestionProgress> createUserQuestionProgress(
            @Valid @RequestBody UserQuestionProgress userQuestionProgress) {
        UserQuestionProgress savedProgress = userQuestionProgressService.createUserQuestionProgress(userQuestionProgress);
        return ResponseEntity.ok(savedProgress);
    }

    /**
     * ✅ 특정 userId 기반 문진 진행 상태 삭제 (본인만 가능)
     */
    @PreAuthorize("#userId == authentication.principal.id") // ✅ 본인만 삭제 가능
    @Transactional
    @DeleteMapping("/{userId}/{questionnaireId}/{questionId}")
    public ResponseEntity<Void> deleteUserQuestionProgress(
            @PathVariable Long userId,
            @PathVariable Long questionnaireId,
            @PathVariable Long questionId) {
        userQuestionProgressService.deleteUserQuestionProgress(userId, questionnaireId, questionId);
        return ResponseEntity.noContent().build();
    }
}
