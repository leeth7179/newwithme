package com.javalab.student.service.shop;

import com.javalab.student.constant.OrderStatus;
import com.javalab.student.dto.shop.PaymentRequestDto;
import com.javalab.student.entity.shop.Order;
import com.javalab.student.repository.shop.OrderRepository;
import com.javalab.student.repository.shop.PaymentRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 결제 서비스 (포트원 SDK 적용)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final IamportClient iamportClient;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CartService cartService;

    @Transactional
    public Map<String, Object> processPayment(PaymentRequestDto requestDto, Long orderId) {
        log.info("🔹 결제 검증 시작: {}", requestDto);

        // 1. 주문 정보 조회
        Order order = orderRepository.findById(requestDto.getMerchantUid())
                .orElseThrow(() -> new IllegalArgumentException("❌ 주문 정보를 찾을 수 없습니다: " + requestDto.getMerchantUid()));

        // 2. 포트원 API를 사용하여 결제 정보 조회 및 검증
        IamportResponse<Payment> paymentResponse;
        try {
            paymentResponse = iamportClient.paymentByImpUid(requestDto.getImpUid());
        } catch (IamportResponseException | IOException e) {
            throw new IllegalArgumentException("❌ 포트원 결제 검증 실패: " + e.getMessage());
        }

        // 3. 결제 정보 조회
        Payment paymentInfo = paymentResponse.getResponse();
        if (paymentInfo == null) {
            throw new IllegalArgumentException("❌ 결제 정보 없음: imp_uid=" + requestDto.getImpUid());
        }

        // 4. 결제 금액 검증
        BigDecimal paidAmount = paymentInfo.getAmount();
        if (paidAmount.compareTo(requestDto.getPaidAmount()) != 0) {
            throw new IllegalArgumentException("❌ 결제 금액 불일치: 요청 금액=" + requestDto.getPaidAmount() + ", 실제 결제 금액=" + paidAmount);
        }

        // 5. Payment 엔티티 생성 및 저장
        com.javalab.student.entity.shop.Payment payment = com.javalab.student.entity.shop.Payment.builder()
                .order(order)
                .impUid(requestDto.getImpUid())
                .itemNm(requestDto.getName())
                .orderStatus(OrderStatus.PAYMENT_COMPLETED)
                .amount(paidAmount)
                .paymentMethod(requestDto.getPayMethod())
                .buyerEmail(requestDto.getBuyerEmail())
                .buyerName(requestDto.getBuyerName())
                .buyerTel(requestDto.getBuyerTel())
                .buyerAddr(requestDto.getBuyerAddr())
                .buyerPostcode(requestDto.getBuyerPostcode())
                .paidAt(requestDto.getPaidAt())
                .build();

        paymentRepository.save(payment);
        log.info("✅ 결제 정보 저장 완료: {}", payment);

        // 6. 주문 상태 업데이트
        order.setOrderStatus(OrderStatus.PAYMENT_COMPLETED);
        orderRepository.save(order);

        // 7. ✅ 결제 완료 후 장바구니 아이템 삭제
        List<Long> cartItemIds = requestDto.getCartItemId();
        if (cartItemIds != null && !cartItemIds.isEmpty()) {
            // cartItemIds에 포함된 상품을 장바구니에서 삭제
            log.info("결제 완료 후 삭제할 장바구니 상품 IDs: {}", cartItemIds);
            cartService.removeCartItem(cartItemIds);  // 장바구니 아이템 삭제
        }


        // 8. 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("paymentId", payment.getId());
        response.put("impUid", payment.getImpUid());
        response.put("merchantUid", payment.getOrder().getId());
        response.put("amount", payment.getAmount());
        response.put("paymentMethod", payment.getPaymentMethod());
        response.put("status", payment.getOrderStatus());
        response.put("paidAt", payment.getPaidAt());

        return response;
    }

}

