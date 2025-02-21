import React, { useState, useEffect } from 'react';
import { Line, Bar } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, BarElement, LineElement, Title, Tooltip, Legend, Filler } from 'chart.js';
import { fetchWithAuth } from '../../common/fetchWithAuth';
import { API_URL } from '../../constant';
import '../../assets/css/admin/Dashboard.css';
import { format } from 'date-fns'; // 날짜 포맷을 위한 라이브러리

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, BarElement, Title, Tooltip, Legend, Filler);

export default function Dashboard() {
    const [newRegistrations, setNewRegistrations] = useState([]);
    const [newDoctorApplications, setNewDoctorApplications] = useState([]);
    const [topSellingProducts, setTopSellingProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedChart, setSelectedChart] = useState(null);
    const [dailySales, setDailySales] = useState(0); // 일 매출 상태
    const [monthlySales, setMonthlySales] = useState(0); // 월 매출 상태
    const [selectedDate, setSelectedDate] = useState(new Date()); // 선택한 날짜
    const [selectedMonth, setSelectedMonth] = useState(new Date()); // 선택한 월

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [registrationsRes, applicationsRes, salesRes] = await Promise.all([
                    fetchWithAuth(`${API_URL}admin/newRegistrations`),
                    fetchWithAuth(`${API_URL}admin/newDoctorApplications`),
                    fetchWithAuth(`${API_URL}admin/topSellingProducts/PAYMENT_COMPLETED`)
                ]);

                setNewRegistrations(await registrationsRes.json());
                setNewDoctorApplications(await applicationsRes.json());
                setTopSellingProducts(await salesRes.json());
            } catch (err) {
                console.error("API 요청 실패:", err);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, []);

     // 일 매출을 가져오는 함수
        useEffect(() => {
            const fetchData = async () => {
                try {
                     // 일 매출 금액 API 호출 (선택한 날짜를 파라미터로 전달)
                    const dailySalesRes = await fetchWithAuth(`${API_URL}admin/dailySales/PAYMENT_COMPLETED?date=${format(selectedDate, 'yyyy-MM-dd')}`);
                    const dailySalesAmount = await dailySalesRes.json();
                    setDailySales(dailySalesAmount);

                    // 월 매출 금액 계산
                    const startDate = format(new Date(selectedMonth.getFullYear(), selectedMonth.getMonth(), 1), 'yyyy-MM-dd');
                    const endDate = format(new Date(selectedMonth.getFullYear(), selectedMonth.getMonth() + 1, 0), 'yyyy-MM-dd');

                    // 월 매출 금액 API 호출
                    const monthlySalesRes = await fetchWithAuth(`${API_URL}admin/monthlySales/PAYMENT_COMPLETED?startDate=${startDate}&endDate=${endDate}`);
                    const monthlySalesAmount = await monthlySalesRes.json();
                    setMonthlySales(monthlySalesAmount);
                } catch (err) {
                    console.error("API 요청 실패:", err);
                } finally {
                    setLoading(false);
                }
            };

            fetchData();
        }, [selectedDate, selectedMonth]); // 일 매출은 날짜가 바뀔 때마다, 월 매출은 월이 바뀔 때마다 실행됨

        // 이전 날로 이동
        const prevDay = () => {
            setSelectedDate(new Date(selectedDate.setDate(selectedDate.getDate() - 1)));
        };

        // 다음 날로 이동
        const nextDay = () => {
            setSelectedDate(new Date(selectedDate.setDate(selectedDate.getDate() + 1)));
        };

        // 이전 달로 이동
        const prevMonth = () => {
            setSelectedMonth(new Date(selectedMonth.setMonth(selectedMonth.getMonth() - 1)));
        };

        // 다음 달로 이동
        const nextMonth = () => {
            setSelectedMonth(new Date(selectedMonth.setMonth(selectedMonth.getMonth() + 1)));
        };


        // 선택된 날짜 포맷 (yyyy-MM-dd)
        const formattedDate = format(selectedDate, 'yyyy-MM-dd');
        // 선택된 월 포맷 (yyyy년 MM월)
        const formattedMonth = format(selectedMonth, 'yyyy년 MM월');

    const getChartData = (data, label, color) => ({
        labels: data.map(d => d.date),
        datasets: [{
            label: label,
            data: data.map(d => d.count),
            borderColor: color,
            backgroundColor: `${color}40`,
            fill: true,
            tension: 0.1,
        }],
    });

    const getRankingChartData = (data) => ({
        labels: data.map(d => d.itemName),
        datasets: [{
            label: '판매량',
            data: data.map(d => d.count),
            backgroundColor: ['#FF5733', '#33FF57', '#3357FF', '#F1C40F', '#8E44AD'],
        }],
    });

    return (
        <div className="dashboard-container">
            {loading ? (
                <p className="loading-text">데이터를 불러오는 중...</p>
            ) : (
                <div className="chart-grid">
                    <div className="chart-box" onClick={() => setSelectedChart('registrations')}>
                        <h2>일별 신규 가입자</h2>
                        <Line data={getChartData(newRegistrations, '신규 가입자', '#4CAF50')} />
                    </div>
                    <div className="chart-box" onClick={() => setSelectedChart('applications')}>
                        <h2>일별 신규 전문가 신청</h2>
                        <Line data={getChartData(newDoctorApplications, '신규 전문가 신청', '#FF9800')} />
                    </div>
                    <div className="chart-box" onClick={() => setSelectedChart('salesRanking')}>
                        <h2>판매 상품 랭킹 (Top 5)</h2>
                        <Bar data={getRankingChartData(topSellingProducts)} />
                    </div>
                </div>
            )}
            {selectedChart && (
                <div className="modal-overlay" onClick={() => setSelectedChart(null)}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <h2>{selectedChart === 'registrations' ? '일별 신규 가입자' : selectedChart === 'applications' ? '일별 신규 전문가 신청' : '판매 상품 랭킹 (Top 5)'}</h2>
                        {selectedChart === 'salesRanking' ? (
                            <Bar data={getRankingChartData(topSellingProducts)} />
                        ) : (
                            <Line data={selectedChart === 'registrations' ? getChartData(newRegistrations, '신규 가입자', '#4CAF50') : getChartData(newDoctorApplications, '신규 전문가 신청', '#FF9800')} />
                        )}
                    </div>
                </div>
            )}
           <div className="stats-grid">
               <div className="stat-box">
                   <h2>일별 매출 ({formattedDate})</h2>
                   <div className="day-navigation">
                       <button onClick={prevDay}>{'<'}</button>
                       <span>{formattedDate}</span>
                       <button onClick={nextDay}>{'>'}</button>
                   </div>
                   <p className="stat-value">{dailySales.toLocaleString()} 원</p>
               </div>
               <div className="stat-box">
                   <h2>월별 매출 ({formattedMonth})</h2>
                   <div className="month-navigation">
                       <button onClick={prevMonth}>{'<'}</button>
                       <span>{formattedMonth}</span>
                       <button onClick={nextMonth}>{'>'}</button>
                   </div>
                   <p className="stat-value">{monthlySales.toLocaleString()} 원</p>
               </div>
           </div>
        </div>
    );
}
