package com.javalab.student.repository;

import com.javalab.student.entity.Choice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 선택지 Repository
 * Choice 엔티티에 대한 CRUD 작업을 처리하는 리포지토리
 */
@Repository
public interface ChoiceRepository extends JpaRepository<Choice, Long> {
    /**
     * ✅ 특정 질문 ID에 해당하는 선택지 조회
     */
    List<Choice> findByQuestion_QuestionId(Long questionId); // ✅ 변경 불필요
}