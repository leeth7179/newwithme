package com.javalab.student.elasticsearch.document;

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
public class ItemSearchDocument {  // 클래스명 변경
    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")  // nori 대신 standard 분석기 사용
    private String name;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Text)
    private String category;  // 카테고리 필드 추가

    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<Substance> substances;
}