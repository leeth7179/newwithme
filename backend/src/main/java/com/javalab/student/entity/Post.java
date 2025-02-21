package com.javalab.student.entity;

import lombok.*;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "views", nullable = false)
    private int views;

    @Column(name = "post_category")
    private String postCategory;

    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    // BaseTimeEntity의 regTime, updateTime을 상속받음
    // 기존의 createdAt, updatedAt메서드는 제거

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments;
}