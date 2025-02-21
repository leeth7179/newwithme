package com.javalab.student.controller;

import com.javalab.student.entity.UserSelectedTopics;
import com.javalab.student.service.UserSelectedTopicsService;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // ✅ 권한 체크 추가
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 📌 유저가 선택한 주제 컨트롤러
 * - userId 기반 주제 선택 관리
 */
@RestController
@RequestMapping("/api/user-selected-topics")
public class UserSelectedTopicsController {

    private static final Logger logger = LoggerFactory.getLogger(UserSelectedTopicsController.class);

    private final UserSelectedTopicsService userSelectedTopicsService;

    @Autowired
    public UserSelectedTopicsController(UserSelectedTopicsService userSelectedTopicsService) {
        this.userSelectedTopicsService = userSelectedTopicsService;
    }

    /**
     * ✅ 특정 userId 기반 선택한 주제 조회 (본인 또는 관리자만 가능)
     */
    @GetMapping("/{userId}")
    @PreAuthorize("authentication.principal.id == #userId or hasRole('ADMIN')") // ✅ 본인 또는 관리자만 가능
    public ResponseEntity<List<UserSelectedTopics>> getUserSelectedTopics(@PathVariable @NotNull Long userId) {
        logger.info("📌 Fetching selected topics for userId: {}", userId);
        try {
            List<UserSelectedTopics> selectedTopics = userSelectedTopicsService.getSelectedTopicsByUserId(userId);
            logger.info("✅ Successfully fetched {} selected topics for userId: {}", selectedTopics.size(), userId);
            return ResponseEntity.ok(selectedTopics);
        } catch (Exception e) {
            logger.error("🚨 Error fetching selected topics for userId: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * ✅ 새로운 주제 선택 저장 (본인 또는 관리자만 가능)
     */
    @PostMapping("/{userId}/{topicId}")
    @PreAuthorize("authentication.principal.id == #userId or hasRole('ADMIN')") // ✅ 본인 또는 관리자만 가능
    public ResponseEntity<UserSelectedTopics> saveUserSelectedTopic(
            @PathVariable Long userId,
            @PathVariable Long topicId) {
        logger.info("📌 Saving new selected topic for userId: {} and topicId: {}", userId, topicId);
        try {
            UserSelectedTopics savedTopic = userSelectedTopicsService.saveUserSelectedTopic(userId, topicId);
            logger.info("✅ Successfully saved selected topic for userId: {} and topicId: {}", userId, topicId);
            return ResponseEntity.ok(savedTopic);
        } catch (Exception e) {
            logger.error("🚨 Error saving selected topic for userId: {} and topicId: {}", userId, topicId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * ✅ 특정 userId와 topicId 기반 주제 삭제 (본인 또는 관리자만 가능)
     */
    @DeleteMapping("/{userId}/{topicId}")
    @PreAuthorize("authentication.principal.id == #userId or hasRole('ADMIN')") // ✅ 본인 또는 관리자만 가능
    public ResponseEntity<Void> deleteUserSelectedTopic(@PathVariable Long userId, @PathVariable Long topicId) {
        logger.info("📌 Deleting selected topic for userId: {} and topicId: {}", userId, topicId);
        try {
            userSelectedTopicsService.deleteUserSelectedTopic(userId, topicId);
            logger.info("✅ Successfully deleted selected topic for userId: {} and topicId: {}", userId, topicId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("🚨 Error deleting selected topic for userId: {} and topicId: {}", userId, topicId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 🚨 예외 처리 - 일반적인 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralExceptions(Exception ex) {
        logger.error("🚨 An unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❗ 예기치 않은 오류가 발생했습니다.");
    }
}
