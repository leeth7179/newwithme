package com.javalab.student.service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.javalab.student.dto.NoticeDto;
import com.javalab.student.entity.Notice;
import com.javalab.student.repository.NoticeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    // 새 공지사항 생성 (DTO -> Entity 변환)
    public NoticeDto createNotice(NoticeDto noticeDto) {
        Notice notice = convertToEntity(noticeDto); 
        notice.setCreatedAt(LocalDateTime.now());
        Notice savedNotice = noticeRepository.save(notice);
        return convertToDto(savedNotice); // 저장된 Entity를 다시 DTO로 변환하여 반환
    }

    // 기존 공지사항 수정 (DTO -> Entity 변환)
    public NoticeDto updateNotice(Long id, NoticeDto noticeDto) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));
        
        // DTO의 필드값으로 엔티티 업데이트
        notice.setTitle(noticeDto.getTitle());
        notice.setContent(noticeDto.getContent());
        notice.setCategory(noticeDto.getCategory());
        notice.setImportant(noticeDto.getImportant());
        notice.setUpdatedAt(LocalDateTime.now());

        Notice updatedNotice = noticeRepository.save(notice);
        return convertToDto(updatedNotice); // 수정된 Entity를 DTO로 변환하여 반환
    }

    // ID로 공지사항 삭제
    public void deleteNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 존재하지 않습니다."));
        
        noticeRepository.delete(notice);
    }

    // ID로 단일 공지사항 조회 (Entity -> DTO 변환)
    public NoticeDto getNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));
        return convertToDto(notice); // 조회된 Entity를 DTO로 변환
    }

    // 모든 공지사항 조회 (Entity 목록 -> DTO 목록 변환)
    public List<NoticeDto> getAllNotices() {
        //List<Notice> notices = noticeRepository.findAll();
        List<Notice> notices = noticeRepository.findAllByOrderByCreatedAtDesc(); // 생성일 기준 내림차순 정렬
        
        // 공지사항 정렬 (긴급 공지 우선, 그 다음 생성일자 순)
        return notices.stream()
                .sorted((n1, n2) -> {
                    // 긴급 카테고리 우선 정렬
                    if ("긴급".equals(n1.getCategory()) && !"긴급".equals(n2.getCategory())) {
                        return -1;
                    } else if (!"긴급".equals(n1.getCategory()) && "긴급".equals(n2.getCategory())) {
                        return 1;
                    }
                    // 같은 카테고리인 경우 최신순 정렬
                    return n2.getCreatedAt().compareTo(n1.getCreatedAt());
                })
                .map(this::convertToDto) // 각 엔티티를 DTO로 변환
                .collect(Collectors.toList());
    }

    // 페이지네이션을 지원하는 메서드 추가
    public Page<NoticeDto> getAllNotices(Pageable pageable) {
        // 리포지토리에서 페이지별 공지사항 조회
        Page<Notice> noticePage = noticeRepository.findAll(pageable);
        
        // Notice 엔티티를 NoticeDto로 변환
        return noticePage.map(this::convertToDto);
    }

    // Notice 엔티티를 DTO로 변환하는 메소드
    private NoticeDto convertToDto(Notice notice) {
        NoticeDto dto = new NoticeDto();
        dto.setId(notice.getId());
        dto.setTitle(notice.getTitle());
        dto.setContent(notice.getContent());
        dto.setCategory(notice.getCategory());
        // Boolean 값을 명시적으로 처리
        dto.setImportant(Boolean.TRUE.equals(notice.getImportant()));
        dto.setCreatedAt(notice.getCreatedAt());
        dto.setUpdatedAt(notice.getUpdatedAt());
        return dto;
    }

    // DTO를 Notice 엔티티로 변환하는 메소드
    private Notice convertToEntity(NoticeDto dto) {
        Notice notice = new Notice();
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        notice.setCategory(dto.getCategory());
        notice.setImportant(dto.getImportant());
        return notice;
    }
}