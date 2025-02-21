package com.javalab.student.constant;

/**
 * 주문 상태
 * - ORDER: 주문 완료
 * - CANCEL: 주문 취소
 */
public enum OrderStatus {
    ORDERED,          // 주문
    PAYMENT_PENDING,  // 입금대기
    PAYMENT_COMPLETED,// 결제완료
    PREPARING_SHIPMENT, // 배송준비
    IN_TRANSIT,       // 배송중
    DELIVERED,        // 배송완료
    RETURN_REQUESTED, // 반품요청
    CANCELED,         // 주문취소
    ORDER_COMPLETED   // 주문완료
}
