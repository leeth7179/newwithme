package com.javalab.student.elasticsearch.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.javalab.student.elasticsearch.entity.ItemSearchElasticsearch;

public interface ItemSearchElasticsearchRepository extends ElasticsearchRepository<ItemSearchElasticsearch, String> {
    List<ItemSearchElasticsearch> findByNameOrDescriptionContaining(String keyword);
}