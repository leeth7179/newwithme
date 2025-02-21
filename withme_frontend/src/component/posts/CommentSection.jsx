import React, { useState, useEffect } from "react";
import PropTypes from "prop-types";
import { fetchWithAuth } from "../../common/fetchWithAuth";
import { API_URL } from "../../constant";
import CommentComponent from "./CommentComponent";
import "../../assets/css/posts/posts.css";

const CommentSection = ({ postId, user = null, isLoggedIn, views = 0 }) => {
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState("");
  const [showCommentForm, setShowCommentForm] = useState(false);
  const [totalComments, setTotalComments] = useState(0);
  const [isHovered, setIsHovered] = useState(null);
  const [isEditing, setIsEditing] = useState(null);
  const [editContent, setEditContent] = useState("");
  const [showReplyForm, setShowReplyForm] = useState(null);
  const [replyContent, setReplyContent] = useState("");

  // 댓글을 계층 구조로 구성하는 함수
  const structureComments = (flatComments) => {
    const commentMap = {};
    const rootComments = [];

    // 날짜순으로 정렬
    const sortedComments = [...flatComments].sort(
      (a, b) => new Date(a.regTime) - new Date(b.regTime)
    );

    // 먼저 모든 댓글을 map으로 구성
    sortedComments.forEach((comment) => {
      commentMap[comment.id] = {
        ...comment,
        replies: comment.replies || []
      };
    });

    // 댓글들을 순회하면서 부모-자식 관계 구성
    sortedComments.forEach((comment) => {
      if (comment.parentCommentId && comment.parentCommentId !== 0) {
        const parentComment = commentMap[comment.parentCommentId];
        if (parentComment) {
          if (!parentComment.replies) {
            parentComment.replies = [];
          }
          parentComment.replies.push(commentMap[comment.id]);
        } else {
          rootComments.push(commentMap[comment.id]);
        }
      } else {
        rootComments.push(commentMap[comment.id]);
      }
    });

    return rootComments;
  };

  // 전체 댓글 수 계산
  const countTotalComments = (commentsArray) => {
    const countReplies = (comments) => {
      return comments.reduce((total, comment) => {
        // 현재 댓글 카운트
        let count = 1;

        // 대댓글이 있다면 재귀적으로 카운트
        if (comment.replies && comment.replies.length > 0) {
          count += countReplies(comment.replies);
        }

        return total + count;
      }, 0);
    };

    return countReplies(commentsArray || []);
  };

  // 댓글 불러오기
  const fetchComments = async () => {
    try {
      const response = await fetchWithAuth(
        `${API_URL}posts/${postId}/comments`
      );
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
      const data = await response.json();
      const structuredComments = structureComments(data || []);
      setComments(structuredComments);
      setTotalComments(countTotalComments(data || []));
    } catch (error) {
      console.error("댓글 가져오기 실패:", error);
      alert("댓글을 불러오는 중 오류가 발생했습니다.");
    }
  };

  // 새 댓글 작성
  const handleNewCommentSubmit = async (e) => {
    e.preventDefault();
    if (!isLoggedIn) {
      alert("로그인이 필요합니다.");
      return;
    }

    try {
      const response = await fetchWithAuth(
        `${API_URL}posts/${postId}/comments`,
        {
          method: "POST",
          body: JSON.stringify({
            content: newComment,
            postId: Number(postId),
            userId: Number(user.id),
            userName: user.name,
            parentCommentId: null
          })
        }
      );

      if (!response.ok) {
        throw new Error(`Error: ${response.status}`);
      }

      await fetchComments();
      setNewComment("");
      setShowCommentForm(false);
      alert("댓글이 성공적으로 작성되었습니다.");
    } catch (error) {
      console.error("댓글 작성 실패:", error);
      alert("댓글 작성 중 오류가 발생했습니다.");
    }
  };

  // 대댓글 작성
  const handleReplySubmit = async (e, parentCommentId) => {
    e.preventDefault();
    if (!isLoggedIn) {
      alert("로그인이 필요합니다.");
      return;
    }

    try {
      const response = await fetchWithAuth(
        `${API_URL}posts/${postId}/comments`,
        {
          method: "POST",
          body: JSON.stringify({
            content: replyContent,
            postId: Number(postId),
            userId: Number(user.id),
            userName: user.name,
            parentCommentId: parentCommentId
          })
        }
      );

      if (!response.ok) {
        throw new Error(`Error: ${response.status}`);
      }

      await fetchComments();
      setShowReplyForm(null);
      setReplyContent("");
      alert("댓글이 성공적으로 작성되었습니다.");
    } catch (error) {
      console.error("댓글 작성 실패:", error);
      alert("댓글 작성 중 오류가 발생했습니다.");
    }
  };

  // 댓글 수정
  const handleEditSubmit = async (e, commentId) => {
    e.preventDefault();
    if (!isLoggedIn) {
      alert("로그인이 필요합니다.");
      return;
    }

    try {
      const response = await fetchWithAuth(
        `${API_URL}posts/${postId}/comments/${commentId}`,
        {
          method: "PUT",
          body: JSON.stringify({
            content: editContent,
            postId: Number(postId),
            userId: Number(user.id),
            userName: user.name
          })
        }
      );

      if (!response.ok) {
        throw new Error(`Error: ${response.status}`);
      }

      await fetchComments();
      setIsEditing(null);
      setEditContent("");
      alert("댓글이 성공적으로 수정되었습니다.");
    } catch (error) {
      console.error("댓글 수정 실패:", error);
      alert("댓글 수정 중 오류가 발생했습니다.");
    }
  };

  // 댓글 삭제
  const handleDelete = async (commentId) => {
    if (!isLoggedIn) {
      alert("로그인이 필요합니다.");
      return;
    }

    if (!window.confirm("정말로 이 댓글을 삭제하시겠습니까?")) return;

    try {
      const response = await fetchWithAuth(
        `${API_URL}posts/${postId}/comments/${commentId}`,
        {
          method: "DELETE",
          body: JSON.stringify({
            userId: Number(user.id),
            postId: Number(postId)
          })
        }
      );

      if (!response.ok) {
        throw new Error(`Error: ${response.status}`);
      }

      await fetchComments();
      alert("댓글이 성공적으로 삭제되었습니다.");
    } catch (error) {
      console.error("댓글 삭제 실패:", error);
      alert("댓글 삭제 중 오류가 발생했습니다.");
    }
  };

  // 편집 시작 함수
  const startEditing = (commentId, content) => {
    setIsEditing(commentId);
    setEditContent(content);
  };

  // 답글 폼 토글 함수
  const toggleReplyForm = (commentId) => {
    if (!isLoggedIn) {
      alert("로그인이 필요합니다.");
      return;
    }
    setShowReplyForm(showReplyForm === commentId ? null : commentId);
    setReplyContent("");
  };

  // 댓글 불러오기
  useEffect(() => {
    fetchComments();
  }, [postId]);

  return (
    <div className="comment_section">
      {/* 댓글 작성 영역 */}
      <div className="comment_write_area">
        {!showCommentForm ? (
          <button
            className="write_button"
            onClick={() => setShowCommentForm(true)}
            disabled={!isLoggedIn}
            title={!isLoggedIn ? "로그인이 필요합니다" : ""}>
            댓글작성
          </button>
        ) : (
          <form className="comment_form" onSubmit={handleNewCommentSubmit}>
            <textarea
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
              placeholder={
                isLoggedIn ? "댓글을 작성하세요" : "로그인이 필요합니다"
              }
              rows="3"
              disabled={!isLoggedIn}
              required
            />
            <div className="button_group">
              <button
                type="button"
                onClick={() => {
                  setShowCommentForm(false);
                  setNewComment("");
                }}>
                취소
              </button>
              <button
                type="submit"
                className="submit"
                disabled={!newComment.trim()}>
                등록
              </button>
            </div>
          </form>
        )}
      </div>

      <div className="section_s_t">
        <p className="comment_title">댓글</p>
        <div className="comment_stats">
          <span>댓글 {totalComments}</span>
          <span>조회수 {views}</span>
        </div>
      </div>

      {comments.length > 0 ? (
        comments.map((comment) => (
          <CommentComponent
            key={comment.id}
            comment={comment}
            depth={1}
            user={user}
            isLoggedIn={isLoggedIn}
            setIsHovered={setIsHovered}
            isHovered={isHovered}
            isEditing={isEditing}
            setIsEditing={setIsEditing}
            startEditing={startEditing}
            handleEditSubmit={handleEditSubmit}
            handleDelete={handleDelete}
            toggleReplyForm={toggleReplyForm}
            showReplyForm={showReplyForm}
            handleReplySubmit={handleReplySubmit}
            replyContent={replyContent}
            setReplyContent={setReplyContent}
            editContent={editContent}
            setEditContent={setEditContent}
            postId={postId}
          />
        ))
      ) : (
        <p className="no_comment">댓글이 없습니다.</p>
      )}
    </div>
  );
};

CommentSection.propTypes = {
  postId: PropTypes.string.isRequired,
  user: PropTypes.shape({
    id: PropTypes.number,
    name: PropTypes.string
  }),
  isLoggedIn: PropTypes.bool.isRequired,
  views: PropTypes.number
};

export default CommentSection;
