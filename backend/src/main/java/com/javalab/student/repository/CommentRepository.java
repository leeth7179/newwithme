package com.javalab.student.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.javalab.student.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId); // 특정 게시글의 모든 댓글 조회

    List<Comment> findByParentCommentId(Comment parent); // 특정 부모 댓글에 속한 대댓글 조회

    Optional<Comment> findByIdAndPostIdAndUserId(Long commentId, Long postId, Long userId);

    List<Comment> findByPost_IdAndParentCommentIsNull(Long postId);
    List<Comment> findByParentComment(Comment parentComment);

    // 사용자별 댓글 조회
    Page<Comment> findByUserId(Long userId, Pageable pageable);

}
