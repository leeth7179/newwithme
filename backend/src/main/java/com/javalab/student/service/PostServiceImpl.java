package com.javalab.student.service;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.javalab.student.dto.CommentDto;
import com.javalab.student.dto.PostDto;
import com.javalab.student.entity.Comment;
import com.javalab.student.entity.Member;
import com.javalab.student.entity.Post;
import com.javalab.student.repository.MemberRepository;
import com.javalab.student.repository.PostRepository;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

@Service
@Slf4j
public class PostServiceImpl implements PostService {
    // 의존성 주입을 위한 필드
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    @Value("${postImgLocation}")
    private String postUploadPath;
    

    @Override
    public PostDto createPost(PostDto postDto) {
        Post post = modelMapper.map(postDto, Post.class);
        post.setViews(0);
        Post savedPost = postRepository.save(post);
        return modelMapper.map(savedPost, PostDto.class);
    }

    // 생성자 주입
    public PostServiceImpl(PostRepository postRepository, MemberRepository memberRepository, ModelMapper modelMapper) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
        configureModelMapper();
    }

    // ModelMapper 설정
    private void configureModelMapper() {
        modelMapper.typeMap(Comment.class, CommentDto.class).addMappings(mapper -> {
            mapper.map(src -> src.getPost().getId(), CommentDto::setPostId);
            mapper.map(src -> src.getParentComment() != null ? src.getParentComment().getId() : null,
                    CommentDto::setParentCommentId);
        });
    }

    // 게시글 목록 조회 (페이징, 정렬 적용)
    @Override
    public Page<PostDto> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable).map(this::entityToDto);
    }

    // 게시글 상세 조회
    @Override
    @Transactional(readOnly = true)
    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + id));
        return modelMapper.map(post, PostDto.class);
    }

    // 게시글 조회수 증가 및 조회
    @Override
    @Transactional
    public PostDto increaseViewsAndGet(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + id));
        
        post.setViews(post.getViews() + 1);
        Post savedPost = postRepository.save(post);
        
        return modelMapper.map(savedPost, PostDto.class);
    }

    // 게시글 생성
    @Override
    @Transactional
    public PostDto createPost(PostDto postDto, MultipartFile image) {
        Post post = modelMapper.map(postDto, Post.class);
        post.setViews(0);
    
        // 이미지 처리
        if (image != null && !image.isEmpty()) {
            try {
                // 원본 이미지 저장
                String originalFilename = image.getOriginalFilename();
                String fileName = System.currentTimeMillis() + "_" + originalFilename;
                Path targetLocation = Paths.get(postUploadPath).resolve(fileName);
                Files.copy(image.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                
                // 썸네일 생성
                String thumbnailName = "thumb_" + fileName;
                Path thumbnailPath = Paths.get(postUploadPath).resolve(thumbnailName);
                
                Thumbnails.of(targetLocation.toFile())
                    .size(300, 300) // 썸네일 크기 조정
                    .toFile(thumbnailPath.toFile());
                
                // 이미지 정보 저장
                post.setImageUrl("/api/posts/image/" + fileName);
                post.setThumbnailUrl("/api/posts/image/" + thumbnailName); // 썸네일 URL 설정
            } catch (IOException e) {
                log.error("이미지 업로드 중 오류 발생: {}", e.getMessage());
                throw new RuntimeException("이미지 업로드 중 오류 발생", e);
            }
        }
    
        Post savedPost = postRepository.save(post);
        return modelMapper.map(savedPost, PostDto.class);
    }

    // 게시글 수정
    @Override
    @Transactional
    public PostDto updatePost(Long id, PostDto postDto, Long userId) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + id));
    
        if (!existingPost.getUserId().equals(userId)) {
            throw new SecurityException("작성자만 수정할 수 있습니다.");
        }
    
        // content null 체크 및 기본값 설정
        if (postDto.getContent() == null || postDto.getContent().trim().isEmpty()) {
            postDto.setContent("<p></p>");
        }
    
        // title null 체크
        if (postDto.getTitle() == null || postDto.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("제목은 필수 입력 항목입니다.");
        }
    
        existingPost.setTitle(postDto.getTitle().trim());
        existingPost.setContent(postDto.getContent().trim());
        
        // postCategory가 null이 아닌 경우에만 업데이트
        if (postDto.getPostCategory() != null) {
            existingPost.setPostCategory(postDto.getPostCategory());
        }
    
        // 이미지 URL 업데이트 (있는 경우에만)
        if (postDto.getImageUrl() != null) {
            existingPost.setImageUrl(postDto.getImageUrl());
        }
        if (postDto.getThumbnailUrl() != null) {
            existingPost.setThumbnailUrl(postDto.getThumbnailUrl());
        }
    
        try {
            Post savedPost = postRepository.save(existingPost);
            return modelMapper.map(savedPost, PostDto.class);
        } catch (Exception e) {
            log.error("게시글 수정 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("게시글 수정 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    @Transactional
    public PostDto updatePost(Long id, PostDto postDto, MultipartFile image, Long userId) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + id));

        if (!existingPost.getUserId().equals(userId)) {
            throw new SecurityException("작성자만 수정할 수 있습니다.");
        }

        existingPost.setTitle(postDto.getTitle());
        existingPost.setContent(postDto.getContent());
        existingPost.setPostCategory(postDto.getPostCategory());

        // 이미지 업데이트 처리
        if (image != null && !image.isEmpty()) {
            try {
                // 기존 이미지 삭제
                if (existingPost.getImageUrl() != null) {
                    Path oldImagePath = Paths.get(postUploadPath).resolve(extractFileName(existingPost.getImageUrl()));
                    Path oldThumbnailPath = Paths.get(postUploadPath).resolve(extractFileName(existingPost.getThumbnailUrl()));
                    Files.deleteIfExists(oldImagePath);
                    Files.deleteIfExists(oldThumbnailPath);
                }

                // 새 이미지 저장
                String originalFilename = image.getOriginalFilename();
                String fileName = System.currentTimeMillis() + "_" + originalFilename;
                Path targetLocation = Paths.get(postUploadPath).resolve(fileName);
                Files.copy(image.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                
                // 썸네일 생성
                String thumbnailName = "thumb_" + fileName;
                Path thumbnailPath = Paths.get(postUploadPath).resolve(thumbnailName);
                
                Thumbnails.of(targetLocation.toFile())
                    .size(300, 300) // 썸네일 크기 조정
                    .toFile(thumbnailPath.toFile());
                
                // 이미지 정보 업데이트
                existingPost.setImageUrl("/api/posts/image/" + fileName);
                existingPost.setThumbnailUrl("/api/posts/image/" + thumbnailName);
            } catch (IOException e) {
                log.error("이미지 업로드 중 오류 발생: {}", e.getMessage());
                throw new RuntimeException("이미지 업로드 중 오류 발생", e);
            }
        }

        Post savedPost = postRepository.save(existingPost);
        return modelMapper.map(savedPost, PostDto.class);
    }




     // 파일 이름 추출 유틸리티 메서드
     private String extractFileName(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        try {
            return url.substring(url.lastIndexOf("/") + 1);
        } catch (StringIndexOutOfBoundsException e) {
            log.warn("잘못된 파일 URL 형식: {}", url);
            return null;
        }
    }

     // 게시글 삭제 시 이미지 삭제 로직 추가
     @Override
