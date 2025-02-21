import React, { useState, useEffect } from 'react';
import { API_URL } from '../../../constant';
import { fetchWithAuth } from '../../../common/fetchWithAuth';
import Payment from '../Order/SubscriptionPayment'; // 구독 결제 컴포넌트
import '../../../assets/css/shop/SubscriptionPage.css';

export default function SubscriptionPage() {
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [subscriptionItem, setSubscriptionItem] = useState(null); // 구독 상품 정보
    const [merchantId, setMerchantId] = useState(""); // 가맹점 UID 상태
    const [orderId, setOrderId] = useState(null); // 주문 ID 상태
    const [isOrdering, setIsOrdering] = useState(false); // 주문 후 결제 UI 전환 상태

    // 구독 상품 정보를 하드코딩하여 로딩
    useEffect(() => {
        const fetchSubscriptionItem = async () => {
            try {
                const itemId = 1; // 하드코딩된 구독 상품 ID
                const response = await fetchWithAuth(`${API_URL}item/view/${itemId}`);
                if (!response.ok) {
                    throw new Error('구독 상품 정보를 불러오지 못했습니다.');
                }
                const data = await response.json();
                console.log("구독 상품 정보 : ", data);
                setSubscriptionItem(data);
            } catch (error) {
                setError(error.message);
            } finally {
                setLoading(false);
            }
        };

        // 가맹점 UID 가져오기
        const fetchMerchantId = () => {
            const id = import.meta.env.VITE_PORTONE_MERCHANT_ID;
            setMerchantId(id);
        };

        fetchSubscriptionItem();
        fetchMerchantId(); // 최초 1회 실행
    }, []);

    // 구독 상품 주문하기
    const handleOrder = async () => {
        if (!subscriptionItem) {
            alert('구독 상품 정보가 없습니다.');
            return;
        }

        const orderData = {
            itemId: subscriptionItem.id,
            count: 1,
        };

        try {
            const response = await fetchWithAuth(`${API_URL}cart/subscriptions/orders`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(orderData),
            });

            if (!response.ok) {
                throw new Error('주문 실패');
            }

            // 서버에서 응답받은 데이터(주문 ID)를 저장
            const orderId = await response.json();
            setOrderId(orderId);
            setIsOrdering(true); // 주문 후 결제 화면으로 전환
        } catch (error) {
            alert(error.message);
        }
    };

    if (loading) return <div>로딩 중...</div>;
    if (error) return <div>{error}</div>;
    if (!subscriptionItem) return <div>상품 정보가 없습니다.</div>;

    return (
        <div className="subscription-container">
            <h2>유료 회원 상품</h2>
            <div className="subscription-item">
                <div className="subscription-item-info">
                    <div className="subscription-info">
                        <p>
                            🎉 <strong>유료 회원 혜택 안내</strong> 🎉
                        </p>
                        <ul>
                            <li>✅ 반려견의 건강 상태를 더 잘 파악할 수 있는 <strong>상세 문진 서비스</strong> 제공</li>
                            <li>✅ <strong>맞춤형 사료 추천</strong>으로 반려견의 건강을 지킬 수 있는 최적의 식사 제안</li>
                            <li>✅ 반려견의 특성에 맞춘 <strong>맞춤 훈련 프로그램</strong> 제공</li>
                            <li>✅ 전문가의 온라인 상담을 통해 <strong>건강 관리 및 문제 해결</strong> 지원</li>
                            <li>✅ 반려견의 활동량과 건강 상태를 <strong>실시간으로 체크</strong>할 수 있는 앱 연동 기능</li>
                            <li>✅ 회원 전용 <strong>할인 혜택</strong>으로 다양한 반려견 관련 제품 구매 시 특가 제공</li>
                            <li>✅ <strong>주기적인 건강 체크 서비스</strong>와 전문 의료진의 분석 보고서 제공</li>
                            <li>✅ 연 1회의 <strong>전문가와의 대면 상담</strong>을 통해 반려견의 건강 상태에 대한 깊이 있는 조언</li>
                            <li>✅ 반려견을 위한 다양한 <strong>프리미엄 상품 및 서비스</strong>에 우선 접근 가능</li>
                            <li>✅ 유료 회원 전용 <strong>커뮤니티</strong>에서 다른 반려견 주인들과 정보 공유 및 소통</li>
                        </ul>
                    </div>
                    <p>가격: {subscriptionItem.price.toLocaleString()}원</p>
                    <div className="subscription-info">
                        {/* 주문이 완료된 경우, 결제 컴포넌트를 아래에 배치 */}
                        {isOrdering && orderId ? (
                            <div className="payment-container">
                                <Payment
                                    merchantId={merchantId}
                                    subscriptionItem={subscriptionItem}
                                    orderId={orderId}
                                />
                            </div>
                        ) : (
                            <button onClick={handleOrder}>확인했습니다.</button>
                        )}
                    </div>


                </div>
            </div>


        </div>
    );
}
