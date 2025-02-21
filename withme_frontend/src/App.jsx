import React, { useEffect, useState } from "react";
import { Routes, Route, Navigate, Outlet, useLocation } from "react-router-dom"; // 중복된 import 합침
import { useSelector, useDispatch } from "react-redux";
import { PersistGate } from "redux-persist/integration/react";
import { persistor } from "./redux/store";
import { fetchUserInfo, clearUser } from "./redux/authSlice";
import { API_URL } from "./constant";
import { fetchWithAuth } from "./common/fetchWithAuth.js";
import { Helmet } from "react-helmet";
import "./App.css";
import { Snackbar, Alert, Badge } from "@mui/material";
import useWebSocket from "./hook/useWebSocket";

// ui
import UiComponents from "./component/elements/UiComponents";

//pet
import PetDetailsView from "./component/pet/PetDetailsView";
import PetRegister from "./component/pet/PetRegister";

// 공지사항
import NoticeList from "./component/notice/NoticeList";
import NoticeForm from "./component/notice/NoticeForm";

// 전문가 신청, 수정
import NoticeView from "./component/notice/NoticeView";

// 전문가 가입, 신청, 수정
import SignupDoctor from "./component/member/SignupDoctor"; // 수의사 회원가입 페이지
import RegisterDoctor from "./component/doctor/RegisterDoctor";
import DoctorApplicationStatus from "./component/doctor/DoctorApplicationStatus";
import DoctorApplicationEdit from "./component/doctor/DoctorApplicationEdit";
import DoctorDashboard from "./component/doctor/DoctorDashboard";
import DoctorMessageList from "./component/doctor/DoctorMessageList";

// 커뮤니티
import PostList from "./component/posts/PostList";
import PostForm from "./component/posts/PostForm";
import PostView from "./component/posts/PostView";

// 관리자
import Admin from "./component/admin/Admin";
import DoctorUpdate from "./component/admin/DoctorUpdate";
import Dashboard from "./component/admin/Dashboard";

// ✅ 회원 관련
import Login from "./component/Login";
import MyPage from "./component/member/MyPage.jsx";
import MyPageProfileEdit from "./component/member/MyPageProfileEdit";
import MyPagePasswordEdit from "./component/member/MyPagePasswordEdit";
import Policy from "./component/member/Policy"; // 약관정책
import RegisterMember from "./component/member/RegisterMember"; // 일반 회원가입 페이지
import SignupSuccess from "./component/member/SignupSuccess"; // 가입 완료

// ✅ 의사 관련
import DoctorSignupSuccess from "./component/member/DoctorSignupSuccess"; // 수의사 가입 완료

// ✅ 기타 페이지
import Home from "./component/Home";
import UnauthorizedPage from "./component/UnAuthorizedPage.jsx";
import Header from "./component/common/Header";
import Footer from "./component/common/Footer";
import NavBar from "./component/common/NavBar";

// ✅ 추가: 문진(survey) 관련 컴포넌트 import
import FreeSurvey from "./component/survey/FreeSurvey";
import FreeSurveyResult from "./component/survey/FreeSurveyResult";
import PaidSurvey from "./component/survey/PaidSurvey";
import PaidSurveyResult from "./component/survey/PaidSurveyResult";
import PaidSurveySelection from "./component/survey/PaidSurveySelection";
import SurveyMain from "./component/survey/SurveyMain";

// 권한 기반 라우팅을 위한 ProtectedRoute 컴포넌트
const ProtectedRoute = ({ isAllowed, redirectPath = '/unauthorized', children }) => {
  if (!isAllowed) {
    return <Navigate to={redirectPath} replace />;
  }
  return children ? children : <Outlet />;
};

// 쇼핑몰
import ItemList from "./component/shop/Product/ItemList";
import ItemView from "./component/shop/Product/ItemView";
import ItemAdd from "./component/shop/Product/ItemAdd";
import ItemEdit from "./component/shop/Product/ItemEdit";
import CartList from "./component/shop/Cart/CartList";
import Order from "./component/shop/Order/OrderDetail";
import PayResult from "./component/shop/Order/PayResult";
import SubscriptionPage from "./component/shop/Product/SubscriptionPage";
import SubscriptionPayment from "./component/shop/Order/SubscriptionPayment";

//검색
import SearchResults from "./component/SearchResults";

