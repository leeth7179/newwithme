import React, { useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import "../../assets/css/member/SignupSuccess.css";


function SignupSuccess() {
  // location 상태에서 사용자명 가져오기
  const location = useLocation();
  const { name } = location.state || {}; // 이전 페이지에서 state로 전달된 username

   useEffect(() => {
      document.body.style.backgroundColor = "#FEF9F6";
      return () => {
        document.body.style.backgroundColor = "";
      };
    }, []);

  return (
    <div className="container" style={{ backgroundColor: "#FEF9F6", marginBottom: "150px" }}>
      <img src="assets/images/dog.png" alt="dog image" />
      <div className="message">
        <p className="title">
          회원가입이 <span style={{ fontWeight: 'bold' }}>완료</span> 되었습니다.
        </p>
        <p>
          <span className="userName" style={{ fontWeight: 'bold' }}>
            {name}
          </span>
          님의 회원가입을 축하합니다.
        </p>
        <p className="last">
          위드미와 함께 더 건강하고 행복한 반려생활을 시작해보세요!
        </p>
      </div>
      <div className="btn-wrap">
        <button id="homeBtn" onClick={() => (window.location.href = '/')}>
          홈으로
        </button>
        <button
          id="loginBtn"
          onClick={() => (window.location.href = '/login')}
        >
          로그인
        </button>
      </div>
    </div>
  );
}

export default SignupSuccess;
