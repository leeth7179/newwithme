import React, { useEffect } from "react";
import {
  Box,
  Typography,
  List,
  ListItem,
  ListItemText,
  Paper,
  CircularProgress,
} from "@mui/material";
import { useSelector, useDispatch } from "react-redux";
import { fetchWithAuth } from "../../common/fetchWithAuth";
import { API_URL } from "../../constant";
import useWebSocket from "../../hook/useWebSocket";
import { setMessages, markMessageAsRead } from "../../redux/messageSlice";
import { showSnackbar } from "../../redux/snackbarSlice";

const MemberMessageList = () => {
  const { user } = useSelector((state) => state.auth);
  const { messages, unreadCount } = useSelector((state) => state.messages);
  const dispatch = useDispatch();

  // WebSocket 연결
  useWebSocket(user);

  // 메시지 목록 조회
  useEffect(() => {
    const fetchMessages = async () => {
      try {
        const response = await fetchWithAuth(`${API_URL}messages/received/${user.id}`);
        if (response.ok) {
          const data = await response.json();
          dispatch(setMessages(data));
        }
      } catch (error) {
        console.error("메시지 조회 실패:", error);
        dispatch(showSnackbar("메시지 목록을 불러오는데 실패했습니다."));
      }
    };

    fetchMessages();
  }, [user.id, dispatch]);

  // 메시지 읽음 처리
  const handleMarkAsRead = async (messageId) => {
    try {
      const response = await fetchWithAuth(`${API_URL}messages/read/${messageId}`, {
        method: "POST"
      });

      if (response.ok) {
        dispatch(markMessageAsRead(messageId));
      }
    } catch (error) {
      console.error("메시지 읽음 처리 실패:", error);
      dispatch(showSnackbar("메시지 읽음 처리에 실패했습니다."));
    }
  };

  // 날짜 포맷팅 함수
  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  if (!messages) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3, maxWidth: 800, mx: 'auto' }}>
      <Box sx={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        mb: 3
      }}>
        <Typography variant="h5" fontWeight="bold">
          받은 답변 목록
        </Typography>
        <Typography variant="subtitle1" color="primary">
          읽지 않은 메시지: {unreadCount}개
        </Typography>
      </Box>

      {messages.length === 0 ? (
        <Paper sx={{ p: 3, textAlign: 'center' }}>
          <Typography color="textSecondary">
            아직 받은 답변이 없습니다.
          </Typography>
        </Paper>
      ) : (
        <List>
          {messages.map((message) => (
            <Paper
              key={message.id}
              elevation={2}
              sx={{
                p: 3,
                mb: 2,
                backgroundColor: message.read ? '#f8f9fa' : '#fff3e0',
                transition: 'all 0.3s ease',
                '&:hover': {
                  transform: 'translateY(-2px)',
                  boxShadow: 3
                }
              }}
            >
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                <Typography variant="subtitle2" color="primary">
                  {message.senderName ? `${message.senderName} 전문의` : '전문의'} 답변
                </Typography>
                <Typography variant="caption" color="textSecondary">
                  {formatDate(message.regTime)}
                </Typography>
              </Box>
              <ListItemText
                primary={message.content}
                sx={{
                  '& .MuiListItemText-primary': {
                    whiteSpace: 'pre-wrap',
                    lineHeight: 1.6
                  }
                }}
              />
              {!message.read && (
                <Box sx={{ mt: 2, textAlign: 'right' }}>
                  <Typography
                    variant="button"
                    color="primary"
                    sx={{ cursor: 'pointer' }}
                    onClick={() => handleMarkAsRead(message.id)}
                  >
                    읽음 표시
                  </Typography>
                </Box>
              )}
            </Paper>
          ))}
        </List>
      )}
    </Box>
  );
};

export default MemberMessageList;