function App() {
  // 리덕스 스토어의 상태를 가져오기 위해 useSelector 훅 사용, auth 슬라이스에서 user, isLoggedIn 상태를 가져옴
  // user: 사용자 정보 객체, isLoggedIn: 로그인 여부
  const { user, isLoggedIn } = useSelector((state) => state.auth);
  const { open: snackbarOpen, message: snackbarMessage } = useSelector((state) => state.snackbar);
  const { unreadCount } = useSelector((state) => state.messages);
  const dispatch = useDispatch();
  const [notification, setNotification] = useState(null);
  const location = useLocation();

  useEffect(() => {
    if (!user && isLoggedIn) {
      dispatch(fetchUserInfo());
    }
  }, [user, isLoggedIn, dispatch]);

  // 📡 WebSocket 연결 (useWebSocket Hook 사용)
  //useWebSocket(user);

  // 알림 닫기
  const handleCloseNotification = () => setNotification(null);
  const handleLogout = async () => {
    try {
      await fetchWithAuth(`${API_URL}auth/logout`, {
        method: "POST"
      });
      dispatch(clearUser());
      await persistor.purge(); // Redux Persist 데이터 초기화
      window.location.href = "/";
    } catch (error) {
      console.error("로그아웃 실패:", error.message);
      alert("로그아웃 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className="App">
      <Helmet>
        <title>행복한 반려생활의 시작, 위드미</title>
        <link
          href="https://fonts.googleapis.com/css2?family=Nanum+Gothic&display=swap"
          rel="stylesheet"
        />
        <link rel="icon" href="/assets/images/favicon.ico" />
      </Helmet>

{location.pathname.startsWith("/admin") ? null : <Header unreadCount={unreadCount} />}

      {/* 🔔 WebSocket 알림 표시 */}
      <Snackbar
        open={!!notification || snackbarOpen}
        autoHideDuration={5000}
        onClose={handleCloseNotification}
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
      >
        <Alert
          onClose={handleCloseNotification}
          severity="info"
          sx={{
            width: '100%',
            '& .MuiAlert-message': {
              fontSize: '0.9rem',
              fontWeight: 500
            }
          }}
        >
          {notification?.message || snackbarMessage}
        </Alert>
      </Snackbar>

      {/* Home을 제외한 모든 페이지에 NavBar 노출하도록 설정 */}
        {!(location.pathname === "/" || location.pathname === "/item/list" || location.pathname.startsWith("/admin")) && <NavBar />}

      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/registerDoctor" element={<RegisterDoctor />} />
        <Route path="/login" element={<Login />} />
        <Route path="/mypage/:id" element={<MyPage />} />
        {/* 마이프로필 */}
        <Route path="/mypage/profile-edit" element={<MyPageProfileEdit />} />
        <Route path="/mypage/password-edit" element={<MyPagePasswordEdit />} />
        {/* 공지사항 목록 */}
        <Route path="/notices" element={<NoticeList />} />
        <Route path="/notices/new" element={<NoticeForm />} />
        <Route path="/notices/:id/edit" element={<NoticeForm mode="edit" />} />
        {/* 커뮤니티 목록 */}
        <Route path="/posts" element={<PostList />} />
        <Route path="/posts/:id" element={<PostView />} />
        <Route path="/posts/new" element={<PostForm />} />
        <Route path="/posts/edit/:id" element={<PostForm />} />
        <Route path="/posts/:id" element={<PostView />} />

        {/* 관리자 */}
        {/* <Route
          path="/admin"
          element={
            <ProtectedRoute isAllowed={!!user && user.roles.includes('ROLE_ADMIN')}>
              <Admin />
            </ProtectedRoute>
          }
        /> */}

        {/* 회원가입 */}
        <Route path="/posts/:id/edit" element={<PostForm />} />

        {/* ✅ 관리자 페이지 */}
        <Route path="/admin" element={<Admin user={user} />} />
        <Route path="/admin/dashboard" element={<Dashboard user={user} />} />
        <Route path="/doctor/status" element={<DoctorUpdate />} />
        <Route path="/survey-main" element={<SurveyMain />} />

        {/* 회원가입 페이지 */}
        <Route path="/policy" element={<Policy />} />
        <Route path="/registerMember" element={<RegisterMember />} />
        <Route path="/signupSuccess" element={<SignupSuccess />} />
        <Route path="/doctorSignupSuccess" element={<DoctorSignupSuccess />} />

        {/* 펫 페이지 */}
        <Route path="/mypage/pet/:petId" element={<PetDetailsView />} />
        <Route path="/mypage/pet/register" element={<PetRegister />} />

        {/* 문진(survey) */}
        <Route path="/survey/free" element={<FreeSurvey />} />
        <Route path="/survey/free/result" element={<FreeSurveyResult />} />
        <Route path="/survey/paid" element={<PaidSurvey />} />
        <Route path="/survey/paid/selection" element={<PaidSurveySelection />} />

        {/* 검색 */}
        <Route path="/item/search" element={<SearchResults />} />
        <Route path="/survey/paid/result" element={<PaidSurveyResult />} />


        {/* 전문의 관련 */}
        <Route
          element={
            <ProtectedRoute isAllowed={!!user && user.roles.includes('ROLE_DOCTOR')} />
          }
        >
          <Route path="/doctor/dashboard" element={<DoctorDashboard />} />
          <Route path="/doctor-messages" element={<DoctorMessageList />} />

        </Route>

        {/* 기타 */}
        {/* 쇼핑몰 */}
        <Route path="/item/list" element={<ItemList />} />
        <Route path="/item/view/:itemId" element={<ItemView user={user} />} />
        <Route path="/item/add" element={<ItemAdd user={user} />} />
        <Route path="/item/edit/:itemId" element={<ItemEdit />} />
        <Route path="/cart/list" element={<CartList />} />
        <Route path="/orders/:orderId" element={<Order />} />
        <Route path="/payResult/:orderId" element={<PayResult />} />{" "}

        {/* ✅ 결제 결과 페이지 라우트 추가 */}
        <Route path="/subscription" element={<SubscriptionPage />} />{" "}

        {/* 구독 결제 확인 페이지 */}

        {/* 수의사 */}
        <Route path="/signupDoctor" element={<SignupDoctor />} />
        <Route path="/doctor/register" element={<RegisterDoctor user={user} />} />
        <Route path="/doctors/status/:id" element={<DoctorApplicationStatus user={user} />} />
        <Route path="/doctors/edit/:id" element={<DoctorApplicationEdit user={user} />} />

        {/* ✅ 기타 페이지 */}
        <Route path="/unauthorized" element={<UnauthorizedPage />} />
        <Route path="/ui" element={<UiComponents />} />
      </Routes>

      {location.pathname.startsWith("/admin") ? null : <Footer />}
    </div>
  );
}

export default App;
