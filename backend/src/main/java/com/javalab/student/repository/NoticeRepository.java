package com.javalab.student.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.javalab.student.entity.Notice;

// Notice 엔티티와 연결된 Repository 인터페이스
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findAllByOrderByCreatedAtDesc();
    Page<Notice> findAllByOrderByImportantDescCreatedAtDesc(Pageable pageable);
}
