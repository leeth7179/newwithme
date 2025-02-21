package com.javalab.student.repository.shop;

import com.javalab.student.entity.shop.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    // 1. 회원 ID로 장바구니 조회
    Cart findByMemberId(Long memberId);
}
