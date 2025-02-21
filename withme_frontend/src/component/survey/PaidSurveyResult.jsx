import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import {
  PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer
} from "recharts";
import {
  Paper, Button, Box, Typography, TextField, CircularProgress, Grid
} from "@mui/material";
import { fetchWithAuth } from "../../common/fetchWithAuth";
import { API_URL } from "../../constant";
import { useSelector, useDispatch } from "react-redux";
import Swal from 'sweetalert2';
import img2 from "../../image/img2.png";
import { addMessage } from "../../redux/messageSlice";
import useWebSocket from "../../hook/useWebSocket";

const PaidSurveyResultPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const dispatch = useDispatch();

  const topicScores = location.state?.topicScores || [];
  const [newMessage, setNewMessage] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [loading, setLoading] = useState(true);
  const { user } = useSelector((state) => state.auth);
  const { connected } = useWebSocket(user);

  const topicQuestionsCount = {
    1: 15, 2: 16, 3: 15, 4: 12, 6: 14, 7: 15, 8: 11,
    9: 10, 10: 8, 11: 14, 12: 13, 13: 13, 14: 13,
    15: 15, 16: 19, 17: 13, 18: 20, 19: 15
  };

  const topicNameMap = {
    1: "소화 건강", 2: "피부 건강", 3: "구강 건강", 4: "체중 관리",
    6: "털과 모질 관리", 7: "눈 건강", 8: "행동 건강", 9: "면역 체계",
    10: "간 건강", 11: "신장 기능", 12: "요로 건강", 13: "에너지 수준",
    14: "노화 및 이동성", 15: "기생충 관리", 16: "백신 접종 이력",
    17: "스트레스 및 불안", 18: "영양 균형", 19: "알레르기 관리"
  };

  const topicColors = [
    { score: '#FF1493', remaining: '#FFE4E8' }, // 딥 핑크
    { score: '#4169E1', remaining: '#E3F2FD' }, // 로얄 블루
    { score: '#32CD32', remaining: '#E8F5E9' }, // 라임 그린
    { score: '#FF4500', remaining: '#FFE4DC' }, // 오렌지 레드
    { score: '#9370DB', remaining: '#F5E6F5' }, // 미디엄 퍼플
    { score: '#20B2AA', remaining: '#E0F2F1' }, // 라이트 시 그린
    { score: '#FF6B6B', remaining: '#FFE4E4' }, // 코랄
    { score: '#4682B4', remaining: '#E3F2FD' }, // 스틸 블루
    { score: '#DA70D6', remaining: '#F8E6F8' }, // 오키드
    { score: '#CD853F', remaining: '#FFF3E0' }, // 페루
    { score: '#6B8E23', remaining: '#F1F8E9' }, // 올리브 드랩
    { score: '#BA55D3', remaining: '#F3E5F5' }, // 미디엄 오키드
    { score: '#FF4081', remaining: '#FCE4EC' }, // 핑크 A200
    { score: '#FFA500', remaining: '#FFF3E0' }, // 오렌지
    { score: '#2E8B57', remaining: '#E8F5E9' }, // 시 그린
    { score: '#8A2BE2', remaining: '#EDE7F6' }, // 블루 바이올렛
    { score: '#FF7F50', remaining: '#FBE9E7' }, // 코랄
    { score: '#C71585', remaining: '#FCE4EC' }  // 미디엄 바이올렛 레드
  ];

  const handleMessageSubmit = async () => {
    if (!user?.id) {
      Swal.fire('🚨 오류', '사용자 정보가 유효하지 않습니다. 다시 로그인해 주세요.', 'error');
      navigate("/login");
      return;
    }

    if (!newMessage.trim()) {
      Swal.fire('⚠️ 경고', '메시지 내용을 입력해주세요.', 'warning');
      return;
    }

    if (newMessage.length > 500) {
      Swal.fire('⚠️ 경고', '메시지는 500자 이내로 작성해주세요.', 'warning');
      return;
    }

    const messageData = {
      senderId: user.id,
      senderName: user.name,
      receiverId: 4,
      content: newMessage,
      messageType: "question",
      timestamp: new Date().toISOString()
    };

    setIsSubmitting(true);

    try {
      const response = await fetchWithAuth(`${API_URL}messages/send`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Accept": "application/json"
        },
        body: JSON.stringify(messageData),
      });

      const responseBody = await response.text();

      if (response.ok) {
        try {
          const result = JSON.parse(responseBody);
          dispatch(addMessage(result));
        } catch (jsonError) {
          console.log('📬 텍스트 응답:', responseBody);
        }

        Swal.fire('✅ 성공', '메시지가 전송되었습니다.', 'success');
        setNewMessage('');
      } else {
        console.error('🚨 서버 응답 오류:', responseBody);
        Swal.fire('🚨 오류', responseBody, 'error');
      }
    } catch (error) {
      console.error('🚨 전송 오류:', error);
      Swal.fire('🚨 오류', '메시지 전송 중 문제가 발생했습니다.', 'error');
    } finally {
      setIsSubmitting(false);
    }
  };

  let data = [];
  try {
    data = topicScores
      .filter((item) => item.topic !== "5")
      .map(({ topic, score }, index) => {
        const totalPerTopic = topicQuestionsCount[topic] * 5;
        const topicName = topicNameMap[topic] || "알 수 없는 주제";
        return {
          name: topicName,
          score,
          total: totalPerTopic,
          chartData: [
            {
              name: "획득 점수",
              value: score,
              color: topicColors[index % topicColors.length].score
            },
            {
              name: "남은 점수",
              value: totalPerTopic - score,
              color: topicColors[index % topicColors.length].remaining
            }
          ]
        };
      });
  } catch (error) {
    console.error("🛑 데이터 처리 중 오류 발생:", error);
  }

  useEffect(() => {
    if (data.length > 0) {
      setLoading(false);
    } else {
      setTimeout(() => setLoading(false), 1500);
    }
  }, [data]);

  useEffect(() => {
    if (window.stompClient && user?.id) {
      window.stompClient.subscribe(`/topic/chat/${user.id}`, (message) => {
        const parsedMsg = JSON.parse(message.body);
        if (parsedMsg.messageType === "answer") {
          Swal.fire("📬 새로운 답변 도착!", `💬 ${parsedMsg.content}`, "info");
        }
      });
    }
  }, [user?.id]);

  if (loading) {
    return (
      <Box sx={{
        height: "100vh",
        display: "flex",
        alignItems: "center",
        justifyContent: "center"
      }}>
        <CircularProgress color="secondary" />
      </Box>
    );
  }

  if (data.length === 0) {
    return (
      <Paper elevation={3} sx={{ p: 4, textAlign: "center" }}>
        <Typography variant="h6" sx={{ mb: 2 }}>🚫 결과를 찾을 수 없습니다.</Typography>
        <Button variant="contained" color="primary" onClick={() => navigate("/survey/paid/selection")}>
          🛠️ 문진 시작하기
        </Button>
      </Paper>
    );
  }

  const getGridSize = (totalItems) => {
    if (totalItems <= 4) return 6;
    if (totalItems <= 8) return 4;
    if (totalItems <= 12) return 3;
    return 2;
  };

  const gridSize = getGridSize(data.length);
  const chartSize = data.length <= 4 ? 400 : data.length <= 8 ? 300 : 250;

  return (
    <Box sx={{
      minHeight: "100vh",
      width: "100%",
      backgroundColor: "#FFFBF8",
      pb: 5
    }}>
      <Box sx={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        py: 4,
        borderBottom: "2px solid #FFB6C1"
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
           유료 문진 검사 결과
        </Typography>
      </Box>

      <Box sx={{
        display: "flex",
        flexWrap: "wrap",
        justifyContent: "center",
        gap: 2,
        mt: 4
      }}>
        {data.map(({ name, score, total }, index) => (
          <Box key={index} sx={{
            backgroundColor: "#FFF0E8",
            color: "#4E342E",
            fontSize: "1.2rem",
            fontWeight: "bold",
            px: 3,
            py: 2,
            borderRadius: "10px",
            minWidth: "180px",
            textAlign: "center",
            boxShadow: "0px 2px 4px rgba(0, 0, 0, 0.1)"
          }}>
            {name} : {score} / {total}점
          </Box>
        ))}
      </Box>

      <Box sx={{ p: 4 }}>
        <Grid container spacing={3} justifyContent="center">
          {data.map((item, index) => (
            <Grid item xs={12} sm={gridSize} key={index}>
              <Box sx={{
                textAlign: "center",
                p: 2,
                backgroundColor: "#FFF5F0",
                borderRadius: "15px",
                boxShadow: "0px 4px 20px rgba(0, 0, 0, 0.1)",
                transition: "transform 0.3s ease",
                "&:hover": {
                  transform: "translateY(-5px)"
                }
              }}>
                <Typography variant="h6" sx={{ mb: 2, color: "#E75480" }}>
                  {item.name}
                </Typography>
                <ResponsiveContainer width="100%" height={chartSize}>
                  <PieChart>
                    <defs>
                      {/* 그림자 효과 */}
                      <filter id={`shadow-${index}`} height="200%">
                        <feDropShadow dx="0" dy="4" stdDeviation="6" floodOpacity="0.2"/>
                      </filter>
                      {/* 그라데이션 */}
                      <linearGradient id={`gradient1-${index}`} x1="0" y1="0" x2="0" y2="1">
                        <stop offset="0%" stopColor={item.chartData[0].color} stopOpacity={1}/>
                        <stop offset="100%" stopColor={item.chartData[0].color} stopOpacity={0.8}/>
                      </linearGradient>
                      <linearGradient id={`gradient2-${index}`} x1="0" y1="0" x2="0" y2="1">
                        <stop offset="0%" stopColor={item.chartData[1].color} stopOpacity={0.8}/>
                        <stop offset="100%" stopColor={item.chartData[1].color} stopOpacity={0.6}/>
                      </linearGradient>
                    </defs>

                    {/* 배경 원 (그림자 효과용) */}
                    <Pie
                      data={[{ value: 100 }]}
                      cx="50%"
                      cy="50%"
                      innerRadius={chartSize * 0.3 - 2}
                      outerRadius={chartSize * 0.4 + 2}
                      fill="#FFF"
                      filter={`url(#shadow-${index})`}
                    />

                    {/* 메인 도넛 차트 */}
                    <Pie
                      data={item.chartData}
                      cx="50%"
                      cy="50%"
                      innerRadius={chartSize * 0.3}
                      outerRadius={chartSize * 0.4}
                      startAngle={90}
                      endAngle={-270}
                      paddingAngle={4}
                      dataKey="value"
                      isAnimationActive={true} // ✅ 애니메이션 활성화
                      animationDuration={1200} // ✅ 애니메이션 속도 조절
                      animationEasing="ease-out" // ✅ 부드러운 애니메이션 적용
                      onMouseEnter={(e, i) => { e.payload.scale = 1.1; }} // ✅ 호버 시 확대 효과
                      onMouseLeave={(e, i) => { e.payload.scale = 1; }} // ✅ 마우스 떠날 때 원래 크기로
                    >
                      {item.chartData.map((entry, i) => (
                        <Cell
                          key={`cell-${i}`}
                          fill={`url(#gradient${i+1}-${index})`}
                          stroke={entry.color}
                          strokeWidth={2}
                          style={{ transform: `scale(${entry.scale || 1})`, transition: "transform 0.3s ease-in-out" }} // ✅ 확대 애니메이션
                        />
                      ))}
                    </Pie>

                    {/* 중앙 텍스트 */}
                    <text
                      x="50%"
                      y="50%"
                      textAnchor="middle"
                      dominantBaseline="middle"
                      fill="#333"
                      fontSize="16"
                      fontWeight="bold"
                    >
                      {`${Math.round((item.chartData[0].value / (item.chartData[0].value + item.chartData[1].value)) * 100)}%`}
                    </text>
                  </PieChart>
                </ResponsiveContainer>

                <Box sx={{
                  mt: 2,
                  display: 'flex',
                  flexDirection: 'column',
                  gap: 1
                }}>
                  <Typography sx={{
                    color: '#333',
                    fontSize: '1rem',
                    fontWeight: 500,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    gap: 1
                  }}>
                    <span style={{
                      width: '12px',
                      height: '12px',
                      borderRadius: '50%',
                      backgroundColor: item.chartData[0].color,
                      display: 'inline-block'
                    }}></span>
                    획득 점수: {item.chartData[0].value}점
                  </Typography>
                  <Typography sx={{
                    color: '#333',
                    fontSize: '1rem',
                    fontWeight: 500,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    gap: 1
                  }}>
                    <span style={{
                      width: '12px',
                      height: '12px',
                      borderRadius: '50%',
                      backgroundColor: item.chartData[1].color,
                      display: 'inline-block'
                    }}></span>
                    남은 점수: {item.chartData[1].value}점
                  </Typography>
                </Box>
              </Box>
            </Grid>
          ))}
        </Grid>
      </Box>

      <Box sx={{
        mt: 5,
        px: 4,
        py: 3,
        mx: 4,
        border: "2px solid #FFB6C1",
        borderRadius: 2,
        backgroundColor: "#FFF5F0",
        boxShadow: "0px 2px 4px rgba(0, 0, 0, 0.1)"
      }}>
        <Typography variant="h5" sx={{
          fontWeight: "bold",
          color: "#E75480",
          mb: 3,
          textAlign: "center"
        }}>
          🩺 전문의 상담
        </Typography>

        <TextField
          fullWidth
          value={newMessage}
          onChange={(e) => {
            const value = e.target.value;
            if (value.length <= 500) {
              setNewMessage(value);
            }
          }}
          placeholder="전문의에게 문의하실 내용을 입력하세요..."
          variant="outlined"
          multiline
          rows={5}
          helperText={`${newMessage.length}/500`}
          sx={{
            backgroundColor: 'white',
            mb: 3,
            '& .MuiOutlinedInput-root': {
              '& fieldset': { borderColor: '#FFB6C1' },
              '&:hover fieldset': { borderColor: '#E75480' },
              '&.Mui-focused fieldset': { borderColor: '#E75480' }
            }
          }}
        />

        <Box sx={{ textAlign: "center" }}>
                  <Button
                    variant="contained"
                    onClick={handleMessageSubmit}
                    disabled={!newMessage.trim() || isSubmitting}
                    sx={{
                      backgroundColor: "#E75480",
                      width: '200px',
                      '&:hover': { backgroundColor: "#FF69B4" },
                      '&:disabled': { backgroundColor: "#FFB6C1" }
                    }}
                  >
                    {isSubmitting ? '⌛ 전송 중...' : '📨 상담 요청'}
                  </Button>
                </Box>
              </Box>
            </Box>
          );
        };

        export default PaidSurveyResultPage;