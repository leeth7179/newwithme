import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

export default function OAuth2RedirectHandler() {
    const navigate = useNavigate();

    useEffect(() => {
        // 1️⃣ URL에서 토큰 추출
        const params = new URLSearchParams(window.location.search);
        const token = params.get("token");

        if (token) {
            // 2️⃣ 토큰을 localStorage에 저장
            localStorage.setItem("accessToken", token);
            navigate("/"); // 메인 페이지로 이동
        } else {
            console.error("OAuth2 로그인 실패");
            navigate("/login");
        }
    }, [navigate]);

    return <div>로그인 중...</div>;
}
