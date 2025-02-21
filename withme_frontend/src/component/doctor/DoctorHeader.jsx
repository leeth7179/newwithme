import React, { useEffect } from "react";
import {
  AppBar,
  Toolbar,
  Typography,
  Badge,
  IconButton,
  Button,
  Tooltip
} from "@mui/material";
import EmailIcon from "@mui/icons-material/Email";
import DashboardIcon from "@mui/icons-material/Dashboard";
import LogoutIcon from "@mui/icons-material/Logout";
import { useNavigate } from "react-router-dom";
import { useSelector, useDispatch } from "react-redux";
import useWebSocket from "../../hook/useWebSocket";
import { showSnackbar } from "../../redux/snackbarSlice";
import Swal from 'sweetalert2';
import { clearUser } from "../../redux/authSlice";
import { fetchWithAuth } from "../../common/fetchWithAuth";
import { API_URL } from "../../constant";
import { addMessage } from "../../redux/messageSlice";

const DoctorHeader = () => {
  const { user } = useSelector((state) => state.auth);
  const { unreadCount, latestMessage } = useSelector((state) => state.messages);
  const navigate = useNavigate();
  const dispatch = useDispatch();

  // WebSocket 연결 및 메시지 핸들러 받기
  const { connected } = useWebSocket(user);

  // 새 메시지 알림 처리
  useEffect(() => {
    if (connected && window.stompClient && user?.id) {
      const topic = `/topic/chat/${user.id}`;

      const subscription = window.stompClient.subscribe(topic, (message) => {
        try {
          const receivedMessage = JSON.parse(message.body);

          // 메시지 Redux 상태에 추가
          dispatch(addMessage(receivedMessage));

          // 팝업 알림
          Swal.fire({
            title: '새로운 상담 요청',
            text: `${receivedMessage.senderName || '회원'}님으로부터 새로운 문의가 도착했습니다.`,
            icon: 'info',
            showCancelButton: true,
            confirmButtonText: '확인하기',
            cancelButtonText: '나중에',
            confirmButtonColor: '#1976d2',
            cancelButtonColor: '#d32f2f'
          }).then((result) => {
            if (result.isConfirmed) {
              navigate('/doctor/messages');
            }
          });

          // 스낵바 알림
          dispatch(showSnackbar({
            message: `새로운 메시지: ${receivedMessage.content}`,
            severity: "info"
          }));
        } catch (error) {
          console.error('메시지 처리 오류:', error);
        }
      });

      // 컴포넌트 언마운트 시 구독 해제
      return () => {
        if (subscription) {
          subscription.unsubscribe();
        }
      };
    }
  }, [connected, user, dispatch, navigate]);

  // 메시지 페이지로 이동
  const handleNavigateToMessages = () => {
    navigate("/doctor/messages");
  };

  // 대시보드로 이동
  const handleNavigateToDashboard = () => {
    navigate("/doctor/dashboard");
  };

  // 로그아웃 처리
  const handleLogout = async () => {
    try {
      await fetchWithAuth(`${API_URL}auth/logout`, { method: "POST" });
      dispatch(clearUser());
      dispatch(showSnackbar("로그아웃 되었습니다."));
      navigate("/login");
    } catch (error) {
      console.error("로그아웃 실패:", error);
      dispatch(showSnackbar("로그아웃 중 오류가 발생했습니다."));
    }
  };

  return (
    <AppBar position="static" sx={{ bgcolor: "#1976d2" }}>
      <Toolbar>
        {/* 좌측 타이틀 및 대시보드 버튼 */}
        <Typography
          variant="h6"
          sx={{
            flexGrow: 1,
            fontWeight: "bold",
            display: 'flex',
            alignItems: 'center',
            gap: 2
          }}
        >
          🏥 전문의 대시보드
          <Tooltip title="대시보드">
            <IconButton
              color="inherit"
              onClick={handleNavigateToDashboard}
              sx={{ ml: 2 }}
            >
              <DashboardIcon />
            </IconButton>
          </Tooltip>
        </Typography>

        {/* 우측 메뉴 */}
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          {/* 사용자 이름 표시 */}
          <Typography variant="body1" sx={{ mr: 2 }}>
            {user?.name} 전문의
          </Typography>

          {/* 메시지 알림 아이콘 */}
          <Tooltip title="메시지 확인">
            <IconButton
              color="inherit"
              onClick={handleNavigateToMessages}
              sx={{
                position: 'relative',
                '&:hover': { backgroundColor: 'rgba(255, 255, 255, 0.1)' }
              }}
            >
              <Badge
                badgeContent={unreadCount}
                color="error"
                sx={{
                  '& .MuiBadge-badge': {
                    fontSize: '0.8rem',
                    minWidth: '20px',
                    height: '20px'
                  }
                }}
              >
                <EmailIcon />
              </Badge>
            </IconButton>
          </Tooltip>

          {/* 로그아웃 버튼 */}
          <Tooltip title="로그아웃">
            <IconButton
              color="inherit"
              onClick={handleLogout}
              sx={{ '&:hover': { backgroundColor: 'rgba(255, 255, 255, 0.1)' } }}
            >
              <LogoutIcon />
            </IconButton>
          </Tooltip>
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default DoctorHeader;