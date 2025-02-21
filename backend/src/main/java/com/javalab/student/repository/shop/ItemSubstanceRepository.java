package com.javalab.student.repository.shop;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;

import com.javalab.student.entity.shop.ItemSubstance; 


@Repository
public interface ItemSubstanceRepository extends JpaRepository<ItemSubstance, Long> {
   // 특정 알러지 성분들에 대해 안전하다고 등록된 상품들 조회
   @Query("SELECT DISTINCT is.item.id FROM ItemSubstance is WHERE is.substance.id IN :substanceIds")
   List<Long> findSafeItemIds(@Param("substanceIds") List<Long> substanceIds);

   // 특정 상품의 알러지 성분 ID 목록 조회
   @Query("SELECT is.substance.id FROM ItemSubstance is WHERE is.item.id = :itemId")
    List<Long> findSubstanceIdsByItemId(@Param("itemId") Long itemId);

   // 특정 상품의 알러지 정보 삭제
   @Modifying
    @Query("DELETE FROM ItemSubstance is WHERE is.item.id = :itemId")
    void deleteByItemId(@Param("itemId") Long itemId);

    List<ItemSubstance> findByItemId(Long itemId);
}