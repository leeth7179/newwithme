package com.javalab.student.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.javalab.student.entity.Substance;

@Repository
public interface SubstanceRepository extends JpaRepository<Substance, Long> {
    // 모든 알러지 성분 조회 (기본 제공되는 findAll() 사용)

    // 이름으로 알러지 성분 검색
    List<Substance> findByNameContaining(String name);

    // ID 목록으로 알러지 성분들 조회 (기본 제공되는 findAllById() 사용)

    // 카테고리별 알러지 성분 조회를 위한 쿼리 추가 가능
    /*
    @Query("SELECT s FROM Substance s WHERE s.category = :category")
    List<Substance> findByCategory(String category);
    */
}