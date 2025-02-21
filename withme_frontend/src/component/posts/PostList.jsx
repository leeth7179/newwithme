import React, { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { API_URL } from "../../constant";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { Tabs, Tab, Button, Pagination, PaginationItem } from "@mui/material";
import { PrimaryButton } from "../elements/CustomComponents";
import { styled } from "@mui/material/styles";
import TabPanel from "../elements/TabPanel";
import "../../assets/css/posts/posts.css";

const categories = [
  "전체",
  "펫푸드",
  "질문/꿀팁",
  "펫일상",
  "펫수다",
  "행사/정보"
];

const PostList = () => {
  const { isLoggedIn } = useSelector((state) => state.auth); // Redux에서 로그인 여부 가져오기
  const [posts, setPosts] = useState([]);
  const [filteredPosts, setFilteredPosts] = useState([]);
  const [activeCategoryIndex, setActiveCategoryIndex] = useState(0);
  const [totalRows, setTotalRows] = useState(0);
  const [paginationModel, setPaginationModel] = useState({
    page: 1,
    pageSize: 10 //페이지당 게시물 수
  });

  const navigate = useNavigate();

  // 게시글 목록 가져오기
  const fetchPosts = async () => {
    try {
      const response = await fetch(
        `${API_URL}posts?page=${paginationModel.page - 1}&size=${
          paginationModel.pageSize
        }`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json"
          }
        }
      );

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(
          `HTTP error! Status: ${response.status}, Body: ${errorText}`
        );
      }

      const data = await response.json();

      // API 응답 데이터 확인 (디버깅용)
      console.log("API 응답 데이터:", data);
      console.log("posts:", data.posts);

      // dtoList 대신 posts 사용
      setPosts(data.posts || []);
      setFilteredPosts(data.posts || []);
      setTotalRows(data.total || 0);
    } catch (error) {
      console.error("게시글 목록 가져오기 실패:", error.message);
      alert("게시글 목록을 불러오는 중 오류가 발생했습니다.");
    }
  };

  useEffect(() => {
    fetchPosts();
  }, [paginationModel]);

  // 카테고리 변경 처리
  const handleCategoryChange = (event, newValue) => {
    setActiveCategoryIndex(newValue);
    const selectedCategory = categories[newValue];
    if (selectedCategory === "전체") {
      setFilteredPosts(posts);
    } else {
      setFilteredPosts(
        posts.filter((post) => post.postCategory === selectedCategory)
      );
    }
  };

  // 페이지네이션 처리
  const handlePageChange = (event, newPage) => {
    setPaginationModel((prev) => ({ ...prev, page: newPage }));
  };

  // 글 작성 버튼 클릭 처리
  const handleWritePostClick = () => {
    if (!isLoggedIn) {
      alert("로그인이 필요합니다.");
      //navigate("/login"); // 로그인 페이지로 이동
    } else {
      window.scrollTo(0, 0);
      navigate("/posts/new"); // 글 작성 페이지로 이동
    }
  };

  // 게시글 클릭 핸들러
  const handlePostClick = (postId) => {
    window.scrollTo(0, 0);
    navigate(`/posts/${postId}`); // Navigate to the post view page with the post ID
  };

  // AntTab 스타일 컴포넌트
  const AntTab = styled((props) => <Tab disableRipple {...props} />)(
    ({ theme }) => ({
      textTransform: "none",
      minWidth: 0,
      fontWeight: theme.typography.fontWeightRegular,
      marginRight: theme.spacing(1),
      color: theme.palette.text.secondary,
      "&:hover": {
        color: theme.palette.primary.main,
        opacity: 1
      },
      "&.Mui-selected": {
        color: theme.palette.primary.main,
        fontWeight: theme.typography.fontWeightMedium
      }
    })
  );

  // AntTabs 스타일 컴포넌트
  const AntTabs = styled(Tabs)(({ theme }) => ({
    borderBottom: `1px solid ${theme.palette.divider}`,
    "& .MuiTabs-indicator": {
      backgroundColor: theme.palette.primary.main
    }
  }));

  return (
    <div className="post_warp">
      <h4>커뮤니티</h4>

      <AntTabs
        value={activeCategoryIndex}
        variant="fullWidth"
        onChange={handleCategoryChange}>
        {categories.map((postCategory) => (
          <AntTab key={postCategory} label={postCategory} />
        ))}
      </AntTabs>

      {/* TabPanels */}
      {categories.map((category) => (
        <TabPanel
          key={category}
          value={activeCategoryIndex}
          index={categories.indexOf(category)}>
          <ul className="post_list_box">
            {filteredPosts.map((post) => (
              <li
                className="post_list_item"
                key={post.id}
                onClick={() => handlePostClick(post.id)}>
                <div className="list_area">
                  {post.thumbnailUrl && (
                    <div className="list_thumbnail_box">
                      <img
                        src={`${API_URL.replace(/\/api\/$/, "")}${
                          post.thumbnailUrl
                        }`}
                        alt="게시물 썸네일"
                        className="thumbnail_img"
                      />
                    </div>
                  )}

                  <span className="list_title_box">
                    <p
                      dangerouslySetInnerHTML={{
                        __html: post.title || "내용 없음"
                      }}></p>
                  </span>
                </div>

                <div className="list_date">
                  <span>{post.views}</span>
                  <div>
                    {post.updateTime && post.updateTime !== post.regTime ? (
                      <div>
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
              </li>
            ))}
          </ul>
          {filteredPosts.length === 0 && (
            <p>해당 카테고리에 게시물이 없습니다.</p>
          )}
        </TabPanel>
      ))}

      <div className="post_btn">
        {/* 글 작성 버튼 */}
        <PrimaryButton
          onClick={handleWritePostClick}
          variant="contained"
          size="small"
          sx={{
            mb: 1,
            minWidth: "auto",
            height: "40px"
          }}>
          글 작성하기
        </PrimaryButton>
      </div>

      {/* 페이지네이션 - Router Integration */}
      <div className="pagination-container">
        <Pagination
          page={paginationModel.page}
          count={Math.ceil(totalRows / paginationModel.pageSize)}
          onChange={handlePageChange}
          siblingCount={1}
          boundaryCount={1}
          renderItem={(item) => (
            <PaginationItem
              component={Link}
              to={`/posts?page=${item.page}&category=${categories[activeCategoryIndex]}`}
              {...item}
            />
          )}
        />
      </div>
    </div>
  );
};

export default PostList;
