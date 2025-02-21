package com.javalab.student.elasticsearch.entity;

import java.util.Set;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.javalab.student.entity.Substance;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Document(indexName = "search_index")
@Getter
@Setter
public class ItemSearchElasticsearch {

    @Id
    private String id; // Elasticsearch에서는 문자열 ID를 사용하는 경우가 많음

    @Field(type = FieldType.Text, analyzer = "korean")
    private String name;

    @Field(type = FieldType.Text, analyzer = "korean")
    private String description;

    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<Substance> substances;
}
