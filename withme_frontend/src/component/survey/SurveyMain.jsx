import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import Header from "../common/Header"; // ✅ 공통 헤더 추가
import Footer from "../common/Footer"; // ✅ 공통 푸터 추가
import "./SurveyMain.css";

function SurveyMain() {
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const timer = setTimeout(() => {
      setLoading(false);
    }, 1000);

    return () => clearTimeout(timer);
  }, []);

  return (
    <>
      <Header /> {/* ✅ 공통 헤더 추가 */}

      <div className="survey-main">
        {loading ? (
          <div>문진 검사 페이지로 이동 중...</div>
        ) : (
          <>
            <h2>문진을 진행할 회원 유형을 선택하세요</h2>
            <div className="survey-options">
              <Link to="/survey/free" className="survey-btn free">무료 문진</Link>
              <Link to="/survey/paid" className="survey-btn paid">유료 문진</Link>
            </div>
          </>
        )}
      </div>

      <Footer /> {/* ✅ 공통 푸터 추가 */}
    </>
  );
}

export default SurveyMain;
