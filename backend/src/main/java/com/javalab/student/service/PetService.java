package com.javalab.student.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.javalab.student.dto.PetDto;
import com.javalab.student.dto.SubstanceDto;

public interface PetService {
    // 특정 사용자의 반려동물 목록 조회
    Page<PetDto> getPetsByUser(Long userId, Pageable pageable);

    // 특정 반려동물 상세 조회
    PetDto getPetDetails(Long petId);

    // 반려동물 등록
    PetDto registerPet(PetDto petDto, MultipartFile image);

    // 반려동물 정보 수정
    PetDto updatePet(Long petId, PetDto petDto, MultipartFile image);

    // 반려동물 삭제
    void deletePet(Long petId, String userEmail);

    // 이미지 업로드
    String uploadPetImage(MultipartFile file);

// 반려동물 이미지 업데이트
void updatePetImage(Long petId, MultipartFile image);

 // 알러지 성분 목록 조회
    List<SubstanceDto> getAllSubstances();

    // 반려동물의 알러지 정보 저장
    void savePetAllergies(Long petId, List<Long> allergyIds);

    // 반려동물의 알러지 정보 조회
    List<SubstanceDto> getPetAllergies(Long petId);
}