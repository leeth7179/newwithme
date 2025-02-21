package com.javalab.student.service;

import com.javalab.student.entity.Member;
import com.javalab.student.entity.Response;
import com.javalab.student.repository.MemberRepository;
import com.javalab.student.repository.ResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 응답 서비스
 * 설문에 대한 응답을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor  // 생성자 주입 자동 생성
public class ResponseService {

    private final ResponseRepository responseRepository;
    private final MemberRepository memberRepository;

    /**
     * 모든 응답 조회 (디버깅용)
     */
    @Transactional(readOnly = true)
    public List<Response> getAllResponses() {
        return responseRepository.findAll();
    }

    /**
     * 응답 생성
     */
    @Transactional
    public Response createResponse(Response response, Long userId) {
        // userId로 Member 조회
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        // 응답 객체에 유저 설정
        response.setUser(user);  // Response 객체에 User를 설정하는 로직

        // 응답을 저장하고 반환
        return responseRepository.save(response);
    }

    /**
     * 특정 userId 기반으로 응답 조회
     * 수정된 부분: `findByUserId()` 메서드 사용
     */
    @Transactional(readOnly = true)
    public List<Response> getResponsesByUserId(Long userId) {
        return responseRepository.findByUser_Id(userId);  // `findByUser_Id` 사용
    }
}
