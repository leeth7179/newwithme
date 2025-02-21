import React, { useState, useEffect } from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import {
  Button,
  Typography,
  Box,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions
} from "@mui/material";
import { fetchWithAuth } from "../../common/fetchWithAuth";
import { API_URL } from "../../constant";
import { getImageUrl } from "../../utils/imageUtils";
import "../../assets/css/member/mypage.css";

const MyPage = () => {
  const navigate = useNavigate();
  const { user, isLoggedIn } = useSelector((state) => state.auth);
  const [pets, setPets] = useState([]);
  const [userPosts, setUserPosts] = useState([]);
  const [userComments, setUserComments] = useState([]);
  const [userInfo, setUserInfo] = useState({
    name: "",
    email: "",
    phone: "",
    address: ""
  });
  const [openLoginDialog, setOpenLoginDialog] = useState(false);

  useEffect(() => {
    // 로그인하지 않았거나 관리자 계정일 경우 마이페이지 접근 제한
    if (!isLoggedIn) {
      setOpenLoginDialog(true);
      return;
    }

    if (user && !user.roles.includes("ROLE_ADMIN")) {
      fetchUserData(user.id);
      fetchPetData(user.id);
      fetchUserCommunityData(user.id);
    }
  }, [user, isLoggedIn]);

  const fetchUserData = async (userId) => {
    try {
      const response = await fetchWithAuth(`${API_URL}members/${userId}`);
      if (response.ok) {
        const result = await response.json();
        setUserInfo(result.data);
      }
    } catch (error) {
      console.error("사용자 정보 로드 중 오류:", error);
    }
  };

  const fetchPetData = async (userId) => {
    try {
      const response = await fetchWithAuth(`${API_URL}pets/user/${userId}`);
      if (!response.ok) {
        const errorText = await response.text();
        console.error("펫 정보 로드 실패:", errorText);
        return;
      }
      const result = await response.json();
      setPets(result.content || []);
    } catch (error) {
      console.error("펫 정보 로드 중 오류:", error);
    }
  };

  const fetchUserCommunityData = async (userId) => {
    try {
      const [postsResponse, commentsResponse] = await Promise.all([
        fetchWithAuth(`${API_URL}posts/user/${userId}?page=0&size=3`),
        fetchWithAuth(`${API_URL}members/${userId}/comments?page=0&size=3`)
      ]);

      if (!postsResponse.ok || !commentsResponse.ok) {
        console.error("커뮤니티 데이터 로드 실패");
        return;
      }

      const postsResult = await postsResponse.json();
      const commentsResult = await commentsResponse.json();

      setUserPosts(postsResult.content || []);
      setUserComments(commentsResult.content || []);
    } catch (error) {
      console.error("커뮤니티 데이터 로드 중 오류:", error);
    }
  };

  const renderPetItem = (pet) => (
    <Box
      key={pet.petId}
      onClick={() => navigate(`/mypage/pet/${pet.petId}`)}
      className="pet_item">
      <Box sx={{ display: "flex", gap: 2, alignItems: "center" }}>
        <Box className="pet_image_container">
          <img
            src={getImageUrl(pet.imageUrl)}
            alt={pet.name}
            onError={(e) => {
              e.target.src = "/assets/images/default-pet-image.png";
            }}
            className="pet_image"
          />
        </Box>

        <Box sx={{ flex: 1 }}>
          <Typography variant="subtitle1" className="pet_name">
            {pet.name}
          </Typography>
          <Typography variant="body2" color="textSecondary">
            {pet.breed || "품종 미상"}
          </Typography>
          <Typography variant="body2" color="textSecondary">
            {pet.gender === "M" ? "수컷" : "암컷"}, {pet.age}세
          </Typography>
        </Box>
      </Box>
    </Box>
  );

  // 로그인 필요 다이얼로그 닫기
  const handleCloseLoginDialog = () => {
    setOpenLoginDialog(false);
    navigate("/login");
  };

  // 관리자 계정이거나 로그인하지 않은 경우 렌더링 방지
  if (!isLoggedIn || (user && user.roles.includes("ROLE_ADMIN"))) {
    return (
      <Dialog open={openLoginDialog} onClose={handleCloseLoginDialog}>
        <DialogTitle>로그인 필요</DialogTitle>
        <DialogContent>
          <Typography>로그인 후 이용 가능합니다.</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseLoginDialog} color="primary">
            로그인 하러 가기
          </Button>
        </DialogActions>
      </Dialog>
    );
  }

  return (
    <div className="page_container">
      <h2 className="page_title">마이 페이지</h2>

      <div className="my_wrap">
        <div className="grid_box">
          <div className="left_section">
            <div className="section community_section">
              <Box className="section_header">
                <Typography variant="h5" component="h2">
                  커뮤니티 활동
                </Typography>
              </Box>

              <div className="post_card">
                <div className="post_section">
                  <div className="subtitle" onClick={() => navigate("/posts")}>
                    <p>작성 게시글</p>
                    <span>{userPosts.length}개</span>
                  </div>
                  {userPosts.length === 0 ? (
                    <div className="empty_message">
                      <Typography variant="body2" color="textSecondary">
                        작성한 게시글이 없습니다.
                      </Typography>
                    </div>
                  ) : (
                    <ul className="item_list">
                      {userPosts.map((post) => (
                        <li key={post.id}>{post.title}</li>
                      ))}
                    </ul>
                  )}
                </div>

                <div className="comment_section">
                  <div className="subtitle" onClick={() => navigate("/posts")}>
                    <p>작성 댓글</p>
                    <span>{userComments.length}개</span>
                  </div>
                  {userComments.length === 0 ? (
                    <div className="empty_message">
                      <Typography variant="body2" color="textSecondary">
                        작성한 댓글이 없습니다.
                      </Typography>
                    </div>
                  ) : (
                    <ul className="item_list">
                      {userComments.map((comment) => (
                        <li key={comment.id}>{comment.content}</li>
                      ))}
                    </ul>
                  )}
                </div>
              </div>
            </div>

            <div className="section pet_section">
              <Box className="section_header">
                <Typography variant="h5" component="h2">
                  펫 정보
                </Typography>
              </Box>

              <div className="pet_list">
                {pets.length === 0 ? (
                  <Typography
                    variant="body1"
                    color="textSecondary"
                    align="center">
                    등록된 반려동물이 없습니다.
                  </Typography>
                ) : (
                  pets.map(renderPetItem)
                )}
              </div>

              <Button
                variant="contained"
                fullWidth
                className="register_button"
                onClick={() => navigate("/mypage/pet/register")}>
                펫 등록하기
              </Button>
            </div>
          </div>

          <div className="info_section">
            <Box className="section_header">
              <Typography variant="h5" component="h2">
                내 정보
              </Typography>
              <Box className="edit_buttons">
                <span
                  onClick={() => navigate("/mypage/profile-edit")}
                  className="edit_info">
                  내정보 수정
                </span>
                <span
                  onClick={() => navigate("/mypage/password-edit")}
                  className="edit_password">
                  비밀번호 수정
                </span>
              </Box>
            </Box>

            <Box className="info_container">
              <Box className="info_user">
                <Typography variant="h6">
                  {userInfo.name || user.name}
                </Typography>
                <Typography variant="body1" color="textSecondary">
                  {userInfo.email || user.email}
                </Typography>
              </Box>
              <Typography variant="subtitle1" className="points">
                보유포인트 10,000원
              </Typography>
            </Box>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MyPage;
