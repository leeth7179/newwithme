package com.javalab.student.entity;

import lombok.*;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")// 댓글 ID
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false) // 게시글 ID와 연결
    private Post post;

    @Column(name = "user_id", nullable = false) // 댓글 작성자 ID
    private Long userId;

    @Column(name = "user_name", nullable = false) // 댓글 작성자 name
    private String userName;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT") // 댓글 내용
    private String content;

    // BaseTimeEntity의 regTime, updateTime을 상속받음
    // 기존의 createdAt, updatedAt메서드는 제거

    // 부모 댓글 (대댓글인 경우 부모 댓글과 연결)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id") // 부모 댓글 ID를 나타내는 컬럼 이름 설정
    private Comment parentComment;

    // 자식 댓글 (해당 댓글에 달린 대댓글 목록)
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> replies;
}
