import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { Typography, TextField, Button, Box } from "@mui/material";
import { fetchWithAuth } from "../../common/fetchWithAuth";
import { API_URL } from "../../constant";
import { setUser } from "../../redux/authSlice";
import "../../assets/css/member/mypage.css";

const MyPageProfileEdit = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { user } = useSelector((state) => state.auth);

  const [formData, setFormData] = useState({
    name: "",
    email: "",
    phone: "",
    address: ""
  });

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const response = await fetchWithAuth(`${API_URL}members/${user.id}`, {
          method: "GET"
        });

        if (response.ok) {
          const result = await response.json();
          setFormData(result.data);
        }
      } catch (error) {
        console.error("사용자 정보 로드 중 오류:", error);
      }
    };

    fetchUserData();
  }, [user]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetchWithAuth(`${API_URL}members/${user.id}`, {
        method: "PUT",
        body: JSON.stringify(formData)
      });

      if (response.ok) {
        const result = await response.json();

        // Redux 상태 업데이트
        dispatch(
          setUser({
            id: result.id,
            email: result.email,
            name: result.name
          })
        );

        alert("프로필이 성공적으로 업데이트되었습니다.");
        navigate(`/mypage/${user.id}`);
      } else {
        const errorText = await response.text();
        throw new Error(errorText || "프로필 업데이트 실패");
      }
    } catch (error) {
      console.error("프로필 업데이트 오류:", error);
      alert(error.message);
    }
  };

  return (
    <div className="my_edit_container">
      <h2 className="page_title">내 정보 수정</h2>

      <div className="my_wrap">
        <div>
          <Box
            component="form"
            onSubmit={handleSubmit}
            sx={{
              display: "flex",
              flexDirection: "column",
              gap: 2
            }}>
            <TextField
              fullWidth
              label="이름"
              name="name"
              value={formData.name}
              onChange={handleInputChange}
              variant="outlined"
              required
            />
            <TextField
              fullWidth
              label="이메일"
              name="email"
              value={formData.email}
              onChange={handleInputChange}
              variant="outlined"
              disabled
            />
            <TextField
              fullWidth
              label="연락처"
              name="phone"
              value={formData.phone}
              onChange={handleInputChange}
              variant="outlined"
            />
            <TextField
              fullWidth
              label="주소"
              name="address"
              value={formData.address}
              onChange={handleInputChange}
              variant="outlined"
            />
            <Box
              sx={{
                display: "flex",
                justifyContent: "flex-end",
                gap: 2,
                mt: 2
              }}>
              <Button
                variant="outlined"
                onClick={() => navigate(`/mypage/${user.id}`)}
                color="secondary">
                취소
              </Button>
              <Button type="submit" variant="contained" color="primary">
                저장
              </Button>
            </Box>
          </Box>
        </div>
      </div>
    </div>
  );
};

export default MyPageProfileEdit;
