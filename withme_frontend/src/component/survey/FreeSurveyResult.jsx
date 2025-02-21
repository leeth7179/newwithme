import React from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { PieChart, Pie, Cell, Tooltip, Legend } from "recharts";
import img2 from "../../image/img2.png";

function FreeSurveyResultPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const { answers } = location.state || { answers: {} };

  const totalQuestions = 15;
  const maxScore = 75;
  const totalScore = Math.round((Object.values(answers).reduce((sum, answer) => sum + answer.score, 0) / (totalQuestions * 5)) * maxScore);

  const chartData = [
    { name: "문진 결과 점수", value: totalScore, color: "#E75480" },
    { name: "남은 건강 점수", value: maxScore - totalScore, color: "#FFB6C1" },
  ];

  const getMessage = (score) => {
    if (score <= 15) return "⚠️ 반려동물 건강이 위험할 수 있어요! 지금부터 건강 관리에 더 신경 써주세요.";
    if (score <= 45) return "🐾 반려동물의 건강을 위해 더 많은 관심과 노력이 필요해요!";
    if (score <= 60) return "🌟 훌륭하지만, 최상의 건강을 위해 더 노력해보세요!";
    return "🏆 완벽한 건강 관리 중! 당신의 반려동물은 최고로 케어 받고 있어요!";
  };

  return (
    <div style={{
      width: "100vw",
      minHeight: "100vh",
      padding: "30px",
      paddingTop: "100px", // 위쪽 여백 추가
      boxSizing: "border-box",
      display: "flex",
      flexDirection: "column",
      alignItems: "center",
      justifyContent: "flex-start", // 위에서부터 시작
      backgroundColor: "#FFFBF8",
      overflowY: "auto" // 내용이 넘칠 경우 스크롤 가능하도록 설정
    }}>
        <div style={{
          display: "flex",
          alignItems: "center",
          marginBottom: "30px"
        }}>
          <img src={img2} alt="문진 검사 결과" style={{ width: "10rem", height: "10rem", marginRight: "20px" }} />
          <h2 style={{
            fontSize: "2.5rem",
            fontWeight: "bold",
            color: "#333",
            textDecoration: "underline",
            textDecorationColor: "pink"
          }}>
            무료 문진 검사 결과
          </h2>
        </div>

      <p style={{
        fontSize: "1.6rem",
        fontWeight: "bold",
        marginBottom: "30px",
        padding: "15px 25px",
        borderRadius: "12px",
        backgroundColor: totalScore <= 30 ? "#FFEBEE" : totalScore <= 60 ? "#FFF8E1" : "#E8F5E9",
        color: totalScore <= 30 ? "#D32F2F" : totalScore <= 60 ? "#F57C00" : "#388E3C",
        textAlign: "center",
        maxWidth: "80%"
      }}>
        {getMessage(totalScore)}
      </p>

      <p style={{
        fontSize: "2rem",
        fontWeight: "bold",
        marginBottom: "20px",
        color: "#444"
      }}>
        총점: {totalScore}점 / {maxScore}점
      </p>

      <div style={{ marginBottom: "40px" }}>
        <PieChart width={600} height={600}>
          <Pie
            data={chartData}
            cx="50%"
            cy="50%"
            outerRadius={180}
            innerRadius={100}
            fill="#82ca9d"
            dataKey="value"
            label={({ name, value }) => `${name}: ${value}점`}
            animationDuration={1000}
          >
            {chartData.map((entry, index) => (
              <Cell key={`cell-${index}`} fill={entry.color} />
            ))}
          </Pie>
          <Tooltip />
          <Legend />
        </PieChart>
      </div>
    <div style={{
      display: "flex",
      justifyContent: "center",  // ✅ 버튼을 화면 중앙 정렬
      alignItems: "center",
      marginTop: "50px",
      width: "100%" // ✅ 부모 요소 크기에 맞춰 정렬
    }}>
      <button
        onClick={() => navigate("/login")}
        style={{
          display: "flex",   // ✅ 내부 텍스트 정렬을 위해 flex 사용
          alignItems: "center", // ✅ 텍스트 수직 정렬
          justifyContent: "center", // ✅ 텍스트 수평 정렬
          fontSize: "2rem",  // ✅ 글자 크기 증가
          fontWeight: "bold",
          color: "#fff",
          backgroundColor: "#FFC1CC",
          border: "none",
          borderRadius: "25px",
          cursor: "pointer",
          transition: "transform 0.3s ease-in-out",
          textAlign: "center",
          width: "420px",  // ✅ 버튼 너비 증가
          height: "130px", // ✅ 버튼 높이 증가 (텍스트가 정확히 중앙에 위치)
          position: "relative",
          whiteSpace: "nowrap", // ✅ 버튼 내부 텍스트가 한 줄 유지되도록 설정
          lineHeight: "normal", // ✅ 줄 간격 조정
        }}
        onMouseEnter={(e) => {
          e.target.style.transform = "scale(1.05)";
          const tooltip = e.target.querySelector(".tooltip");
          if (tooltip) tooltip.style.visibility = "visible";
        }}
        onMouseLeave={(e) => {
          e.target.style.transform = "scale(1.0)";
          const tooltip = e.target.querySelector(".tooltip");
          if (tooltip) tooltip.style.visibility = "hidden";
        }}
      >
        🐾 유료회원으로 전환
        <div className="tooltip" style={{
          position: "absolute",
          top: "-85px",  // ✅ 툴팁을 더 위로 조정
          left: "50%",    // ✅ 툴팁을 버튼 중앙 정렬
          transform: "translateX(-50%)",
          backgroundColor: "#FFD1DC",
          color: "#000",
          padding: "12px",
          borderRadius: "15px",
          fontSize: "1.6rem",
          visibility: "hidden",
          boxShadow: "0px 4px 6px rgba(0, 0, 0, 0.2)",
          transition: "opacity 0.3s",
          whiteSpace: "nowrap",
        }}>
          🐶 더 많은 혜택을 원하시면 유료로 전환하세요!
        </div>
      </button>
    </div>



    </div>
  );
}

export default FreeSurveyResultPage;
