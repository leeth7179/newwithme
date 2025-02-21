import { Button, TextField, Typography } from "@mui/material";
import { useState, useEffect } from "react";
import { API_URL } from "../../constant";
import { fetchWithAuth } from "../../common/fetchWithAuth"; // 인증된 API 호출 함수
import '../../assets/css/admin/RegisterDoctor.css';
import { useNavigate } from "react-router-dom";

export default function DoctorApplicationForm({ user }) {
    // 사용자 정보 및 추가 입력 필드를 저장할 상태
    const [userData, setUserData] = useState({
        subject: "",
        hospital: "",
        doctorNumber: "",
    });

    const [error, setError] = useState(null); // 에러 메시지 초기값 null로 설정
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    // 입력 값 업데이트
    const handleChange = (e) => {
        const { name, value } = e.target;
        setUserData((prevData) => ({
            ...prevData,
            [name]: value,
        }));
    };

    // 전문가 신청 제출 (사용자 정보 + 추가 입력 데이터)
    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!userData.subject || !userData.hospital || !userData.doctorNumber) {
            setError("모든 필드를 입력해야 합니다.");
            return;
        }

        setLoading(true);

        try {
            const response = await fetchWithAuth(`${API_URL}doctors/apply/${user.email}`, {
                method: "POST",
                body: JSON.stringify(userData),

            });

            if (response.ok) {
                alert("전문가 신청이 완료되었습니다.");
                navigate(`/doctors/status/${user.id}`); // 신청 후 상태 페이지로 리다이렉트
            } else {
                const errorMessage = await response.text();
                setError(errorMessage || "신청에 실패했습니다.");
            }
        } catch (error) {
            console.error("신청 실패:", error);
            setError("신청에 실패했습니다. 다시 시도해주세요.");
        } finally {
            setLoading(false);
        }
    };

    // 에러가 발생하면 얼럿을 띄우고, 신청 정보 조회 페이지로 리다이렉트
    useEffect(() => {
        if (error) {
            alert("신청 정보가 있습니다.");
            navigate(`/doctors/status/${user.id}`); // 신청 정보 조회 페이지로 리다이렉트
        }
    }, [error, history]);

    return (
        <div className="application-form-container">
            <Typography variant="h4" gutterBottom>
                전문가 신청
            </Typography>

            <form onSubmit={handleSubmit}>
                {/* 추가 입력 필드 (전문가 정보) */}
                <div className="form-group">
                    <TextField
                        label="전문분야"
                        name="subject"
                        value={userData.subject}
                        onChange={handleChange}
                        fullWidth
                        required
                    />
                </div>
                <div className="form-group">
                    <TextField
                        label="병원주소"
                        name="hospital"
                        value={userData.hospital}
                        onChange={handleChange}
                        fullWidth
                        required
                    />
                </div>
                <div className="form-group">
                    <TextField
                        label="면허 번호"
                        name="doctorNumber"
                        value={userData.doctorNumber}
                        onChange={handleChange}
                        fullWidth
                        required
                    />
                </div>

                <Button
                    variant="contained"
                    color="primary"
                    type="submit"
                    fullWidth
                    disabled={loading}
                >
                    {loading ? "신청 중..." : "전문가 신청"}
                </Button>
            </form>
        </div>
    );
}
