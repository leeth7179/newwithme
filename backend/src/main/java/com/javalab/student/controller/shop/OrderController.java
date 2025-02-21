package com.javalab.student.controller.shop;

import com.javalab.student.dto.shop.OrderDto;
import com.javalab.student.dto.shop.OrderHistDto;
import com.javalab.student.dto.shop.OrderItemDto;
import com.javalab.student.service.shop.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

/**
 * 주문 컨트롤러
 * - api/orders(Post) : 주문 요청
 * - api/orders(Get) : 주문 내역 조회
 * - api/orders/{page} : 주문 내역 조회 페이징처리
 * - api/orders/{orderId}/cancel : 주문 취소
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("api/orders")
@Log4j2
public class OrderController {

    private final OrderService orderService;


    /**
     * 주문 상세 조회 API
     * @param orderId 주문 ID
     * @param principal 로그인한 사용자
     * @return 주문 상세 정보
     */
    @GetMapping("/view/{orderId}")
    public ResponseEntity<?> getOrderDetail(@PathVariable Long orderId, Principal principal) {
        log.info("주문 상세 조회: 주문 ID={}, 사용자={}", orderId, principal.getName());

        // 주문 상세 정보 가져오기
        List<OrderItemDto> orderDetail = orderService.getOrderDetail(orderId, principal.getName());

        if (orderDetail == null) {
            return ResponseEntity.badRequest().body("해당 주문을 찾을 수 없습니다.");
        }

        return ResponseEntity.ok(orderDetail);
    }

    /*@PostMapping
    public @ResponseBody ResponseEntity<?> order(@RequestBody @Valid OrderDto orderDto,
                                                 BindingResult bindingResult, Principal principal) {
        log.info("주문 요청: {}, 사용자: {}", orderDto, principal.getName());

        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                sb.append(fieldError.getDefaultMessage()).append(" ");
            }
            log.warn("입력값 검증 실패: {}", sb);
            return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        try {
            Long orderId = orderService.order(orderDto, principal.getName());
            log.info("주문 완료: 주문ID {}", orderId);
            return new ResponseEntity<>(orderId, HttpStatus.OK);
        } catch (Exception e) {
            log.error("주문 중 오류 발생", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }*/

    @GetMapping(value = {"/", "/{page}"})
    public ResponseEntity<Page<OrderHistDto>> orderHist(@PathVariable("page") Optional<Integer> page, Principal principal) {
        log.info("주문 내역 조회 요청: 사용자 {}, 페이지 {}", principal.getName(), page.orElse(0));

        Pageable pageable = PageRequest.of(page.orElse(0), 4);
        Page<OrderHistDto> ordersHistDtoList = orderService.getOrderList(principal.getName(), pageable);

        log.info("조회된 주문 내역 수: {}", ordersHistDtoList.getTotalElements());
        return new ResponseEntity<>(ordersHistDtoList, HttpStatus.OK);
    }

    @PostMapping("/{orderId}/cancel")
    public @ResponseBody ResponseEntity<?> cancelOrder(@PathVariable("orderId") Long orderId, Principal principal) {
        log.info("주문 취소 요청: 주문ID {}, 사용자 {}", orderId, principal.getName());

        if (!orderService.validateOrder(orderId, principal.getName())) {
            log.warn("주문 취소 권한 없음: 주문ID {}", orderId);
            return new ResponseEntity<>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        orderService.cancelOrder(orderId);
        log.info("주문 취소 완료: 주문ID {}", orderId);
        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }
}
