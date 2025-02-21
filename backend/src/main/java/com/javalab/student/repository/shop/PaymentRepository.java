package com.javalab.student.repository.shop;

import com.javalab.student.entity.shop.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // 주문 ID로 결제 내역 조회
    Optional<Payment> findByOrderId(Long orderId);

    // PG사 결제 고유번호로 IMP UID로 결제 내역 조회
    Optional<Payment> findByImpUid(String impUid);

}
