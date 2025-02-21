package com.javalab.student.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.javalab.student.dto.CommentDto;
import com.javalab.student.dto.PostDto;
import com.javalab.student.service.CommentService;
import com.javalab.student.service.PostService;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;


@RestController
@RequestMapping("/api/posts")
@PreAuthorize("isAuthenticated()") // 인증된 사용자만 접근 가능
@Slf4j

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1MB
    maxFileSize = 1024 * 1024 * 10,  // 10MB
    maxRequestSize = 1024 * 1024 * 10 // 10MB
)

public class PostController {

    @Value("${postImgLocation}")
    private String postUploadPath;

    private final PostService postService;
    private final CommentService commentService;
    

    public PostController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

     // 이미지 리소스 제공 메서드
     @GetMapping("/image/{filename}")
     public ResponseEntity<Resource> serveImage(@PathVariable String filename) throws IOException {
         Path filePath = Paths.get(postUploadPath).resolve(filename);
         Resource resource = new UrlResource(filePath.toUri());
     
         if (resource.exists() && resource.isReadable()) {
             return ResponseEntity.ok()
                 .contentType(MediaType.parseMediaType(determineContentType(filename)))
                 .body(resource);
         } else {
             return ResponseEntity.notFound().build();
         }
     }

     private String determineContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            default:
                return "application/octet-stream";
        }
    }

    // 모든 게시글 조회
    @GetMapping
    public ResponseEntity<?> getAllPosts(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("regTime").descending());
        Page<PostDto> posts = postService.getAllPosts(pageRequest);
        return ResponseEntity.ok(Map.of("total", posts.getTotalElements(), "posts", posts.getContent()));
    }
    

    // 특정 게시글 조회 (조회수 증가 포함)
    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPostById(@PathVariable("postId") Long postId) {
        PostDto postDto = postService.increaseViewsAndGet(postId);
        return ResponseEntity.ok(postDto);
    }

    // 특정 게시글 댓글
    @GetMapping("/{postId}/comments")
public ResponseEntity<List<CommentDto>> getCommentsByPostId(
    @PathVariable("postId") Long postId
) {
    List<CommentDto> comments = commentService.getCommentsByPostId(postId);
    return ResponseEntity.ok(comments);
}
    // 게시글 생성
    @PostMapping
public ResponseEntity<?> createPost(
    @RequestBody PostDto postDto,
    Principal principal
) {
    // 기본 유효성 검사
    if (postDto.getContent() == null || postDto.getContent().trim().isEmpty()) {
        return ResponseEntity.badRequest()
            .body(Map.of("error", "내용을 입력해주세요."));
    }
    
    if (postDto.getTitle() == null || postDto.getTitle().trim().isEmpty()) {
        return ResponseEntity.badRequest()
            .body(Map.of("error", "제목을 입력해주세요."));
    }

    try {
        Long userId = postService.getUserIdByEmail(principal.getName());
        postDto.setUserId(userId);
        
        // content가 비어있는 경우 기본값 설정
        if (postDto.getContent().trim().isEmpty()) {
            postDto.setContent("<p></p>");
        }

        PostDto createdPost = postService.createPost(postDto);
        return ResponseEntity.ok(createdPost);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("error", "게시글 저장 중 오류가 발생했습니다."));
    }
}

    // 게시글 수정
    @PutMapping("/{postId}")
public ResponseEntity<?> updatePost(
    @PathVariable("postId") Long postId,
    @RequestBody PostDto postDto,
    Principal principal
) {
    try {
        if (postDto.getContent() == null || postDto.getContent().trim().isEmpty()) {
            postDto.setContent("<p></p>");
        }
        // 필수 필드 검증
        // if (postDto.getContent() == null || postDto.getContent().trim().isEmpty()) {
        //     return ResponseEntity.badRequest()
        //         .body(Map.of("error", "내용을 입력해주세요."));
        // }
        
        if (postDto.getTitle() == null || postDto.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "제목을 입력해주세요."));
        }

        Long userId = postService.getUserIdByEmail(principal.getName());
        
        // content가 비어있는 경우 기본값 설정
        if (postDto.getContent().trim().isEmpty()) {
            postDto.setContent("<p></p>");
        }

        PostDto updatedPost = postService.updatePost(postId, postDto, userId);
        return ResponseEntity.ok(updatedPost);
        
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "게시글 수정 중 오류가 발생했습니다.: " + e.getMessage()));
    }
}


    // 게시글 삭제
    @DeleteMapping("/{postId}")
