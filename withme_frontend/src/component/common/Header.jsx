import "../../assets/css/common/Header.css";
import React, { useState, useEffect, useRef } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useSelector, useDispatch } from "react-redux";
import { clearUser } from "../../redux/authSlice";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faShoppingBasket, faCaretDown } from "@fortawesome/free-solid-svg-icons";
import { API_URL } from "../../constant";
import { fetchWithAuth } from "../../common/fetchWithAuth";

const Header = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { user, isLoggedIn } = useSelector((state) => state.auth);
  const [isAdminOpen, setIsAdminOpen] = useState(false);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (adminDropdownRef.current && !adminDropdownRef.current.contains(event.target)) {
        setIsAdminOpen(false); // 드롭다운 외부 클릭 시 닫기
      }
    };

    document.addEventListener("mousedown", handleClickOutside); // 클릭 이벤트 추가
    return () => {
      document.removeEventListener("mousedown", handleClickOutside); // 클린업
    };
  }, []);

  const handleLogout = async () => {
    try {
      await fetchWithAuth(`${API_URL}auth/logout`, { method: "POST" });
      dispatch(clearUser());
      window.location.href = "/";
    } catch (error) {
      console.error("로그아웃 실패:", error.message);
      alert("로그아웃 중 오류가 발생했습니다.");
    }
  };

  const handleMypageClick = () => {
    if (!isLoggedIn) {
      alert("로그인 후 이용 가능합니다.");
      navigate("/login");
    } else {
      navigate(`/mypage/${user.id}`);
    }
  };

  const handleCartClick = () => {
    if (!isLoggedIn) {
      alert("로그인 후 이용 가능합니다.");
      navigate("/login");
    }
  };

  const adminDropdownRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (adminDropdownRef.current && !adminDropdownRef.current.contains(event.target)) {
        setIsAdminOpen(false); // 드롭다운 외부 클릭 시 닫기
      }
    };

    document.addEventListener("mousedown", handleClickOutside); // 클릭 이벤트 추가
    return () => {
      document.removeEventListener("mousedown", handleClickOutside); // 클린업
    };
  }, []);

  return (
    <header>
      <div className="gnb-container">
        <ul className="gnb" style={{ fontWeight: "bold" }}>
          <Link to="/">
            <img src="/assets/images/text_logo.png" alt="텍스트 로고" className="textLogo" />
          </Link>
          {isLoggedIn ? (
            <>
              <li style={{ color: "#333" }}>{user.name}님</li>
              {user.roles.includes("ROLE_ADMIN") && (
                <li>
                  <Link to="/admin">관리자</Link>
                </li>
              )}
              <li>
                <Link to="/" onClick={handleLogout} className="logout-btn">
                  로그아웃
                </Link>
              </li>
              {user.roles.includes("DOCTOR") && (
                <li>
                  <Link to="/doctor-messages">상담내역</Link>
                </li>
              )}
              {!user.roles.includes("ADMIN") && (
                  <li>
                    <Link to={`/mypage/${user.id}`}>마이페이지</Link>
                  </li>
              )}
              {user.roles.includes("PENDING_DOCTOR") && (
                <li>
                  <button
                    className="admin-btn"
                    onClick={() => setIsAdminOpen(!isAdminOpen)}>
                    수의사 <FontAwesomeIcon icon={faCaretDown} />
                  </button>
                  {isAdminOpen && (
                    <ul className="admin-dropdown">
                      <li>
                        <Link to="/doctor/register">수의사 신청</Link>
                      </li>
                      <li>
                        <Link to={`/doctors/status/${user.id}`}>수의사 신청상태</Link>
                      </li>
                      <li>
                        <Link to={`/doctors/edit/${user.id}`}>수의사 신청서 수정</Link>
                      </li>
                    </ul>
                  )}
                </li>
              )}
            </>
          ) : (
            <li>
              <Link to="/login">로그인</Link>
            </li>
          )}

          {!isLoggedIn && (
            <li className="join-us">
              <Link to="/policy">회원가입</Link>
              <span className="tooltip">+2,000P</span>
            </li>
          )}

          <li>
            <Link to="/cart/list" className="cart-btn" onClick={handleCartClick}>
              <FontAwesomeIcon icon={faShoppingBasket} />
            </Link>
          </li>
        </ul>
      </div>
    </header>
  );
};

export default Header;
