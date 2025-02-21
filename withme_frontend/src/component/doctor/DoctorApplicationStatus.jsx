import React, { useState, useEffect } from "react";
import { fetchWithAuth } from "../../common/fetchWithAuth"; // 인증된 fetch 함수
import { API_URL } from "../../constant"; // API 기본 URL
import { useNavigate } from "react-router-dom";
import "../../assets/css/doctor/DoctorApplicationStatus.css";

/**
 * 전문가 신청 상태 조회(사용자)
 */
export default function DoctorApplicationStatus({ user }) {
    const [doctor, setDoctor] = useState(null);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchDoctorApplication = async () => {
            try {
                const response = await fetchWithAuth(`${API_URL}doctors/application/${user.id}`, {
                    method: "GET",
                });

                if (response.ok) {
                    const data = await response.json();
                    setDoctor(data);
                    console.log(data);
                } else if (response.status === 404) {
                    setError("현재 신청된 정보가 없습니다. 전문가 신청을 진행해주세요.");
                } else {
                    setError("서버 오류로 신청 정보를 불러오지 못했습니다.");
                }
            } catch (error) {
                navigate("/unauthorized");
            } finally {
                setLoading(false);
            }
        };

        fetchDoctorApplication();
    }, []);

    // 상태값 한글 변환 함수
    const getStatusText = (status) => {
        switch (status) {
            case "APPROVED":
                return "승인됨";
            case "PENDING":
                return "심사 중";
            case "REJECTED":
                return "거절됨";
            case "ON_HOLD":
                return "보류 중";
            default:
                return status;
        }
    };

    // 로딩 화면
    if (loading) {
        return <div>⏳ 로딩 중...</div>;
    }

    // 오류 발생 시
    if (error) {
        return <div style={{ color: "red" }}>{error}</div>;
    }
const formatPhoneNumber = (phone) => {
  return phone.replace(/(\d{3})(\d{4})(\d{4})/, "$1-$2-$3");
};

    return (
        <div className="doctor-application-status">
            <div className="status-header">
                <h2>수의사 신청 정보</h2>
            </div>

            {/* 신청 진행 과정 */}
            <div className="status-container">
                <div className={`status-step`}>
                    <p>신청접수</p>
                </div>
                <p> > </p>
                {(doctor.status === "REJECTED" || doctor.status === "ON_HOLD") ? (
                    <div className="status-step rejected">
                        <p>{doctor.status === "REJECTED" ? "거절" : "보류"}</p>
                    </div>
                ) : (
                    <div className={`status-step ${doctor.status === "PENDING" ? "active" : ""}`}>
                        <p>서류심사중</p>
                    </div>
                )}
                <p> > </p>
                <div className={`status-step ${doctor.status === "APPROVED" ? "active" : ""}`}>
                    <p>승인</p>
                </div>
            </div>

            {/* 거절/보류 사유 출력 (사유가 있을 경우에만 강조) */}
            {doctor.reason && (
                <div className={`status-reason ${doctor.status === "REJECTED" || doctor.status === "ON_HOLD" ? "active" : ""}`}>
                    <strong> {doctor.reason}</strong>
                </div>
            )}

            {/* 전문가 신청 정보 출력 */}
            <div className="doctor-info">
                <p><strong>병원명</strong></p>
                <p>{doctor.hospital}</p>
                <p><strong>전문분야</strong></p>
                <p>{doctor.subject}</p>
                <p><strong>신청자</strong></p>
                <p>{doctor.member.name}</p>
                <p><strong>연락처</strong></p>
                <p>{formatPhoneNumber(doctor.member.phone)}</p>
                <p><strong>신청일</strong></p>
                <p>{doctor.regTime.slice(0, 10)}</p>
            </div>

            {/* 수정 페이지로 이동하는 버튼 */}
            <div className="button-container">
                <button onClick={() => navigate(`/doctors/edit/${user.id}`)}>수정하기</button>
            </div>
        </div>
    );
}
