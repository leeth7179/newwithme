package com.javalab.student.service.shop;

import com.javalab.student.dto.shop.TopSellingProductDTO;
import com.javalab.student.entity.shop.Item;
import com.javalab.student.entity.shop.OrderItem;
import com.javalab.student.repository.shop.ItemRepository;
import com.javalab.student.repository.shop.OrderItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class SalesService {
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;


    /*상품 판매량 */
    public Map<Long, Integer> getSalesCount(List<Long> completedOrderIds) {
        log.info("주어진 주문 ID 목록으로 판매량 집계 중...");

        Map<Long, Integer> salesCount = orderItemRepository.findByOrderIdIn(completedOrderIds)
                .stream()
                .filter(orderItem -> orderItem.getItem() != null) // Null 체크 추가
                .collect(Collectors.groupingBy(
                        orderItem -> orderItem.getItem().getId(),
                        Collectors.summingInt(OrderItem::getCount)
                ));

        log.info("판매량 집계 완료: {}개의 상품에 대한 판매량 집계", salesCount.size());
        return salesCount;
    }

    /* 판매량 상위 5개 상품 */
    public List<TopSellingProductDTO> getTopSellingProducts(Map<Long, Integer> salesCount) {
        log.info("판매량 상위 5개 상품 추출 중...");

        List<TopSellingProductDTO> topSellingProducts = salesCount.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .map(entry -> {
                    Item item = itemRepository.findById(entry.getKey()).orElse(null);
                    return new TopSellingProductDTO(item.getItemNm(), entry.getValue());
                })
                .collect(Collectors.toList());

        log.info("상위 5개 판매 상품 추출 완료: {}", topSellingProducts.size());
        return topSellingProducts;
    }

    // 일 매출 금액 계산
    public double getDailySalesAmount(List<Long> completedOrderIds, LocalDate date) {
        log.info("{} 일 매출 금액 계산 중...", date);





        double dailySalesAmount = orderItemRepository.findByOrderIdIn(completedOrderIds)
                .stream()
                .filter(orderItem -> orderItem.getItem() != null &&
                        orderItem.getOrder().getOrderDate().toLocalDate().isEqual(date)) // 날짜만 비교
                .mapToDouble(orderItem -> orderItem.getItem().getPrice() * orderItem.getCount()) // 가격 * 수량
                .sum();

        log.info("{} 일 매출 금액: {}", date, dailySalesAmount);
        return dailySalesAmount;
    }

    // 월 매출 금액 계산
    public double getMonthlySalesAmount(List<Long> completedOrderIds, LocalDate startDate, LocalDate endDate) {
        log.info("월 매출 금액 계산 중... 시작일: {}, 종료일: {}", startDate, endDate);

        // LocalDate -> LocalDateTime으로 변환
        LocalDateTime startDateTime = startDate.atStartOfDay(); // 시작일의 00:00:00 시각을 생성
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59); // 끝날짜의 23:59:59 시각을 생성

        double monthlySalesAmount = orderItemRepository.findByOrderIdIn(completedOrderIds)
                .stream()
                .filter(orderItem -> orderItem.getItem() != null &&
                        orderItem.getOrder().getOrderDate().isAfter(startDateTime) &&
                        orderItem.getOrder().getOrderDate().isBefore(endDateTime))
                .mapToDouble(orderItem -> orderItem.getItem().getPrice() * orderItem.getCount()) // 가격 * 수량
                .sum();

        log.info("계산된 월 매출 금액: {}", monthlySalesAmount);
        return monthlySalesAmount;
    }
}
