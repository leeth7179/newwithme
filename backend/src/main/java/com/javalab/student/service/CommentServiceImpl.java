package com.javalab.student.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javalab.student.dto.CommentDto;
import com.javalab.student.entity.Comment;
import com.javalab.student.entity.Member;
import com.javalab.student.entity.Post;
import com.javalab.student.repository.CommentRepository;
import com.javalab.student.repository.MemberRepository;
import com.javalab.student.repository.PostRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {
    
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    // 생성자 추가
    public CommentServiceImpl(CommentRepository commentRepository,
                            PostRepository postRepository,
                            MemberRepository memberRepository,
                            ModelMapper modelMapper) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
    }

    // 사용자별 댓글 조회 구현 추가
    @Override
    @Transactional(readOnly = true)
    public Page<CommentDto> getCommentsByUserId(Long userId, Pageable pageable) {
        try {
            Page<Comment> comments = commentRepository.findByUserId(userId, pageable);
            log.info("Found {} comments for user {}", comments.getContent().size(), userId);
            return comments.map(comment -> modelMapper.map(comment, CommentDto.class));
        } catch (Exception e) {
            log.error("Error fetching comments for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to fetch comments for user", e);
        }
    }

    @Override
    public List<CommentDto> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPost_IdAndParentCommentIsNull(postId);
        return comments.stream()
            .map(comment -> {
                CommentDto dto = modelMapper.map(comment, CommentDto.class);
                dto.setReplies(getChildComments(comment));
                return dto;
            })
            .collect(Collectors.toList());
    }

    private List<CommentDto> getChildComments(Comment parentComment) {
        List<Comment> childComments = commentRepository.findByParentComment(parentComment);
        return childComments.stream()
            .map(comment -> modelMapper.map(comment, CommentDto.class))
            .collect(Collectors.toList());
    }
    
    private CommentDto convertToDto(Comment comment) {
        List<CommentDto> replies = comment.getReplies() != null ?
            comment.getReplies().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList()) : null;

        return CommentDto.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .userId(comment.getUserId())
                .userName(comment.getUserName())
                .content(comment.getContent())
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .replies(replies)
                .regTime(comment.getRegTime())
                .updateTime(comment.getUpdateTime())
                .build();
    }

    @Override
@Transactional
public CommentDto createComment(CommentDto commentDto) {
    try {
        Post post = postRepository.findById(commentDto.getPostId())
            .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        Member member = memberRepository.findById(commentDto.getUserId())
            .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setPost(post);
        comment.setUserId(commentDto.getUserId());
        comment.setUserName(commentDto.getUserName());

        // 부모 댓글 ID가 null이 아니고 0이 아닐 때만 부모 댓글 찾기
        if (commentDto.getParentCommentId() != null && commentDto.getParentCommentId() != 0) {
            Comment parentComment = commentRepository.findById(commentDto.getParentCommentId())
                .orElseThrow(() -> new EntityNotFoundException("부모 댓글을 찾을 수 없습니다."));
            comment.setParentComment(parentComment);
        }

        Comment savedComment = commentRepository.save(comment);
        return convertToDto(savedComment);
    } catch (Exception e) {
        log.error("댓글 생성 중 오류 발생: {}", e.getMessage(), e);
        throw new RuntimeException("댓글 생성에 실패했습니다.", e);
    }
}

    @Override
    @Transactional
    public CommentDto updateComment(CommentDto commentDto, Long userId) {
        try {
            Comment comment = commentRepository.findById(commentDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

            if (!comment.getUserId().equals(userId)) {
                throw new AccessDeniedException("댓글 수정 권한이 없습니다.");
            }

            comment.setContent(commentDto.getContent());
            Comment updatedComment = commentRepository.save(comment);
            return convertToDto(updatedComment);
        } catch (Exception e) {
            log.error("댓글 수정 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("댓글 수정에 실패했습니다.", e);
        }
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        try {
            Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

            if (!comment.getUserId().equals(userId)) {
                throw new AccessDeniedException("댓글 삭제 권한이 없습니다.");
            }

            if (!comment.getReplies().isEmpty()) {
                commentRepository.delete(comment);
            } else {
                commentRepository.delete(comment);
            }
        } catch (EntityNotFoundException | AccessDeniedException e) {
            log.error("댓글 삭제 중 오류 발생: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("댓글 삭제 중 예기치 않은 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("댓글 삭제에 실패했습니다.", e);
        }
    }
}