import React from "react";
import { useNavigate } from "react-router-dom"; // 페이지 이동을 위해 사용

const MainPage = () => {
  const navigate = useNavigate(); // 페이지 이동을 위한 hook

  const handleSurveyStart = (userType) => {
    if (userType === "paid") {
      // 유료회원 문진 시작 페이지로 이동
      navigate("/paid-survey");
    } else if (userType === "free") {
      // 무료회원 문진 페이지로 이동
      navigate("/free-survey");
    } else if (userType === "expert") {
      // 전문가 페이지로 이동
      navigate("/expert-dashboard");
    }
  };

  return (
    <div className="main-page">
      <h1>문진 검사 시작</h1>
      <div className="button-container">
        <button onClick={() => handleSurveyStart("paid")}>유료회원용 문진</button>
        <button onClick={() => handleSurveyStart("free")}>무료회원용 문진</button>
        <button onClick={() => handleSurveyStart("expert")}>전문가용 대시보드</button>
      </div>
    </div>
  );
};

export default MainPage;
