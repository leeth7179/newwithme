import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Typography, Button, Box } from "@mui/material";
import { Star as StarIcon } from "@mui/icons-material";
import { useSelector } from "react-redux";
import { API_URL } from "../../constant";
import { fetchWithAuth } from "../../utils/fetchWithAuth";
import { PrimaryButton, DeleteButton } from "../elements/CustomComponents";

const NoticeView = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [notice, setNotice] = useState(null);
  const { user, isLoggedIn } = useSelector((state) => state.auth);

  // 날짜 포맷팅 함수
  const formatDate = (dateString) => {
    if (!dateString) return "";
    const date = new Date(dateString);
    return new Intl.DateTimeFormat("ko-KR", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit"
    }).format(date);
  };

  useEffect(() => {
    const fetchNotice = async () => {
      try {
        const response = await fetch(`${API_URL}notices/${id}`, {
          method: "GET",
          headers: {
            "Content-Type": "application/x-www-form-urlencoded"
          },
          credentials: "include"
        });

        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const data = await response.json();
        setNotice(data);
      } catch (error) {
        console.error("공지사항을 불러오는 중 오류가 발생했습니다.", error);
        alert("공지사항을 불러오는데 실패했습니다.");
        navigate("/notices");
      }
    };

    fetchNotice();
  }, [id, navigate]);

  const deleteNotice = async () => {
    if (window.confirm("정말로 삭제하시겠습니까?")) {
      try {
        const response = await fetchWithAuth(`${API_URL}notices/${id}`, {
          method: "DELETE"
        });

        if (!response.ok) {
          const errorText = await response.text();
          throw new Error(
            `HTTP error! Status: ${response.status}, Body: ${errorText}`
          );
        }

        alert("공지사항이 삭제되었습니다.");
        navigate("/notices");
      } catch (error) {
        console.error("공지사항 삭제 중 오류가 발생했습니다:", error);
        alert(error.message || "공지사항 삭제에 실패했습니다.");
      }
    }
  };

  if (!notice) return <p>공지사항을 불러오는 중입니다...</p>;

  return (
    <Box sx={{ padding: "20px" }}>
      <Typography variant="h4" gutterBottom>
        {notice.important && (
          <StarIcon color="primary" sx={{ mr: 1, verticalAlign: "middle" }} />
        )}
        {notice.title}
      </Typography>
      <Typography variant="body1" sx={{ mt: 2, mb: 2 }}>
        {notice.content}
      </Typography>
      <Box sx={{ mt: 2, color: "text.secondary" }}>
        <Typography variant="subtitle1">카테고리: {notice.category}</Typography>
        <Typography variant="subtitle2">
          {notice.updatedAt !== notice.createdAt
            ? `수정일: ${formatDate(notice.updatedAt)}`
            : `작성일: ${formatDate(notice.createdAt)}`}
        </Typography>
      </Box>

      {isLoggedIn && user?.role === "ADMIN" && (
        <Box sx={{ mt: 3 }}>
          <PrimaryButton
            onClick={() => navigate(`/notices/edit/${id}`)}
            sx={{ mr: 1 }}>
            수정
          </PrimaryButton>
          <DeleteButton onClick={deleteNotice}>삭제</DeleteButton>
        </Box>
      )}

      <Button
        variant="outlined"
        onClick={() => navigate("/notices")}
        sx={{ mt: 3 }}>
        목록으로
      </Button>
    </Box>
  );
};

export default NoticeView;
