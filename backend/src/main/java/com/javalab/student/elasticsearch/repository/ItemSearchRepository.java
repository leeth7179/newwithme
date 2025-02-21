package com.javalab.student.elasticsearch.repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.javalab.student.elasticsearch.document.ItemSearchDocument;

@Repository
public interface ItemSearchRepository extends ElasticsearchRepository<ItemSearchDocument, String> {
    // 이름 또는 설명으로 검색 - 하나의 파라미터만 받도록 수정
    @Query("{\"bool\": {\"should\": [{\"match\": {\"name\": \"?0\"}}, {\"match\": {\"description\": \"?0\"}}]}}")
    List<ItemSearchDocument> findByNameOrDescriptionContaining(String keyword);

    // 상품 ID 목록으로 검색
    @Query("{\"bool\": {\"filter\": {\"terms\": {\"item_id\": ?0}}}}")
    List<ItemSearchDocument> findByItemIds(List<Long> itemIds);

    // 검색어로 검색하고 특정 성분들 제외
    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\",\"fields\": [\"name\", \"description\"]}}],\"must_not\": {\"terms\": {\"substance_ids\": ?1}}}}")
    List<ItemSearchDocument> findByNameOrDescriptionAndExcludeSubstances(
        String keyword,
        List<Long> substanceIds
    );
}