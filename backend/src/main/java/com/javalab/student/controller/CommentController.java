package com.javalab.student.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Sort;

import com.javalab.student.dto.CommentDto;
import com.javalab.student.security.dto.MemberSecurityDto;
import com.javalab.student.service.CommentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final CommentService commentService;

    // 사용자별 댓글 조회
    @GetMapping("/{userId}/comments")
    public ResponseEntity<?> getCommentsByUserId(
        @PathVariable("userId") Long userId,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("regTime").descending());
        Page<CommentDto> comments = commentService.getCommentsByUserId(userId, pageRequest);
        return ResponseEntity.ok(Map.of("total", comments.getTotalElements(), "content", comments.getContent()));
    }
}