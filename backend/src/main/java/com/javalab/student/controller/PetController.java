package com.javalab.student.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.javalab.student.dto.PetDto;
import com.javalab.student.dto.SubstanceDto;
import com.javalab.student.service.PetService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
@Slf4j
public class PetController {
    private final PetService petService;

    @Value("${petImgLocation}")
    private String petUploadPath;

    @GetMapping("/image/{filename:.+}")
public ResponseEntity<Resource> getPetImage(@PathVariable("filename") String filename) {
    log.info("Image request received for filename: {}", filename);
    
    try {
        Path filePath = Paths.get(petUploadPath).resolve(filename).normalize();
        
        if (!Files.exists(filePath)) {
            log.error("Image file not found at path: {}", filePath);
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(filePath.toUri());
        String contentType = Files.probeContentType(filePath);
        
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .body(resource);
            
    } catch (Exception e) {
        log.error("Error serving image: {}", e.getMessage());
        return ResponseEntity.internalServerError().build();
    }
}

    

    // 특정 사용자의 반려동물 목록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PetDto>> getPetsByUser(
            @PathVariable("userId") Long userId,
            @PageableDefault(size = 10, sort = "petId") Pageable pageable) {
        try {
            Page<PetDto> pets = petService.getPetsByUser(userId, pageable);
            return ResponseEntity.ok(pets);
        } catch (Exception e) {
            log.error("Error fetching pets for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 특정 반려동물 상세 조회
    @GetMapping("/{petId}")
public ResponseEntity<PetDto> getPetDetails(@PathVariable("petId") Long petId) {
    try {
        PetDto petDto = petService.getPetDetails(petId);
        return ResponseEntity.ok(petDto);
    } catch (EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 404 Not Found
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 500 Internal Server Error
    }
}

@GetMapping("/substances")
public ResponseEntity<List<SubstanceDto>> getAllSubstances() {
    return ResponseEntity.ok(petService.getAllSubstances());
}


    // 반려동물 등록 (이미지 포함)
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<PetDto> registerPet(
        @RequestParam(value = "image", required = false) MultipartFile image,
        @RequestParam("name") String name,
        @RequestParam("breed") String breed,
        @RequestParam("age") Integer age,
        @RequestParam("weight") Integer weight,
        @RequestParam("gender") String gender,
        @RequestParam("userId") Long userId,
        @RequestParam(value = "neutered", required = false) Boolean neutered,
        @RequestParam(value = "healthConditions", required = false) String healthConditions,
        @RequestParam(value = "allergyIds", required = false) List<Long> allergyIds) {

    log.info("이미지 파일: {}", image != null ? image.getOriginalFilename() : "없음");

    try {
        PetDto petDto = PetDto.builder()
                .name(name)
                .breed(breed)
                .age(age)
                .weight(weight)
                .gender(gender)
                .userId(userId)
                .neutered(neutered)
                .healthConditions(healthConditions)
                .allergyIds(allergyIds) 
                .build();

        PetDto savedPet = petService.registerPet(petDto, image);
        return ResponseEntity.ok(savedPet);

    } catch (RuntimeException e) {
        log.error("펫 등록 중 오류 발생: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 500 Internal Server Error
    }
}



    // 반려동물 정보 수정 (이미지 포함)
    @PutMapping(value = "/{petId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
@PreAuthorize("isAuthenticated()")
public ResponseEntity<PetDto> updatePet(
        @PathVariable("petId") Long petId,
        @RequestParam(value = "image", required = false) MultipartFile image,
        @RequestParam("name") String name,
        @RequestParam("breed") String breed,
        @RequestParam("age") Integer age,
        @RequestParam("weight") Integer weight,
        @RequestParam("gender") String gender,
        @RequestParam("userId") Long userId,
        @RequestParam(value = "neutered", required = false) Boolean neutered,
        @RequestParam(value = "healthConditions", required = false) String healthConditions,
        Authentication authentication) {
    
    log.info("Authentication Principal: {}", authentication.getPrincipal());
    
    // principal이 String인 경우 (이메일)
    String userEmail = authentication.getName();
    log.info("User email from authentication: {}", userEmail);
    
    // 사용자 검증 로직
    try {
        PetDto petDto = PetDto.builder()
                .petId(petId)
                .name(name)
                .breed(breed)
                .age(age)
                .weight(weight)
                .gender(gender)
                .userId(userId)
                .neutered(neutered)
                .healthConditions(healthConditions)
                .build();
        
        if (image != null && !image.isEmpty()) {
            petService.updatePetImage(petId, image);
        }
        
        return ResponseEntity.ok(petService.updatePet(petId, petDto, image));
    } catch (Exception e) {
        log.error("Error updating pet: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

    // 반려동물 삭제
    @DeleteMapping("/{petId}")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<Void> deletePet(
    @PathVariable("petId") Long petId,  // 파라미터 이름 명시적 지정
    Authentication authentication
) {
    try {
        // authentication에서 이메일 가져오기
        String userEmail = authentication.getName();
        // 서비스에서 이메일로 사용자 ID 조회하거나 필요한 검증 수행
        petService.deletePet(petId, userEmail);
        return ResponseEntity.noContent().build();
    } catch (Exception e) {
        log.error("Error deleting pet: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

}