package com.javalab.student.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {

    private Long id; // 댓글 ID

    private Long postId; // 댓글이 속한 게시글 ID

    private Long userId; // 댓글 작성자 ID

    private String userName; // 댓글 작성자 name

     @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(max = 500, message = "댓글은 500자 이내로 작성해야 합니다.")
    private String content; // 댓글 내용

     @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime regTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime updateTime;

    private Long parentCommentId; // 부모 댓글 ID (대댓글인 경우 부모 댓글의 ID, 일반 댓글인 경우 null)

    private List<CommentDto> replies; // 대댓글 리스트 (해당 댓글에 달린 대댓글)
}
