package com.javalab.student.service;

import com.javalab.student.dto.PetDto;
import com.javalab.student.dto.SubstanceDto;
import com.javalab.student.entity.Member;
import com.javalab.student.entity.Pet;
import com.javalab.student.entity.PetAllergy;
import com.javalab.student.repository.MemberRepository;
import com.javalab.student.repository.PetRepository;
import com.javalab.student.service.PetService;
import com.javalab.student.entity.Substance;
import com.javalab.student.repository.PetAllergyRepository;
import com.javalab.student.repository.SubstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class PetServiceImpl implements PetService {
    
    private final PetRepository petRepository;
    private final MemberRepository memberRepository;
    private final PetAllergyRepository petAllergyRepository;
    private final SubstanceRepository substanceRepository;
    private final ModelMapper modelMapper;

    @Value("${petImgLocation}")
    private String petUploadPath;

    @Override
public String uploadPetImage(MultipartFile file) {
    try {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        // 파일 저장 경로 생성
        String originalFilename = file.getOriginalFilename();
        String fileName = System.currentTimeMillis() + "_" + originalFilename;
        Path filePath = Paths.get(petUploadPath, fileName);

        // 파일 저장
        Files.copy(file.getInputStream(), filePath);

        return fileName; // 저장된 파일 이름 반환
    } catch (IOException e) {
        log.error("이미지 업로드 중 오류 발생: {}", e.getMessage());
        throw new RuntimeException("이미지 업로드 중 오류 발생", e);
    }
}

    

    @Override
    public void updatePetImage(Long petId, MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            // 이미지 업로드 및 경로 반환
            String uploadedFileName = uploadPetImage(image);

            // 반려동물 엔티티 조회 및 업데이트
            Pet pet = petRepository.findById(petId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 ID의 반려동물이 존재하지 않습니다."));
            pet.setImageUrl(uploadedFileName); // 이미지 URL 업데이트
            petRepository.save(pet);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PetDto> getPetsByUser(Long userId, Pageable pageable) {
        try {
            Page<Pet> pets = petRepository.findByUserId(userId, pageable);
            log.info("Found {} pets for user {}", pets.getContent().size(), userId);
            return pets.map(pet -> modelMapper.map(pet, PetDto.class));
        } catch (Exception e) {
            log.error("Error fetching pets for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to fetch pets for user", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PetDto getPetDetails(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found with id: " + petId));
        
        PetDto petDto = modelMapper.map(pet, PetDto.class);
        
        // 이미지 URL이 있는 경우 로그로 확인
        if (petDto.getImageUrl() != null) {
            log.debug("Pet image URL: {}", petDto.getImageUrl());
        }
        
        return petDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubstanceDto> getAllSubstances() {
        return substanceRepository.findAll().stream()
                .map(substance -> SubstanceDto.builder()
                        .substanceId(substance.getSubstanceId())
                        .name(substance.getName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void savePetAllergies(Long petId, List<Long> allergyIds) {
        // 기존 알러지 정보 삭제
        petAllergyRepository.deleteByPetId(petId);

        // 새로운 알러지 정보 저장
        if (allergyIds != null && !allergyIds.isEmpty()) {
            allergyIds.forEach(substanceId -> {
                PetAllergy petAllergy = new PetAllergy();
                petAllergy.setPetId(petId);
                petAllergy.setSubstanceId(substanceId);
                petAllergyRepository.save(petAllergy);
            });
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubstanceDto> getPetAllergies(Long petId) {
        List<Long> substanceIds = petAllergyRepository.findSubstanceIdsByPetId(petId);
        return substanceRepository.findAllById(substanceIds).stream()
                .map(substance -> SubstanceDto.builder()
                        .substanceId(substance.getSubstanceId())
                        .name(substance.getName())
                        .build())
                .collect(Collectors.toList());
    }




@Override
@Transactional
public PetDto registerPet(PetDto petDto, MultipartFile image) {
    try {
        Pet pet = modelMapper.map(petDto, Pet.class);

        // 이미지 업로드 처리
        if (image != null && !image.isEmpty()) {
            String originalFilename = image.getOriginalFilename();
            String fileName = System.currentTimeMillis() + "_" + originalFilename;

            Path filePath = Paths.get(petUploadPath, fileName);

            // 파일 저장
            Files.copy(image.getInputStream(), filePath);

            // 이미지 URL 생성 (중복 방지)
            pet.setImageUrl("/api/pets/image/" + fileName); // API 경로 설정
            pet.setImageName(fileName); // 실제 파일 이름 저장
        }

        Pet savedPet = petRepository.save(pet);

         // 알러지 정보 저장
         if (petDto.getAllergyIds() != null && !petDto.getAllergyIds().isEmpty()) {
            savePetAllergies(savedPet.getPetId(), petDto.getAllergyIds());
        }
        
        return modelMapper.map(savedPet, PetDto.class);
    } catch (IOException e) {
        log.error("이미지 업로드 중 오류 발생: {}", e.getMessage());
        throw new RuntimeException("이미지 업로드 중 오류 발생", e);
    }
}



@Override
@Transactional
public PetDto updatePet(Long petId, PetDto petDto, MultipartFile image) {
    Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> new EntityNotFoundException("Pet not found with id: " + petId));

    // 기본 정보 업데이트
    pet.setName(petDto.getName());
    pet.setBreed(petDto.getBreed());
    pet.setAge(petDto.getAge());
    pet.setWeight(petDto.getWeight());
    pet.setGender(petDto.getGender());
    pet.setNeutered(petDto.getNeutered());
    pet.setHealthConditions(petDto.getHealthConditions());

    // 이미지 업데이트 처리
    if (image != null && !image.isEmpty()) {
        // 기존 이미지 삭제
        if (pet.getImageUrl() != null) {
            Path oldImagePath = Paths.get(petUploadPath, pet.getImageName());
            try {
                Files.deleteIfExists(oldImagePath);
            } catch (IOException e) {
                log.warn("기존 이미지를 삭제하지 못했습니다: {}", e.getMessage());
            }
        }

        // 새 이미지 업로드 및 설정
        String originalFilename = image.getOriginalFilename();
        String fileName = System.currentTimeMillis() + "_" + originalFilename;

        Path filePath = Paths.get(petUploadPath, fileName);
        try {
            image.transferTo(filePath.toFile());
        } catch (IOException e) {
            log.error("이미지 업로드 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("이미지 업로드 중 오류 발생", e);
        }

        pet.setImageUrl("/api/pets/image/" + fileName); // API 경로 설정
        pet.setImageName(fileName); // 실제 파일 이름 저장
    }

    Pet updatedPet = petRepository.save(pet);
    return modelMapper.map(updatedPet, PetDto.class);
}

    
@Override
@Transactional
public void deletePet(Long petId, String userEmail) {
    // Pet 조회
    Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> new EntityNotFoundException("Pet not found with id: " + petId));
    
    // Member 조회 (null 처리)
    Member member = memberRepository.findByEmail(userEmail);
    if (member == null) {
        throw new EntityNotFoundException("User not found with email: " + userEmail);
    }
    
    // 사용자 검증
    if (!pet.getUserId().equals(member.getId())) {
        throw new AccessDeniedException("사용자가 해당 반려동물을 삭제할 권한이 없습니다.");
    }
    
    // 이미지 삭제
    if (pet.getImageUrl() != null) {
        deleteExistingImage(pet.getImageUrl());
    }
    
    // Pet 삭제
    petRepository.delete(pet);
}



    private String saveImage(MultipartFile image) {
        try {
            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            Path targetLocation = Paths.get(petUploadPath).resolve(fileName);
            Files.copy(image.getInputStream(), targetLocation);
            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store image " + image.getOriginalFilename(), ex);
        }
    }

    private void deleteExistingImage(String imageUrl) {
        try {
            Path imagePath = Paths.get(petUploadPath).resolve(imageUrl);
            Files.deleteIfExists(imagePath);
        } catch (IOException ex) {
            throw new RuntimeException("Could not delete image " + imageUrl, ex);
        }
    }
}