import React, { useEffect, useState } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import {
  Typography,
  Tabs,
  Tab,
  Pagination,
  PaginationItem,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  Box
} from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { styled } from "@mui/material/styles";
import { useSelector } from "react-redux";
import { PrimaryButton, DeleteButton } from "../elements/CustomComponents";
import { fetchWithAuth } from "../../common/fetchWithAuth";
import { API_URL } from "../../constant";
import '../../assets/css/Admin/NoticeList.css';


const NoticeListAdmin = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [notices, setNotices] = useState([]);
  const [filteredNotices, setFilteredNotices] = useState([]);
  const [activeTab, setActiveTab] = useState(0);
  const [totalRows, setTotalRows] = useState(0);
  const [expandedNotice, setExpandedNotice] = useState(null);

  // URL에서 현재 페이지 파라미터 가져오기
  const query = new URLSearchParams(location.search);
  const page = parseInt(query.get("page") || "1", 10);
  const pageSize = 10; //페이지당 게시물 수

  const { user, isLoggedIn } = useSelector((state) => state.auth);
  const categories = ["전체", "일반", "이벤트", "정책/운영"];

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

  const fetchNotices = async () => {
    try {
      const response = await fetchWithAuth(
        `${API_URL}notices?page=${page - 1}&size=${pageSize}`
      );

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(
          `HTTP error! Status: ${response.status}, Body: ${errorText}`
        );
      }

      const data = await response.json();

      // 중요 공지사항을 먼저 정렬
      const sortedNotices = [
        ...data.content.filter((notice) => notice.important),
        ...data.content.filter((notice) => !notice.important)
      ];

      setNotices(sortedNotices);
      setFilteredNotices(sortedNotices);
      setTotalRows(data.totalElements);
    } catch (error) {
      console.error(
        "공지사항을 불러오는 중 오류가 발생했습니다:",
        error.message
      );
      alert("공지사항 목록을 가져오는 데 실패했습니다.");
    }
  };

  // 아코디언 변경 핸들러 추가
  const handleAccordionChange = (noticeId) => {
    setExpandedNotice(expandedNotice === noticeId ? null : noticeId);
  };

  // 공지사항 삭제 핸들러 추가
  const handleDeleteNotice = async (noticeId) => {
    if (!window.confirm("정말로 이 공지사항을 삭제하시겠습니까?")) return;

    try {
      const response = await fetchWithAuth(`${API_URL}notices/${noticeId}`, {
        method: "DELETE"
      });

      if (!response.ok) {
        throw new Error("삭제 실패");
      }

      alert("공지사항이 삭제되었습니다.");
      fetchNotices();
    } catch (error) {
      console.error("공지사항 삭제 중 오류:", error);
      alert("공지사항 삭제에 실패했습니다.");
    }
  };

  // 공지사항 수정 핸들러 추가
  const handleEditNotice = (noticeId) => {
    //console.log(`Navigating to edit notice: ${noticeId}`);
    navigate(`/notices/${noticeId}/edit`);
  };

  useEffect(() => {
    fetchNotices();
  }, [page]); // page 변경 시 데이터 다시 불러오기

  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue);
    const selectedCategory = categories[newValue];

    if (selectedCategory === "전체") {
      setFilteredNotices(notices);
    } else {
      setFilteredNotices(
        notices.filter((notice) => notice.category === selectedCategory)
      );
    }
    // 탭 변경 시 1페이지로 이동
    navigate(`/notices?page=1&category=${selectedCategory}`);
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

  const isAdmin = (user) => {
    if (!user || !user.roles) return false;

    // roles가 문자열 '[ROLE_ADMIN]' 형태인 경우
    const rolesValue = user.roles.replace(/^\[|\]$/g, "");
    return rolesValue === "ROLE_ADMIN";
  };

  return (
    <div className="notice-container">
      <h4>공지사항 목록</h4>
      <div>
        <AntTabs
          value={activeTab}
          variant="fullWidth"
          onChange={handleTabChange}>
          {categories.map((category, index) => (
            <AntTab key={index} label={category} />
          ))}
        </AntTabs>
      </div>
      <div className="notice_list_container">
        {filteredNotices.map((notice) => (
          <Accordion
            key={notice.id}
            expanded={expandedNotice === notice.id}
            onChange={() => handleAccordionChange(notice.id)}>
            <AccordionSummary
              expandIcon={<ExpandMoreIcon />}
              sx={{
                "& .MuiAccordionSummary-content": {
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center"
                }
              }}>
              <div
                style={{ display: "flex", alignItems: "center", gap: "10px" }}>
                {notice.important && (
                  <span className="important_text">중요</span>
                )}
                <Typography>{notice.title}</Typography>
              </div>
              <Typography variant="body2">
                {formatDate(notice.updatedAt || notice.createdAt)}
              </Typography>
            </AccordionSummary>
            <AccordionDetails>
              <div
                dangerouslySetInnerHTML={{ __html: notice.content }}
                style={{ wordBreak: "break-word" }}
              />

              {isLoggedIn && isAdmin(user) && (
                <Box sx={{ mt: 3 }}>
                  <PrimaryButton
                    onClick={() => handleEditNotice(notice.id)}
                    sx={{ mr: 1 }}>
                    수정
                  </PrimaryButton>
                  <DeleteButton onClick={() => handleDeleteNotice(notice.id)}>
                    삭제
                  </DeleteButton>
                </Box>
              )}
            </AccordionDetails>
          </Accordion>
        ))}

        {filteredNotices.length === 0 && (
          <Typography variant="body1" sx={{ mt: 2, textAlign: "center" }}>
            해당 카테고리에 공지사항이 없습니다.
          </Typography>
        )}
      </div>

      {/* 페이지네이션 - Router Integration */}
      <div className="pagination-container">
        <Pagination
          page={page}
          count={Math.ceil(totalRows / pageSize)}
          siblingCount={1}
          boundaryCount={1}
          renderItem={(item) => (
            <PaginationItem
              component={Link}
              to={`/notices?page=${item.page}&category=${categories[activeTab]}`}
              {...item}
            />
          )}
        />
      </div>
    </div>
  );
};

export default NoticeListAdmin;
