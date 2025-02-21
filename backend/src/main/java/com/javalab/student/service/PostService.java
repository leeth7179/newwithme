package com.javalab.student.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.javalab.student.dto.PostDto;

public interface PostService {
    PostDto createPost(PostDto postDto);
    PostDto createPost(PostDto postDto, MultipartFile image);
    
    Page<PostDto> getAllPosts(Pageable pageable);
    PostDto getPostById(Long id);

    PostDto updatePost(Long id, PostDto postDto, Long userId);
    PostDto updatePost(Long id, PostDto postDto, MultipartFile image, Long userId);
    
    void deletePost(Long id, Long userId);

    // 이메일로 사용자 ID 조회 메서드 추가
    Long getUserIdByEmail(String email);

    PostDto increaseViewsAndGet(Long id);

    //사용자별 게시글 조회
    Page<PostDto> getPostsByUserId(Long userId, Pageable pageable);
}