public ResponseEntity<Void> deletePost(
    @PathVariable("postId") Long postId,
    Principal principal
) {
    Long userId = postService.getUserIdByEmail(principal.getName());
    postService.deletePost(postId, userId);
    return ResponseEntity.noContent().build();
}


    // 댓글 생성
    @PostMapping("/{postId}/comments")
public ResponseEntity<?> createComment(
    @PathVariable("postId") Long postId, 
    @Valid @RequestBody CommentDto commentDto, 
    Principal principal
) {
    try {
        log.info("댓글 생성 시도 - postId: {}, userId: {}, content: {}", 
            postId, principal.getName(), commentDto.getContent());

        Long userId = postService.getUserIdByEmail(principal.getName());
        commentDto.setPostId(postId);
        commentDto.setUserId(userId);
        
        CommentDto savedComment = commentService.createComment(commentDto);
        
        log.info("댓글 생성 성공 - commentId: {}", savedComment.getId());
        return ResponseEntity.ok(savedComment);
    } catch (Exception e) {
        log.error("댓글 생성 실패 - postId: {}, error: {}", postId, e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("error", "댓글 생성 중 오류가 발생했습니다."));
    }
}

    // 댓글 수정
    @PutMapping("/{postId}/comments/{commentId}")
public ResponseEntity<?> updateComment(
    @PathVariable("postId") Long postId, 
    @PathVariable("commentId") Long commentId, 
    @RequestBody CommentDto commentDto, 
    Principal principal
) {
    try {
        Long userId = postService.getUserIdByEmail(principal.getName());
        commentDto.setId(commentId);
        commentDto.setPostId(postId);
        
        CommentDto updatedComment = commentService.updateComment(commentDto, userId);
        return ResponseEntity.ok(updatedComment);
    } catch (Exception e) {
        log.error("댓글 수정 중 오류 발생", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("error", "댓글 수정 중 오류가 발생했습니다."));
    }
}
//댓글 삭제
@DeleteMapping("/{postId}/comments/{commentId}")
public ResponseEntity<?> deleteComment(
    @PathVariable("postId") Long postId, 
    @PathVariable("commentId") Long commentId, 
    Principal principal
) {
    try {
        Long userId = postService.getUserIdByEmail(principal.getName());
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok().build();
    } catch (Exception e) {
        log.error("댓글 삭제 중 오류 발생", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("error", "댓글 삭제 중 오류가 발생했습니다."));
    }
}

//스마트에디터 이미지 업로드
@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(
        @RequestParam("image") MultipartFile file
    ) {

        log.info("File upload request received");
        log.info("File name: {}", file.getOriginalFilename());
        log.info("File size: {}", file.getSize());
        log.info("Content type: {}", file.getContentType());
    try {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "파일이 비어있습니다."));
        }

        // 원본 이미지 저장
        String originalFileName = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + "_" + originalFileName;
        Path targetLocation = Paths.get(postUploadPath).resolve(fileName);
        
        // 디렉토리 존재 확인 및 생성
        Files.createDirectories(targetLocation.getParent());
        
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        // 썸네일 생성
        String thumbnailName = "thumb_" + fileName;
        Path thumbnailPath = Paths.get(postUploadPath).resolve(thumbnailName);
        
        // Thumbnailator 라이브러리 사용
        Thumbnails.of(targetLocation.toFile())
            .size(300, 300) // 썸네일 크기 조정
            .toFile(thumbnailPath.toFile());
        
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", "/api/posts/image/" + fileName);
            response.put("thumbnailUrl", "/api/posts/image/" + thumbnailName);
        
        return ResponseEntity.ok(response);
    } catch (IOException ex) {
        log.error("이미지 업로드 중 오류 발생", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of(
                "error", "이미지 업로드 중 오류가 발생했습니다.",
                "details", ex.getMessage()
            ));
    }
}




// 사용자별 게시글 조회
@GetMapping("/user/{userId}")
public ResponseEntity<?> getPostsByUserId(
    @PathVariable("userId") Long userId,
    @RequestParam(name = "page", defaultValue = "0") int page,
    @RequestParam(name = "size", defaultValue = "10") int size
) {
    PageRequest pageRequest = PageRequest.of(page, size, Sort.by("regTime").descending());
    Page<PostDto> posts = postService.getPostsByUserId(userId, pageRequest);
    return ResponseEntity.ok(Map.of("total", posts.getTotalElements(), "content", posts.getContent()));
}

}
