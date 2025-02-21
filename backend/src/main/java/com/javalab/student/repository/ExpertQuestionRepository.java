package com.javalab.student.repository;

import com.javalab.student.entity.ExpertQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpertQuestionRepository extends JpaRepository<ExpertQuestion, Long> {
    List<ExpertQuestion> findAllByMember_Id(Long userId);  // ✅ `findAllByUserId` → `findAllByMember_Id` 변경
}
