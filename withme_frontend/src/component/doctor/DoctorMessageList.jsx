import React, { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import { fetchWithAuth } from "../../common/fetchWithAuth";
import { API_URL } from "../../constant";
import {
  Box,
  Typography,
  Button,
  CircularProgress,
  TextField,
  Paper,
  Snackbar,
  Alert,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  Pagination
} from '@mui/material';
import img2 from "../../image/img2.png";

const DoctorMessageList = () => {
  const navigate = useNavigate();
  const { user } = useSelector((state) => state.auth);
  const [localMessages, setLocalMessages] = useState([]);
  const [replyContent, setReplyContent] = useState("");
  const [selectedMessage, setSelectedMessage] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [isReplying, setIsReplying] = useState(false);
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const getFifteenDaysAgo = () => {
    const date = new Date();
    date.setDate(date.getDate() - 15);
    return date;
  };

  const calculateDaysLeft = (regTime) => {
    const messageDate = new Date(regTime);
    const currentDate = new Date();
    const timeDiff = messageDate.getTime() - currentDate.getTime();
    const daysLeft = 15 - Math.ceil(timeDiff / (1000 * 3600 * 24));
    return Math.max(0, daysLeft);
  };

  const fetchMessagesByPage = useCallback(async (page) => {
    setLoading(true);
    try {
      const response = await fetchWithAuth(`${API_URL}messages/${user.id}?page=${page - 1}&size=8`);
      if (response.ok) {
        const data = await response.json();
        setLocalMessages(data.content);
        setTotalPages(data.totalPages);
      } else {
        showPopup("메시지를 불러오는데 실패했습니다.");
      }
    } catch (error) {
      console.error('메시지 로드 중 오류:', error);
      showPopup("네트워크 오류가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  }, [user.id]);

  useEffect(() => {
    fetchMessagesByPage(currentPage);
  }, [fetchMessagesByPage, currentPage]);

  const handleOpenMessage = useCallback((message) => {
    if (!message) return;

    setSelectedMessage(message);
    setIsReplying(false);

    if (message.id && !message.read) {
      setLocalMessages(prevMessages =>
        prevMessages.map(msg =>
          msg.id === message.id ? {...msg, read: true} : msg
        )
      );

      fetchWithAuth(`${API_URL}messages/read?messageId=${message.id}`, {
        method: "POST"
      }).catch(error => {
        console.error('메시지 읽음 처리 실패:', error);
        showPopup("메시지 읽음 처리 중 오류 발생");
      });
    }
  }, []);

  const showPopup = (msg) => {
    setSnackbarMessage(msg);
    setSnackbarOpen(true);
  };

  const handleCloseSnackbar = () => {
    setSnackbarOpen(false);
  };

  const handleReply = async () => {
    if (!replyContent.trim()) {
      showPopup("답장 내용을 입력하세요.");
      return;
    }

    if (!selectedMessage) {
      showPopup("답장을 보낼 메시지를 선택하세요.");
      return;
    }

    try {
      const response = await fetchWithAuth(`${API_URL}messages/send`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          senderId: user.id,
          receiverId: selectedMessage.senderId,
          content: replyContent.trim()
        }),
      });

      if (response.ok) {
        // 메시지 전송 성공 후 목록 새로고침
        await fetchMessagesByPage(currentPage);
        setReplyContent("");
        setIsReplying(false);
        showPopup("답장이 성공적으로 전송되었습니다.");
      } else {
        const errorData = await response.json();
        showPopup(errorData.message || "답장 전송에 실패했습니다.");
      }
    } catch (error) {
      console.error("답장 전송 중 오류:", error);
      showPopup("네트워크 오류로 답장을 전송하지 못했습니다.");
    }
  };

  if (loading) {
    return (
      <Box sx={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100vh',
        backgroundColor: '#FFFBF8'
      }}>
        <CircularProgress sx={{ color: '#E75480' }} />
      </Box>
    );
  }

  return (
    <Box sx={{
      display: 'flex',
      flexDirection: 'column',
      height: 'calc(100vh - 100px)',
      maxWidth: 1200,
      margin: 'auto',
      backgroundColor: '#FFFBF8',
      p: 3,
      borderRadius: 2
    }}>
      <Box sx={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        mb: 4,
        borderBottom: "2px solid #FFB6C1",
        pb: 2
      }}>
        <img src={img2} alt="Pet" style={{ width: "120px", marginRight: "20px" }} />
        <Typography variant="h4" sx={{
          fontWeight: "bold",
          color: "#E75480",
          backgroundColor: "#FFF5F0",
          px: 3,
          py: 2,
          borderRadius: "15px",
        }}>
          📬 받은 메시지 목록
        </Typography>
      </Box>

      <Box sx={{ display: 'flex', flexGrow: 1, gap: 2, mb: 2 }}>
        <Paper sx={{
          width: '50%',
          backgroundColor: '#FFF5F0',
          borderRadius: 2,
          p: 2
        }}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell align="center" sx={{ fontWeight: 'bold', backgroundColor: '#FFE4E8' }}>보낸 사람</TableCell>
                <TableCell align="center" sx={{ fontWeight: 'bold', backgroundColor: '#FFE4E8' }}>내용</TableCell>
                <TableCell align="center" sx={{ fontWeight: 'bold', backgroundColor: '#FFE4E8' }}>시간</TableCell>
                <TableCell align="center" sx={{ fontWeight: 'bold', backgroundColor: '#FFE4E8' }}>남은 일수</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {localMessages.map((message) => {
                const daysLeft = calculateDaysLeft(message.regTime);
                return (
                  <TableRow
                    key={message.id}
                    onClick={() => handleOpenMessage(message)}
                    sx={{
                      cursor: 'pointer',
                      '&:hover': { backgroundColor: '#FFF0F0' },
                      opacity: daysLeft === 0 ? 0.5 : 1
                    }}
                  >
                    <TableCell align="center">{message.senderName}</TableCell>
                    <TableCell align="left" sx={{
                      color: message.read ? '#666' : '#000',
                      fontWeight: message.read ? 'normal' : 'bold'
                    }}>
                      {message.content.substring(0, 50)}...
                    </TableCell>
                    <TableCell align="center">
                      {new Date(message.regTime).toLocaleString()}
                    </TableCell>
                    <TableCell align="center">
                      <Typography variant="caption" sx={{
                        color: daysLeft <= 3 ? '#E75480' : '#666',
                        fontWeight: daysLeft <= 3 ? 'bold' : 'normal'
                      }}>
                        {daysLeft === 0 ? '삭제 예정' : `${daysLeft}일 후 삭제`}
                      </Typography>
                    </TableCell>
                  </TableRow>
                );
              })}
            </TableBody>
          </Table>

          <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2 }}>
            <Pagination
              count={totalPages}
              page={currentPage}
              onChange={(e, value) => setCurrentPage(value)}
              color="primary"
              shape="rounded"
              siblingCount={1}
              boundaryCount={0}
              sx={{
                '& .MuiPaginationItem-root': {
                  color: '#E75480',
                  '&.Mui-selected': {
                    backgroundColor: '#FFE4E8'
                  },
                  '&.MuiPaginationItem-page': {
                    minWidth: '32px',
                    height: '32px',
                    fontSize: '14px',
                    margin: '0 2px',
                  },
                  '&.MuiPaginationItem-previousNext': {
                    minWidth: '32px',
                    height: '32px',
                    fontSize: '14px',
                    margin: '0 2px',
                  }
                }
              }}
            />
          </Box>
        </Paper>

        <Paper sx={{
          width: '50%',
          p: 3,
          display: 'flex',
          flexDirection: 'column',
          backgroundColor: '#FFF5F0',
          borderRadius: 2
        }}>
          {selectedMessage ? (
            <>
              <Typography variant="h6" sx={{
                mb: 2,
                color: '#E75480',
                fontWeight: 'bold'
              }}>
                선택된 메시지
              </Typography>
              <Box sx={{
                mb: 2,
                flexGrow: 1,
                overflowY: 'auto',
                backgroundColor: '#FFFFFF',
                p: 2,
                borderRadius: 1
              }}>
                <Typography variant="body1">{selectedMessage.content}</Typography>
                <Typography variant="caption" sx={{
                  display: 'block',
                  mt: 1,
                  color: '#666'
                }}>
                  보낸 사람: {selectedMessage.senderName} |
                  시간: {new Date(selectedMessage.regTime).toLocaleString()}
                </Typography>
              </Box>
              <Button
                variant="contained"
                onClick={() => setIsReplying(true)}
                fullWidth
                sx={{
                  mb: 2,
                  backgroundColor: '#E75480',
                  '&:hover': {
                    backgroundColor: '#FF69B4'
                  }
                }}
              >
                답변하기
              </Button>
              {isReplying && (
                <>
                  <TextField
                    multiline
                    rows={4}
                    value={replyContent}
                    onChange={(e) => setReplyContent(e.target.value)}
                    fullWidth
                    placeholder="답장 내용을 입력하세요."
                    sx={{
                      mb: 2,
                      '& .MuiOutlinedInput-root': {
                        backgroundColor: '#FFFFFF',
                        '& fieldset': {
                          borderColor: '#FFB6C1'
                        },
                        '&:hover fieldset': {
                          borderColor: '#E75480'
                        },
                        '&.Mui-focused fieldset': {
                          borderColor: '#E75480'
                        }
                      }
                    }}
                  />
                  <Button
                    variant="contained"
                    onClick={handleReply}
                    fullWidth
                    sx={{
                      backgroundColor: '#E75480',
                      '&:hover': {
                        backgroundColor: '#FF69B4'
                      }
                    }}
                  >
                    💬 답장 전송
                  </Button>
                </>
              )}
            </>
          ) : (
            <Typography variant="body1" sx={{ color: '#666' }}>
              메시지를 선택하세요.
            </Typography>
          )}
        </Paper>
      </Box>

      <Snackbar
        open={snackbarOpen}
        autoHideDuration={5000}
        onClose={handleCloseSnackbar}
        anchorOrigin={{ vertical: "top", horizontal: "center" }}
      >
        <Alert
          onClose={handleCloseSnackbar}
          severity="info"
          sx={{ width: '100%' }}
        >
          {snackbarMessage}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default DoctorMessageList;
