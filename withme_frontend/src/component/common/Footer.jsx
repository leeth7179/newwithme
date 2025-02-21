import React from "react";
import "../../assets/css/common/Footer.css";

const Footer = () => {
  return (
    <footer className="footer">
      <div className="footer-container">
        <div className="footer-top">
          <img
            src="/assets/images/logo.png"
            alt="로고"
            className="footer-logo"
          />
          <p>
            (주)위드미 : 경기도 안산시 상록구 광덕1로 375, 5층
            <br /> 대표 : 홍길동 사업자등록번호 : 123-45-67890
          </p>
        </div>

        <div className="footer-middle">
          <div className="cs-center-container">
            <div className="cs-center">
              <h2>CS CENTER</h2>
              <h2 className="cs-number">1588-0123</h2>
            </div>
            <div className="cs-info">
              <p>평일 09:30 - 18:00</p>
              <p>점심시간 13:20 - 14:10</p>
              <p>토/일 공휴일 휴무</p>
            </div>
          </div>
        </div>

        <div className="footer-bottom">
          <p>
            COPYRIGHT &copy; <span className="footer-bold">WITHME</span> CO.,
            LTD. ALL RIGHTS RESERVED.
          </p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
