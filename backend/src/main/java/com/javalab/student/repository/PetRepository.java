package com.javalab.student.repository;

import com.javalab.student.entity.Pet;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    // 특정 사용자의 반려동물 목록 조회 (페이징 지원)
    Page<Pet> findByUserId(Long userId, Pageable pageable);
    List<Pet> findAllByUserId(Long userId);
}