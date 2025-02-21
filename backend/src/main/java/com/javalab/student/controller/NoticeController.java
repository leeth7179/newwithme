package com.javalab.student.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.javalab.student.dto.NoticeDto;
import com.javalab.student.entity.Notice;
import com.javalab.student.service.NoticeService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    //공지사항 목록
    @GetMapping
    public ResponseEntity<Page<NoticeDto>> getAllNotices(
        @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<NoticeDto> noticePage = noticeService.getAllNotices(pageable);
        return ResponseEntity.ok(noticePage);
    }

    //공지사항 상세
    @GetMapping("/{id}")
public ResponseEntity<NoticeDto> getNoticeById(@PathVariable("id") Long id) {
    NoticeDto notice = noticeService.getNoticeById(id); 
    return ResponseEntity.ok(notice);
}

    //공지사항 등록
    @PostMapping
    public ResponseEntity<NoticeDto> createNotice(@RequestBody NoticeDto noticeDto) {
        NoticeDto createdNotice = noticeService.createNotice(noticeDto); 
        return ResponseEntity.ok(createdNotice);
    }

    //공지사항 수정
     @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // 관리자 권한 체크
    public ResponseEntity<NoticeDto> updateNotice(
        @PathVariable("id") Long id, 
        @RequestBody NoticeDto noticeDto,
        Principal principal // 현재 로그인된 사용자 정보
    ) {
        // 로그인된 사용자 확인 로직 추가 가능
        NoticeDto updatedNotice = noticeService.updateNotice(id, noticeDto); 
        return ResponseEntity.ok(updatedNotice);
    }

    //공지사항 삭제
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteNotice(@PathVariable("id") Long noticeId) {
        try {
            noticeService.deleteNotice(noticeId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("공지사항 삭제 중 오류가 발생했습니다.");
        }
    }
}
