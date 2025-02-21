import DoctorUpdate from './DoctorUpdate';
import DoctorList from './DoctorList';
import UserList from './UserList';
import ItemAdd from '../shop/Product/ItemAdd';
import React, { useState, useEffect } from 'react';
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import Dashboard from './Dashboard';
import ItemList from './ItemList';
import NoticeListAdmin from './NoticeListAdmin';
import NoticeAdd from './NoticeAdd';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCircleUser, faChevronDown, faHouse, faUser, faPaw, faTag, faPen } from "@fortawesome/free-solid-svg-icons";

import '../../assets/css/admin/Admin.css';

export default function Admin({ user }) {
  const { user: loggedInUser } = useSelector((state) => state.auth);
  const navigate = useNavigate();

  // 드롭다운 상태 관리
  const [showDoctorMenu, setShowDoctorMenu] = useState(false);
  const [showCustomerMenu, setShowCustomerMenu] = useState(false);
  const [showShopMenu, setShowShopMenu] = useState(false);
  const [showNoticeMenu, setShowNoticeMenu] = useState(false);

  // 현재 보여줄 페이지 상태 관리
  const [currentPage, setCurrentPage] = useState(<Dashboard />);

  // 메뉴 항목 활성화 상태 관리
  const [activeMenu, setActiveMenu] = useState('home');  // 'home', 'doctor', 'customer', 'shop', 'notice'

  // 클릭 시 드롭다운 닫히게 하는 효과
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (!e.target.closest('.menu-header')) {
        setShowDoctorMenu(false);
        setShowCustomerMenu(false);
        setShowShopMenu(false);
        setShowNoticeMenu(false);
      }
    };

    document.addEventListener('click', handleClickOutside);
    return () => document.removeEventListener('click', handleClickOutside);
  }, []);

  return (
    <div className="admin-container">
      {/* 사이드 메뉴 */}
      <div className="side-menu">
        <div className="admin-profile">
          <FontAwesomeIcon icon={faCircleUser} className="profile-icon" />
          <p style={{ fontWeight: "bold", fontSize: "1.1em" }}>관리자</p>
          <p>{loggedInUser.email}</p>
        </div>
        <ul style={{ marginLeft: "0" }}>
          {/* 홈 카테고리 */}
          <div className="category">
            <p
              className="menu-header home-header"
              onClick={() => {
                setCurrentPage(<Dashboard user={user} />);
                setActiveMenu('home');
                setShowDoctorMenu(false);
                setShowCustomerMenu(false);
                setShowShopMenu(false);
                setShowNoticeMenu(false);
              }}
            >
              <FontAwesomeIcon icon={faHouse} className="left-icon" /> 홈
              <FontAwesomeIcon icon={faChevronDown} className="right-icon" style={{ color: "#353535" }} />
            </p>
          </div>

          {/* 전문가 관리 카테고리 */}
          <div className="category">
            <p
              className={`menu-header ${activeMenu === 'doctor' ? 'active' : ''}`}
              onClick={() => {
                setShowDoctorMenu(!showDoctorMenu);
                setShowCustomerMenu(false);
                setShowShopMenu(false);
                setShowNoticeMenu(false);
                setActiveMenu('doctor');
              }}
            >
              <FontAwesomeIcon icon={faPaw} className="left-icon" /> 수의사 관리
              <FontAwesomeIcon icon={faChevronDown} className="right-icon" />
            </p>
            <div className={`menu-items ${showDoctorMenu ? 'show' : ''}`}>
              <ul>
                <li className="menu-item" onClick={() => setCurrentPage(<DoctorList />)}>수의사 가입 현황</li>
                <li className="menu-item" onClick={() => setCurrentPage(<DoctorUpdate />)}>승인 대기 목록</li>
              </ul>
            </div>
          </div>

          {/* 고객 관리 카테고리 */}
          <div className="category">
            <p
              className={`menu-header ${activeMenu === 'customer' ? 'active' : ''}`}
              onClick={() => {
                setShowCustomerMenu(!showCustomerMenu);
                setShowDoctorMenu(false);
                setShowShopMenu(false);
                setShowNoticeMenu(false);
                setActiveMenu('customer');
              }}
            >
              <FontAwesomeIcon icon={faUser} className="left-icon" /> 고객 관리
              <FontAwesomeIcon icon={faChevronDown} className="right-icon" />
            </p>
            <div className={`menu-items ${showCustomerMenu ? 'show' : ''}`}>
              <ul>
                <li className="menu-item" onClick={() => setCurrentPage(<UserList />)}>고객 가입 현황</li>
              </ul>
            </div>
          </div>

          {/* 쇼핑몰 카테고리 */}
          <div className="category">
            <p
              className={`menu-header ${activeMenu === 'shop' ? 'active' : ''}`}
              onClick={() => {
                setShowShopMenu(!showShopMenu);
                setShowDoctorMenu(false);
                setShowCustomerMenu(false);
                setShowNoticeMenu(false);
                setActiveMenu('shop');
              }}
            >
              <FontAwesomeIcon icon={faTag} className="left-icon" /> 쇼핑몰
              <FontAwesomeIcon icon={faChevronDown} className="right-icon" />
            </p>
            <div className={`menu-items ${showShopMenu ? 'show' : ''}`}>
              <ul>
                <li className="menu-item" onClick={() => setCurrentPage(<ItemAdd />)}>상품 등록</li>
                <li className="menu-item" onClick={() => setCurrentPage(<ItemList />)}>상품 목록</li>
                <li className="menu-item">주문 관리</li>
              </ul>
            </div>
          </div>

          {/* 공지사항 카테고리 */}
          <div className="category">
            <p
              className={`menu-header ${activeMenu === 'notice' ? 'active' : ''}`}
              onClick={() => {
                setShowNoticeMenu(!showNoticeMenu);
                setShowDoctorMenu(false);
                setShowCustomerMenu(false);
                setShowShopMenu(false);
                setActiveMenu('notice');
              }}
            >
              <FontAwesomeIcon icon={faPen} className="left-icon" /> 공지사항
              <FontAwesomeIcon icon={faChevronDown} className="right-icon" />
            </p>
            <div className={`menu-items ${showNoticeMenu ? 'show' : ''}`}>
              <ul>
                <li className="menu-item" onClick={() => setCurrentPage(<NoticeAdd />)}>새로 등록하기</li>
                <li className="menu-item" onClick={() => setCurrentPage(<NoticeList />)}>목록 보기</li>
              </ul>
            </div>
          </div>
        </ul>
        <button onClick={() => navigate(`/`)} className="exit-btn">나가기</button>
      </div>

      {/* 메인 콘텐츠 */}
      <div className="main-content">
        {currentPage}
      </div>
    </div>
  );
}
