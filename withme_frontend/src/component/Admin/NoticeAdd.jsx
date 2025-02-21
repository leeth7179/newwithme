import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  TextField,
  FormControlLabel,
  Checkbox,
  Box,
  Typography
} from "@mui/material";
import { PrimaryButton } from "../elements/CustomComponents";
import { API_URL } from "../../constant";
import { useSelector } from "react-redux";
import { fetchWithAuth } from "../../common/fetchWithAuth";

const NoticeAdd = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const isEdit = Boolean(id);

  // Redux 상태 가져오기
  const { user, isLoggedIn } = useSelector((state) => state.auth);

  // formData 상태 정의
  const [formData, setFormData] = useState({
    title: "",
    content: "",
    category: "",
    important: false
  });

  // 수정 모드일 경우 기존 데이터 불러오기
  useEffect(() => {
    const fetchNotice = async () => {
      if (isEdit) {
        try {
          const response = await fetchWithAuth(`${API_URL}notices/${id}`);

          if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
          }

          const data = await response.json();
          console.log("Fetched notice data:", data);

          if (data) {
            setFormData({
              title: data.title || "",
              content: data.content || "",
              category: data.category || "",
              important: data.important || false
            });
          }
        } catch (error) {
          console.error("공지사항을 불러오는 중 오류가 발생했습니다.", error);
          alert("공지사항을 불러오는데 실패했습니다.");
          navigate("/notices");
        }
      }
    };

    fetchNotice();
  }, [id, navigate, isEdit]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    // 더 자세한 권한 및 토큰 확인
    console.log("제출 시 인증 정보:", {
      isLoggedIn,
      userRoles: user?.roles
    });

    if (!isLoggedIn || !isAdmin(user)) {
      alert("공지사항은 관리자만 작성/수정할 수 있습니다.");
      return;
    }

    try {
      const url = isEdit ? `${API_URL}notices/${id}` : `${API_URL}notices`;
      const method = isEdit ? "PUT" : "POST";

      const response = await fetchWithAuth(url, {
        method,
        body: JSON.stringify({
          ...formData,
          important: formData.important === true
        })
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(
          `HTTP error! Status: ${response.status}, Body: ${errorText}`
        );
      }

      alert(`공지사항이 ${isEdit ? "수정" : "등록"}되었습니다.`);
      navigate("/notices");
    } catch (error) {
      console.error("작업 중 오류 발생:", error);
      alert("공지사항 작성/수정 권한이 없습니다. 관리자로 로그인해주세요.");
    }
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value
    }));
  };

  const isAdmin = (user) => {
    if (!user || !user.roles) return false;
    if (typeof user.roles === "string") {
      return user.roles.includes("ROLE_ADMIN");
    }
    return false;
  };

  // 관리자가 아니거나 로그인하지 않은 경우 접근 제한
  if (!isLoggedIn || !isAdmin(user)) {
    return (
      <Box sx={{ padding: "20px", textAlign: "center" }}>
        <Typography variant="h6" color="error">
          접근 권한이 없습니다.
        </Typography>
        <Typography variant="body1">
          관리자로 로그인해야 공지사항을 작성할 수 있습니다.
        </Typography>
      </Box>
    );
  }

  return (
    <Box sx={{ padding: "20px", maxWidth: "600px", margin: "0 auto" }}>
      <Typography variant="h4" gutterBottom>
        {isEdit ? "공지사항 수정" : "공지사항 등록"}
      </Typography>

      <form onSubmit={handleSubmit}>
        <FormControl fullWidth margin="normal">
          <TextField
            label="제목"
            variant="outlined"
            name="title"
            value={formData.title}
            onChange={handleChange}
            required
            placeholder="공지사항 제목을 입력하세요"
          />
        </FormControl>

        <FormControl fullWidth margin="normal">
          <TextField
            label="내용"
            variant="outlined"
            name="content"
            value={formData.content}
            onChange={handleChange}
            multiline
            rows={8}
            required
            placeholder="공지사항 내용을 입력하세요"
          />
        </FormControl>

        <FormControl fullWidth margin="normal">
          <InputLabel id="category-label">카테고리를 선택하세요</InputLabel>
          <Select
            labelId="category-label"
            name="category"
            value={formData.category}
            onChange={handleChange}
            required>
            <MenuItem value="일반">일반</MenuItem>
            <MenuItem value="이벤트">이벤트</MenuItem>
            <MenuItem value="정책/운영">정책/운영</MenuItem>
          </Select>
        </FormControl>

        <FormControl fullWidth margin="normal">
          <FormControlLabel
            control={
              <Checkbox
                name="important"
                checked={formData.important}
                onChange={handleChange}
                color="primary"
              />
            }
            label="중요 공지사항으로 표시"
          />
        </FormControl>

        <PrimaryButton
          type="submit"
          fullWidth
          variant="contained"
          sx={{ mt: 2, py: 1.5 }}>
          {isEdit ? "수정하기" : "등록하기"}
        </PrimaryButton>
      </form>
    </Box>
  );
};

export default NoticeAdd;
