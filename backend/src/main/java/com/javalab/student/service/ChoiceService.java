package com.javalab.student.service;

import com.javalab.student.entity.Choice;
import com.javalab.student.repository.ChoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 선택지 서비스
 * 설문 질문에 대한 선택지에 대한 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
public class ChoiceService {

    private final ChoiceRepository choiceRepository;

    @Autowired
    public ChoiceService(ChoiceRepository choiceRepository) {
        this.choiceRepository = choiceRepository;
    }

    /**
     * ✅ 모든 선택지 조회
     */
    public List<Choice> getAllChoices() {
        return choiceRepository.findAll();
    }

    /**
     * ✅ 선택지 ID로 선택지 조회
     */
    public Optional<Choice> getChoiceById(Long choiceId) {
        return choiceRepository.findById(choiceId);
    }

    /**
     * ✅ 특정 질문 ID에 해당하는 선택지 조회
     */
    public List<Choice> getChoicesByQuestionId(Long questionId) {
        return choiceRepository.findByQuestion_QuestionId(questionId);
    }

    /**
     * ✅ 새로운 선택지 생성
     */
    public Choice createChoice(Choice choice) {
        return choiceRepository.save(choice);
    }

    /**
     * ✅ 선택지 삭제
     */
    public void deleteChoice(Long choiceId) {
        choiceRepository.deleteById(choiceId);
    }
}