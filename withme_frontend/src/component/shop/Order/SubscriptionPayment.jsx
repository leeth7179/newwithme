import React from "react";
import { Button } from "@mui/material";
import { API_URL } from '../../../constant';
import { fetchWithAuth } from '../../../common/fetchWithAuth';
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";

/**
 * 구독 전용 결제 컴포넌트
 * - `SubscriptionPage`에서 주문 후 바로 결제 진행
 * - 결제 성공 후 결과 페이지로 이동
 *
 * @param {string} merchantId - 가맹점 ID
 * @param {object} subscriptionItem - 구독 상품 정보
 * @param {string} orderId - 주문 ID
 * @returns {JSX.Element}
 */
const SubscriptionPayment = ({ merchantId, subscriptionItem, orderId }) => {
  const navigate = useNavigate();
  const { user } = useSelector((state) => state.auth);

  console.log("구독 상품:", subscriptionItem);
  console.log("주문 ID:", orderId);
  console.log("사용자 정보:", user?.email);

  if (!subscriptionItem || !orderId) {
    return <div>결제 정보를 불러오는 중...</div>;
  }

  const { itemNm, price } = subscriptionItem; // 상품명과 가격 추출

  // 회원 권한 변경 API 호출
  const updateUserRole = async (email, role) => {
    if (!email) {
      console.error("이메일이 없습니다. 권한 변경 요청을 취소합니다.");
      return;
    }

    const response = await fetchWithAuth(`${API_URL}members/role/${email}?role=${role}`, {
      method: "PATCH",
    });

    if (response.ok) {
      console.log(`✅ 회원(${email})의 권한이 ${role}로 변경되었습니다.`);
    } else {
      console.error(`❌ 회원 권한 변경 실패:`, await response.text());
    }

    return response;
  };

  // 백엔드에 결제 데이터 전송
  const processPayment = async (rsp) => {
    const paymentRequest = {
      impUid: rsp.imp_uid,
      merchantUid: orderId,
      paidAmount: rsp.paid_amount,
      name: rsp.name,
      pgProvider: rsp.pg_provider,
      buyerEmail: rsp.buyer_email,
      buyerName: rsp.buyer_name,
      buyTel: rsp.buyer_tel,
      buyerAddr: rsp.buyer_addr,
      buyerPostcode: rsp.buyer_postcode,
      paidAt: rsp.paid_at,
      status: "PAYMENT_COMPLETED",
    };

    return fetchWithAuth(`${API_URL}payments/request/${orderId}`, {
      method: "POST",
      body: JSON.stringify(paymentRequest),
    });
  };

  // 결제 요청 핸들러
  const handlePayment = async () => {
    const IMP = window.IMP; // 아임포트 결제 라이브러리 초기화
    IMP.init(merchantId);   // 가맹점 식별코드 설정

    const paymentData = {
      pg: "kakaopay",
      pay_method: "card",
      merchant_uid: orderId,  // 주문 ID를 거래 번호로 설정
      name: itemNm,
      amount: price,
      buyer_email: user?.email || "test@portone.io",
      buyer_name: user?.name || "홍길동",
      buyer_tel: "010-1234-5678",
      buyer_addr: "서울특별시 강남구 테헤란로",
      buyer_postcode: "06164",
      m_redirect_url: "https://localhost:3000/", // 결제 완료 후 리다이렉트할 URL
    };

    IMP.request_pay(paymentData, async (rsp) => {
      if (rsp.success) {
        alert("결제가 완료되었습니다!");
        console.log("✅ 결제 완료 응답:", rsp);

        // 1️⃣ 결제 데이터 백엔드로 전송
        const paymentResponse = await processPayment(rsp);
        if (!paymentResponse.ok) {
          console.error("❌ 결제 정보 저장 실패:", await paymentResponse.text());
          return;
        }
        const paymentData = await paymentResponse.json();

        // 2️⃣ 회원 권한 변경 (VIP로 업그레이드)
        const roleUpdateResponse = await updateUserRole(user?.email, "VIP");
        if (roleUpdateResponse?.ok) {
          console.log("✅ 회원 권한 변경 완료!");
        } else {
          console.error("❌ 회원 권한 변경 실패");
        }

        // 3️⃣ 결제 결과 페이지로 이동
        navigate(`/payResult/${orderId}`, { state: { paymentInfo: paymentData } });
      } else {
        alert(`❌ 결제 실패: ${rsp.error_msg}`);
      }
    });
  };

  return (
    <Button variant="contained" color="primary" size="large" onClick={handlePayment}>
      결제하기
    </Button>
  );
};

export default SubscriptionPayment;
