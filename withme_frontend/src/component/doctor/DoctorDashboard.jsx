import React, { useState, useEffect } from "react";
import {
  Typography,
  Grid,
  Card,
  CardContent,
  Button,
  Box,
  CircularProgress
} from "@mui/material";
import { useSelector } from "react-redux";
import { fetchWithAuth } from "../../common/fetchWithAuth";
import { API_URL } from "../../constant";
import { useNavigate } from "react-router-dom";
import useWebSocket from "../../hook/useWebSocket"; // ✅ WebSocket Hook 호출

const DoctorDashboard = () => {
  const { user } = useSelector((state) => state.auth);
  const [dashboardData, setDashboardData] = useState({
    newConsultRequests: 0,
    pendingMessages: 0,
    recentConsultations: []
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  // ✅ WebSocket Hook 호출
  useWebSocket(user);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        const response = await fetchWithAuth(`${API_URL}doctor/dashboard/${user.id}`);
        if (!response.ok) throw new Error("대시보드 데이터를 불러오는 데 실패했습니다.");

        const data = await response.json();
        setDashboardData(data);
        setError(null);
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    };

    if (user?.id) fetchDashboardData();
  }, [user]);

  if (loading) return <CircularProgress />;
  if (error) return <Typography color="error">{error}</Typography>;

  return (
    <Box sx={{ flexGrow: 1, p: 3 }}>
      <Typography variant="h4" gutterBottom>
        {user.name} 전문의 대시보드
      </Typography>
      <Grid container spacing={3}>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6">새로운 상담 요청</Typography>
              <Typography variant="h4" color="primary">
                {dashboardData.newConsultRequests}건
              </Typography>
              <Button variant="outlined" onClick={() => navigate("/doctor/messages")}>
                상담 확인하기
              </Button>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6">답변 대기 중</Typography>
              <Typography variant="h4" color="error">
                {dashboardData.pendingMessages}건
              </Typography>
              <Button variant="outlined" color="error" onClick={() => navigate("/doctor/messages")}>
                메시지 확인
              </Button>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6">최근 상담 이력</Typography>
              {dashboardData.recentConsultations.length > 0 ? (
                dashboardData.recentConsultations.map((consult) => (
                  <Typography key={consult.id}>{consult.memberName} - {consult.date}</Typography>
                ))
              ) : (
                <Typography color="textSecondary">최근 상담 이력이 없습니다.</Typography>
              )}
              <Button variant="outlined" onClick={() => navigate("/doctor/consultations")}>
                전체 이력 보기
              </Button>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default DoctorDashboard;
