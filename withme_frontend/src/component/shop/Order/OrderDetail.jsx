import React, { useState, useEffect } from "react";
import { Button, TextField, Box, Typography, Paper } from "@mui/material";
import { API_URL, SERVER_URL2 } from '../../../constant';
import { fetchWithAuth } from '../../../common/fetchWithAuth';
import Payment from "./Payment"; // 결제 컴포넌트 추가
import { useParams, useLocation } from "react-router-dom";

/**
 * 주문 상세 페이지
 * - 특정 주문 ID에 대한 주문 아이템 목록을 조회하고 표시하는 컴포넌트
 */
const OrderDetail = () => {
  const { orderId } = useParams();
  const location = useLocation();

  const [orderItems, setOrderItems] = useState([]);
  const [merchantId, setMerchantId] = useState("");
  const [zipCode, setZipCode] = useState("");
    const [address1, setAddress1] = useState("");
const [address2, setAddress2] = useState("");
const [orderData, setOrderData] = useState(location.state?.orderData || {});

  useEffect(() => {
      fetchOrderItems(orderId);
      const id = fetchMerchantId();
      setMerchantId(id);
    }, [orderId]);

  // 주문 아이템 목록 조회
  const fetchOrderItems = async (orderId) => {
    try {
      const response = await fetchWithAuth(`${API_URL}orders/view/${orderId}`);
      if (response.ok) {
        const data = await response.json();
        setOrderItems(data);
        console.log("주문데이터: " , data);
      } else {
        console.error("주문 아이템 정보 조회 실패:", response.status);
      }
    } catch (error) {
      console.error("주문 아이템 정보를 불러오는 중 오류 발생:", error);
    }
  };

  // 가맹점 UID 가져오기
  const fetchMerchantId = () => {
    const merchantId = import.meta.env.VITE_PORTONE_MERCHANT_ID;
    console.log("가맹점 UID:", merchantId);
    return merchantId;
  };

  return (
    <Box sx={{ maxWidth: 800, margin: "auto", padding: 3 }}>
      <Typography variant="h4" gutterBottom>
        주문 상세 정보
      </Typography>

      {orderItems.length > 0 ? (
        <Paper sx={{ padding: 3 }}>
          {orderItems.map((item) => (
            <Box key={item.orderItemId} display="flex" alignItems="center" mb={2}>
              <img
                src={item.imgUrl.startsWith('/assets') ? item.imgUrl : SERVER_URL2 + item.imgUrl}
                alt={item.itemNm}
                style={{ width: 100, height: 100, marginRight: 20 }}
              />
              <Box>
                <Typography variant="h6">{item.itemNm}</Typography>
                <Typography variant="body1">가격: {item.orderPrice}원</Typography>
                <Typography variant="body1">수량: {item.count}</Typography>
              </Box>
            </Box>
          ))}
          {/* 배송 정보 입력 */}
                    <Typography variant="h6" mt={3} gutterBottom>
                      배송 정보
                    </Typography>
                    <TextField label="우편번호" value={zipCode} onChange={(e) => setZipCode(e.target.value)} fullWidth margin="normal" />
                    <TextField label="주소 1" value={address1} onChange={(e) => setAddress1(e.target.value)} fullWidth margin="normal" />
                    <TextField label="주소 2 (상세주소)" value={address2} onChange={(e) => setAddress2(e.target.value)} fullWidth margin="normal" />


          {/* 결제 버튼 */}
          <Box mt={3} textAlign="center">
            {merchantId && (
              <Payment merchantId={merchantId}
               orderItems={orderItems}
                zipCode={zipCode}
                 address1={address1}
                 address2={address2}
                  orderId={orderId}
                   orderData={orderData}/>
            )}
          </Box>
        </Paper>
      ) : (
        <Typography>주문 정보를 불러오는 중...</Typography>
      )}
    </Box>
  );
};

export default OrderDetail;
