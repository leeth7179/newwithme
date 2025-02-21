import React, { useState, useEffect } from "react";
import { fetchWithAuth } from "../../common/fetchWithAuth"; // 인증된 fetch
import { API_URL } from "../../constant"; // API 기본 URL
import { useNavigate } from "react-router-dom"; // 리디렉션을 위해 사용

/**
 * 전문가 신청 정보 수정 페이지
 */
export default function DoctorApplicationEdit({ user }) {
    const [doctorData, setDoctorData] = useState({
        subject: "",
        hospital: "",
        doctorNumber: "",
    });
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate(); // useNavigate 사용

    useEffect(() => {
        const fetchDoctorData = async () => {
            try {
                const response = await fetchWithAuth(`${API_URL}doctors/application/${user.id}`, {
                    method: "GET",
                });

                if (response.ok) {
                    const data = await response.json();
                    setDoctorData(data);
                } else {
                    setError("의사 신청 정보를 불러올 수 없습니다.");
                }
            } catch (error) {
               navigate("/unauthorized");
            }
        };

        fetchDoctorData();
    }, [user.id]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setDoctorData((prevData) => ({
            ...prevData,
            [name]: value,
        }));
    };

console.log("doctorData : ", doctorData);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            const response = await fetchWithAuth(`${API_URL}doctors/application/${user.id}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(doctorData),
            });

            if (response.ok) {
                alert("신청 정보가 수정되었습니다.");
                navigate("/doctors/status/" + user.id);
            } else {
                setError("수정에 실패했습니다.");
            }
        } catch (error) {
            setError("수정에 실패했습니다.");
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async () => {
        if (window.confirm("정말로 신청 정보를 삭제하시겠습니까?")) {
            setLoading(true);
            try {
                const response = await fetchWithAuth(`${API_URL}doctors/application/${user.id}`, {
                    method: "DELETE",
                });

                if (response.ok) {
                    alert("신청 정보가 삭제되었습니다.");
                    navigate("/"); // 홈으로 리디렉션
                } else {
                    setError("삭제에 실패했습니다.");
                }
            } catch (error) {
                setError("삭제에 실패했습니다.");
            } finally {
                setLoading(false);
            }
        }
    };

    return (
        <div>
            <h2>의사 신청 수정</h2>
            {error && <p style={{ color: "red" }}>{error}</p>}
            <form onSubmit={handleSubmit}>
                <div>
                    <label htmlFor="subject">전문 분야</label>
                    <input
                        type="text"
                        id="subject"
                        name="subject"
                        value={doctorData.subject}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div>
                    <label htmlFor="hospital">병원명</label>
                    <input
                        type="text"
                        id="hospital"
                        name="hospital"
                        value={doctorData.hospital}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div>
                    <label htmlFor="doctorNumber">의사 번호</label>
                    <input
                        type="text"
                        id="doctorNumber"
                        name="doctorNumber"
                        value={doctorData.doctorNumber}
                        onChange={handleChange}
                        required
                    />
                </div>
                <button type="submit" disabled={loading}>
                    {loading ? "수정 중..." : "수정"}
                </button>
            </form>

            {/* 삭제 버튼 추가 */}
            <button onClick={handleDelete} disabled={loading}>
                {loading ? "삭제 중..." : "삭제"}
            </button>
        </div>
    );
}
