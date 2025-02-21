import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import { Typography, TextField, Button, Box } from "@mui/material";
import { fetchWithAuth } from "../../common/fetchWithAuth";
import { API_URL } from "../../constant";
import "../../assets/css/member/mypage.css";

const MyPagePasswordEdit = () => {
  const navigate = useNavigate();
  const { user } = useSelector((state) => state.auth);

  const [passwords, setPasswords] = useState({
    currentPassword: "",
    newPassword: "",
    confirmNewPassword: ""
  });

  const [errors, setErrors] = useState({});

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setPasswords((prev) => ({
      ...prev,
      [name]: value
    }));
    // 입력 시 에러 초기화
    setErrors((prev) => ({
      ...prev,
      [name]: ""
    }));
  };

  const validateForm = () => {
    const newErrors = {};

    if (!passwords.currentPassword) {
      newErrors.currentPassword = "현재 비밀번호를 입력해주세요.";
    }

    if (!passwords.newPassword) {
      newErrors.newPassword = "새 비밀번호를 입력해주세요.";
    } else if (passwords.newPassword.length < 8) {
      newErrors.newPassword = "비밀번호는 최소 8자 이상이어야 합니다.";
    }

    if (passwords.newPassword !== passwords.confirmNewPassword) {
      newErrors.confirmNewPassword = "새 비밀번호가 일치하지 않습니다.";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    try {
      const response = await fetchWithAuth(
        `${API_URL}members/${user.id}/password`,
        {
          method: "PUT",
          body: JSON.stringify({
            currentPassword: passwords.currentPassword,
            newPassword: passwords.newPassword
          })
        }
      );

      if (response.ok) {
        alert("비밀번호가 성공적으로 변경되었습니다.");
        navigate(`/mypage/${user.id}`);
      } else {
        const errorText = await response.text();
        throw new Error(errorText || "비밀번호 변경 실패");
      }
    } catch (error) {
      console.error("비밀번호 변경 오류:", error);
      alert(error.message);
    }
  };

  return (
    <div className="my_edit_container">
      <h2 className="page_title">비밀번호 변경</h2>

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
              type="password"
              label="현재 비밀번호"
              name="currentPassword"
              value={passwords.currentPassword}
              onChange={handleInputChange}
              variant="outlined"
              required
              error={!!errors.currentPassword}
              helperText={errors.currentPassword}
            />
            <TextField
              fullWidth
              type="password"
              label="새 비밀번호"
              name="newPassword"
              value={passwords.newPassword}
              onChange={handleInputChange}
              variant="outlined"
              required
              error={!!errors.newPassword}
              helperText={errors.newPassword}
            />
            <TextField
              fullWidth
              type="password"
              label="새 비밀번호 확인"
              name="confirmNewPassword"
              value={passwords.confirmNewPassword}
              onChange={handleInputChange}
              variant="outlined"
              required
              error={!!errors.confirmNewPassword}
              helperText={errors.confirmNewPassword}
            />
          </Box>
        </div>

        <div>
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
              비밀번호 변경
            </Button>
          </Box>
        </div>
      </div>
    </div>
  );
};

export default MyPagePasswordEdit;