@Transactional
public void deletePost(Long id, Long userId) {
    try {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + id));

        if (!existingPost.getUserId().equals(userId)) {
            throw new SecurityException("작성자만 삭제할 수 있습니다.");
        }

        // 이미지 삭제 로직
        if (existingPost.getImageUrl() != null) {
            try {
                String imageFileName = extractFileName(existingPost.getImageUrl());
                String thumbnailFileName = extractFileName(existingPost.getThumbnailUrl());
                
                if (imageFileName != null && !imageFileName.isEmpty()) {
                    Path imagePath = Paths.get(postUploadPath).resolve(imageFileName);
                    Files.deleteIfExists(imagePath);
                }
                
                if (thumbnailFileName != null && !thumbnailFileName.isEmpty()) {
                    Path thumbnailPath = Paths.get(postUploadPath).resolve(thumbnailFileName);
                    Files.deleteIfExists(thumbnailPath);
                }
            } catch (IOException e) {
                log.error("이미지 파일 삭제 중 오류 발생: {}", e.getMessage());
                // 이미지 삭제 실패해도 게시글은 삭제 진행
            }
        }

        // 연관된 댓글이 있는 경우 (cascading이 설정되어 있지만 명시적으로 처리)
        if (existingPost.getComments() != null && !existingPost.getComments().isEmpty()) {
            log.info("게시글 {} 의 연관된 댓글 {} 개가 함께 삭제됩니다.", id, existingPost.getComments().size());
        }

        postRepository.delete(existingPost);
        log.info("게시글 {} 이 성공적으로 삭제되었습니다.", id);
        
    } catch (Exception e) {
        log.error("게시글 삭제 중 오류 발생: {}", e.getMessage());
        throw new RuntimeException("게시글 삭제 중 오류가 발생했습니다.", e);
    }
}


    // 이메일로 사용자 ID 조회
    @Override
    @Transactional(readOnly = true)
    public Long getUserIdByEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new IllegalArgumentException("해당 이메일의 사용자를 찾을 수 없습니다: " + email);
        }
        return member.getId();
    }

    @Override
@Transactional(readOnly = true)
public Page<PostDto> getPostsByUserId(Long userId, Pageable pageable) {
    Page<Post> posts = postRepository.findByUserId(userId, pageable);
    return posts.map(this::entityToDto);
}

    // Entity를 DTO로 변환하는 유틸리티 메서드
    private PostDto entityToDto(Post post) {
        return PostDto.builder()
            .id(post.getId())
            .userId(post.getUserId())
            .title(post.getTitle())
            .content(post.getContent())
            .postCategory(post.getPostCategory())
            .views(post.getViews())
            .regTime(post.getRegTime())
            .updateTime(post.getUpdateTime())
            .thumbnailUrl(post.getThumbnailUrl()) 
            .imageUrl(post.getImageUrl()) 
            .build();
    }
}