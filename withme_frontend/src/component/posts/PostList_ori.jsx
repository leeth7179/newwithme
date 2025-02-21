import React, { useEffect, useState } from "react";
import { API_URL } from "../../constant";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { Tabs, Tab, Button, Pagination } from "@mui/material";
import TabPanel from "../elements/TabPanel";

const categories = [
  "전체",
  "펫푸드",
  "질문/꿀팁",
  "펫일상",
  "펫수다",
  "행사/정보"
]; // 카테고리 목록

const PostList = () => {
  const [posts, setPosts] = useState([]); // 전체 게시글
  const [filteredPosts, setFilteredPosts] = useState([]); // 필터링된 게시글
  const [activeCategoryIndex, setActiveCategoryIndex] = useState(0); // 현재 활성화된 카테고리 인덱스
  const [currentUserId, setCurrentUserId] = useState(null); // 현재 로그인한 사용자 ID
  const [totalRows, setTotalRows] = useState(0); // 전체 게시글 수
  const [paginationModel, setPaginationModel] = useState({
    page: 1, // 현재 페이지 (1부터 시작)
    pageSize: 10 // 한 페이지에 표시할 게시글 수
  }); // 페이지네이션 상태
  const navigate = useNavigate();

  /**
   * 컴포넌트가 마운트될 때 한 번 실행되며,
   * paginationModel 또는 posts가 변경될 때도 실행됩니다.
   */
  useEffect(() => {
    fetchCurrentUser();
    fetchPosts();
  }, [paginationModel]);

  // 현재 로그인한 사용자 정보 가져오기 (예: /api/auth/me 엔드포인트)
  const fetchCurrentUser = async () => {
    try {
      const response = await axios.get(`${API_URL}/auth/me`);
      if (response.status === 200) {
        setCurrentUserId(response.data.userId);
      }
    } catch (error) {
      console.error("사용자 정보 가져오기 실패:", error.message);
      alert("로그인 정보가 필요합니다.");
      navigate("/login");
    }
  };

  // 게시글 데이터 가져오기
  const fetchPosts = async () => {
    const { page, pageSize } = paginationModel; // 현재 페이지와 페이지 크기

    try {
      const response = await axios.get(
        `${API_URL}/posts?page=${page - 1}&size=${pageSize}` // 서버에서 요청 시 페이지는 보통 0부터 시작
      );
      if (response.status === 200) {
        const data = response.data; // 서버에서 받아온 데이터
        setPosts(data.dtoList || []); // 전체 게시글 저장
        setFilteredPosts(data.dtoList || []); // 기본적으로 전체 게시글 표시
        setTotalRows(data.total || 0); // 전체 게시글 수 저장
      } else {
        console.error("게시글 목록 불러오기 실패:", response.status);
        alert(`게시글 목록 불러오기 실패: ${response.statusText}`);
      }
    } catch (error) {
      console.error("게시글 목록 가져오는 중 오류 발생:", error.message);
      alert("게시글 목록 가져오기 실패: 네트워크 또는 서버 오류");
    }
  };

  // 카테고리 변경 시 필터링 처리
  const handleCategoryChange = (event, newValue) => {
    setActiveCategoryIndex(newValue);
    const selectedCategory = categories[newValue];
    if (selectedCategory === "전체") {
      setFilteredPosts(posts); // 전체 게시글 표시
    } else {
      setFilteredPosts(
        posts.filter((post) => post.category === selectedCategory)
      ); // 선택된 카테고리의 게시글만 표시
    }
  };

  // 페이지네이션 변경 처리
  const handlePageChange = (event, newPage) => {
    setPaginationModel((prev) => ({ ...prev, page: newPage }));
  };

  return (
    <div style={{ padding: "20px" }}>
      <h1>커뮤니티</h1>

      {/* MUI Tabs */}
      <Tabs
        value={activeCategoryIndex}
        onChange={handleCategoryChange}
        variant="scrollable"
        scrollButtons="auto"
        aria-label="posts-category-tabs">
        {categories.map((category, index) => (
          <Tab key={index} label={category} />
        ))}
      </Tabs>

      {/* TabPanels */}
      {categories.map((category, index) => (
        <TabPanel key={index} value={activeCategoryIndex} index={index}>
          <ul>
            {filteredPosts.map((post) => (
              <li key={post.postId} style={{ marginBottom: "20px" }}>
                <h2>{post.postTitle}</h2>
                <p>{post.postContent}</p>
                <p>
                  <strong>카테고리:</strong> {post.category}
                </p>
                {/* 작성자만 수정/삭제 버튼 표시 */}
                {currentUserId === post.userId && (
                  <>
                    <Button
                      onClick={() => navigate(`/posts/edit/${post.postId}`)}
                      style={{
                        padding: "8px",
                        marginRight: "10px",
                        backgroundColor: "#28a745",
                        color: "#fff",
                        borderRadius: "5px"
                      }}>
                      수정
                    </Button>
                    <Button
                      onClick={() => deletePost(post.postId)}
                      style={{
                        padding: "8px",
                        backgroundColor: "#dc3545",
                        color: "#fff",
                        borderRadius: "5px"
                      }}>
                      삭제
                    </Button>
                  </>
                )}
              </li>
            ))}
          </ul>
          {filteredPosts.length === 0 && (
            <p>해당 카테고리에 게시물이 없습니다.</p>
          )}
        </TabPanel>
      ))}

      {/* 페이지네이션 */}
      <Pagination
        // count={Math.ceil(totalRows / paginationModel.pageSize)} // 총 페이지 수 계산
        // page={paginationModel.page}
        count={10}
        onChange={handlePageChange}
        color="primary"
        size="small"
      />
    </div>
  );
};

export default PostList;
