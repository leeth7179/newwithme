package com.javalab.student.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.javalab.student.dto.CommentDto;

public interface CommentService {
    List<CommentDto> getCommentsByPostId(Long postId);
    CommentDto createComment(CommentDto commentDto);
    CommentDto updateComment(CommentDto commentDto, Long userId);
    void deleteComment(Long commentId, Long userId);
    Page<CommentDto> getCommentsByUserId(Long userId, Pageable pageable);
}