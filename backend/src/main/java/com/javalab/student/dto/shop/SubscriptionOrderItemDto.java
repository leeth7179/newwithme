package com.javalab.student.dto.shop;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionOrderItemDto {
    private Long itemId;  // 구독 상품 ID
    private int count;    // 주문 수량
}
