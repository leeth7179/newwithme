package com.javalab.student.service.shop;


import com.javalab.student.dto.shop.*;
import com.javalab.student.entity.shop.Item;
import com.javalab.student.repository.shop.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class SubscriptionService {

    private final OrderService orderService;
    private final ItemRepository itemRepository;

    public Long orderSubscriptionItem(SubscriptionOrderItemDto subscriptionOrderRequestDto, String email) {
        log.info("구독 상품 주문 서비스 시작", subscriptionOrderRequestDto, email);

        // 1. 구독 상품 조회
        Item subscriptionItem = itemRepository
                .findById(subscriptionOrderRequestDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);

        // 2. 구독 상품을 OrderItemDto로 변환
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setItemId(subscriptionItem.getId());
        orderItemDto.setItemNm(subscriptionItem.getItemNm());
        orderItemDto.setCount(subscriptionOrderRequestDto.getCount()); // 요청받은 수량 사용

        // 3. 주문 서비스 호출하여 구독 상품 주문 생성
        List<OrderItemDto> orderItemDtoList = new ArrayList<>();
        orderItemDtoList.add(orderItemDto);

        Long orderId = orderService.orders(orderItemDtoList, email);

        // 4. 구독 상품 주문 완료 후 장바구니 항목 삭제하지 않음
        log.info("구독 상품 주문 완료: 주문ID {}", orderId);

        // 5. 주문 ID 반환
        return orderId;
    }


}
