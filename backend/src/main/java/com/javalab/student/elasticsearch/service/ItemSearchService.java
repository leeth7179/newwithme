package com.javalab.student.elasticsearch.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.javalab.student.elasticsearch.document.ItemSearchDocument;
import com.javalab.student.elasticsearch.repository.ItemSearchRepository;
import com.javalab.student.repository.PetAllergyRepository;
import com.javalab.student.repository.shop.ItemSubstanceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemSearchService {
    private final ItemSearchRepository searchRepository;
    private final PetAllergyRepository petAllergyRepository;
    private final ItemSubstanceRepository itemSubstanceRepository;

   // 일반 검색
   public List<ItemSearchDocument> searchItems(String keyword) {
    return searchRepository.findByNameOrDescriptionContaining(keyword);
}

    // 반려동물 알러지 기반 필터링된 상품 목록
    public List<ItemSearchDocument> getAllergyFilteredItems(Long petId) {
        List<Long> allergySubstanceIds = petAllergyRepository.findSubstanceIdsByPetId(petId);
        List<Long> safeItemIds = itemSubstanceRepository.findSafeItemIds(allergySubstanceIds);
        return searchRepository.findByItemIds(safeItemIds);
    }

    // 검색어와 반려동물 알러지 모두 적용된 검색
    public List<ItemSearchDocument> searchItemsWithAllergyFilter(String keyword, Long petId) {
        List<Long> allergySubstanceIds = petAllergyRepository.findSubstanceIdsByPetId(petId);
        return searchRepository.findByNameOrDescriptionAndExcludeSubstances(
            keyword,
            allergySubstanceIds
        );
    }
}