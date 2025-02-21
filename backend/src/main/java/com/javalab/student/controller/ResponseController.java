package com.javalab.student.controller;

import com.javalab.student.entity.Member;
import com.javalab.student.entity.Response;
import com.javalab.student.service.ResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // ✅ 권한 체크 추가
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 응답 컨트롤러
 * 설문 응답 관련 요청을 처리하는 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/responses")
public class ResponseController {

    private final ResponseService responseService;

    @Autowired
    public ResponseController(ResponseService responseService) {
        this.responseService = responseService;
    }

    /**
     * ✅ 모든 응답 조회 (관리자만 가능)
     */
    @PreAuthorize("hasRole('ADMIN')") // ✅ 관리자만 접근 가능
    @GetMapping
    public ResponseEntity<List<Response>> getAllResponses() {
        return ResponseEntity.ok(responseService.getAllResponses());
    }

    /**
     * ✅ 특정 사용자의 응답 조회 (본인만 가능)
     */
    @PreAuthorize("#userId == authentication.principal.id") // ✅ 본인만 조회 가능
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Response>> getResponsesByUserId(@PathVariable Long userId) {
        List<Response> responses = responseService.getResponsesByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * ✅ 응답 저장 (로그인된 사용자만 가능)
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<Response> saveResponse(@RequestBody Response response,
                                                 @AuthenticationPrincipal UserDetails userDetails) {  // 현재 로그인한 사용자 정보 가져오기
        Long userId = ((Member) userDetails).getId();  // UserDetails에서 사용자 ID 추출
        Response savedResponse = responseService.createResponse(response, userId);
        return ResponseEntity.ok(savedResponse);
    }
}