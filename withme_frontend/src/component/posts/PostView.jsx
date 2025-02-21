import React, { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { useParams, useNavigate } from "react-router-dom";
import { API_URL } from "../../constant";
import { fetchWithAuth } from "../../common/fetchWithAuth";
import CommentSection from "./CommentSection";
import { Button } from "@mui/material";
import { PrimaryButton } from "../elements/CustomComponents";
import "../../assets/css/posts/posts.css";

const PostView = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isLoggedIn, user } = useSelector((state) => state.auth);
  const [post, setPost] = useState(null);

  // 게시글 불러오기
  const fetchPost = async () => {
    try {
      const response = await fetch(`${API_URL}posts/${id}`);
      if (!response.ok)
        throw new Error(`HTTP 오류! 상태 코드: ${response.status}`);
      const data = await response.json();
      setPost(data);
    } catch (error) {
      console.error("게시글 가져오기 실패:", error.message);
    }
  };

  const handleDeletePost = async () => {
    if (!isLoggedIn) {
      alert("해당 권한이 없습니다.");
      return;
    }

    if (!window.confirm("정말로 이 게시글을 삭제하시겠습니까?")) return;

    try {
      const response = await fetchWithAuth(`${API_URL}posts/${id}`, {
        method: "DELETE"
      });

      if (!response.ok) throw new Error(`삭제 실패: ${response.status}`);

      alert("게시글이 성공적으로 삭제되었습니다.");
      navigate("/posts");
    } catch (error) {
      console.error("게시글 삭제 실패:", error);
      if (error.message.includes("403")) {
        alert("삭제 권한이 없습니다.");
      } else if (error.message.includes("404")) {
        alert("존재하지 않는 게시글입니다.");
      } else {
        alert("게시글 삭제 중 오류가 발생했습니다.");
      }
    }
  };

  useEffect(() => {
    fetchPost();
  }, [id]);

  if (!post) return <p>게시글 로딩 중...</p>;

  return (
    <div className="view_warp">
      <h4>커뮤니티</h4>

      <div className="view_inner">
        <div className="view_top">
          <div className="view_title">
            <p>{post.title || "제목 없음"}</p>
          </div>

          <div className="view_area">
            {/* <div className="author_info">
              <p>작성자 {post.name}</p>
            </div> */}
            {post.updateTime && post.updateTime !== post.regTime ? (
              <div>
                수정일:{" "}
                {new Date(post.updateTime).toLocaleString("ko-KR", {
                  year: "numeric",
                  month: "2-digit",
                  day: "2-digit",
                  hour: "2-digit",
                  minute: "2-digit"
                })}
              </div>
            ) : (
              <div>
                작성일:{" "}
                {post.regTime &&
                  new Date(post.regTime).toLocaleString("ko-KR", {
                    year: "numeric",
                    month: "2-digit",
                    day: "2-digit",
                    hour: "2-digit",
                    minute: "2-digit"
                  })}
              </div>
            )}
          </div>
        </div>
      </div>

      <div className="view_content">
        <div className="view_inner">
          <p
            className="content"
            dangerouslySetInnerHTML={{
              __html: post.content || "내용 없음"
            }}></p>
        </div>
      </div>

      {/* 댓글 섹션 */}
      <CommentSection
        postId={id}
        user={user}
        isLoggedIn={isLoggedIn}
        views={post.views} // views prop 전달
      />

      <div className="view_btn_container">
        <PrimaryButton className="button" onClick={() => navigate("/posts")}>
          목록보기
        </PrimaryButton>
        <div>
          {user?.id === post.userId && (
            <div className="view_btn">
              <button className="button delete_btn" onClick={handleDeletePost}>
                삭제
              </button>
              <button
                className="button"
                onClick={() => navigate(`/posts/${id}/edit`)}>
                수정
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default PostView;
