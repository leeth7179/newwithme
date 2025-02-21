import React, { useState, useEffect } from "react";
import { Star as StarIcon } from "@mui/icons-material";
import { API_URL } from "../../constant";
import { fetchWithAuth } from "../../common/fetchWithAuth";
import { useNavigate } from "react-router-dom";
import "../../assets/css/notice/main_notice.css";

const MainNotice = () => {
  const [notices, setNotices] = useState([]);
  const [currentNoticeIndex, setCurrentNoticeIndex] = useState(0);
  const [isAnimating, setIsAnimating] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchNotices = async () => {
      try {
        const response = await fetchWithAuth(`${API_URL}notices?page=0&size=5`);

        if (!response.ok) {
          throw new Error("공지사항을 불러오는 데 실패했습니다.");
        }

        const data = await response.json();

        // 중요 공지사항을 먼저 정렬
        const sortedNotices = [
          ...data.content.filter((notice) => notice.important),
          ...data.content.filter((notice) => !notice.important)
        ];

        setNotices(sortedNotices);
      } catch (error) {
        console.error("공지사항 로딩 중 에러:", error);
      }
    };

    fetchNotices();
  }, []);

  useEffect(() => {
    if (notices.length <= 1) return; // 1개 이하면 롤링 필요 없음

    const interval = setInterval(() => {
      setIsAnimating(true);

      setTimeout(() => {
        setCurrentNoticeIndex((prevIndex) => (prevIndex + 1) % notices.length);
        setIsAnimating(false);
      }, 500); // 애니메이션 지속 시간
    }, 3000); // 3초마다 변경

    return () => clearInterval(interval);
  }, [notices]);

  const handleNoticeClick = () => {
    navigate("/notices");
  };

  if (notices.length === 0) return null;

  const currentNotice = notices[currentNoticeIndex] || {};

  return (
    <div className="notice_warp" onClick={handleNoticeClick}>
      <div className={`notice ${isAnimating ? "notice_animate" : ""}`}>
        <span className="notice_title">공지사항</span>
        📢
        <span className="line">|</span>
        {currentNotice.important && (
          <span className="important_text">중요</span>
        )}
        <div>{currentNotice.title || "로딩 중..."}</div>
      </div>
    </div>
  );
};

export default MainNotice;
