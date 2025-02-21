package com.javalab.student.controller.shop;

import com.javalab.student.dto.shop.*;
import com.javalab.student.service.shop.CartService;
import com.javalab.student.service.shop.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 *  장바구니 컨트롤러
 *  - /api/cart/add : 장바구니 상품 추가
 *  - /api/cart/list : 장바구니 조회
 *  - /api/cart/cartItem/{cartItemId} : 장바구니 수량 수정(Patch)
 *  - /api/cart/cartItem/{cartItemId} : 장바구니 아이템 삭제(Delete)
 *  - /api/cart/orders : 장바구니 아이템 주문 처리
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/cart")
@Log4j2
public class CartController {

    private final CartService cartService;
    private final SubscriptionService subscriptionService;

    /**
     *  장바구니 상품 추가
     */
    @PostMapping("/add")
    public @ResponseBody ResponseEntity<?> order(@RequestBody @Valid CartItemDto cartItemDto,
                                                 BindingResult bindingResult, Principal principal) {
        log.info("장바구니 담기 요청: {}, 사용자: {}", cartItemDto, principal.getName());

        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                sb.append(fieldError.getDefaultMessage()).append(" ");
            }
            log.warn("입력값 검증 실패: {}", sb);
            return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        try {
            Long cartItemId = cartService.addCart(cartItemDto, principal.getName());
            log.info("장바구니에 상품 추가 완료: {}", cartItemId);
            return new ResponseEntity<>(cartItemId, HttpStatus.OK);
        } catch (Exception e) {
            log.error("장바구니 추가 중 오류 발생", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/list")
    public @ResponseBody ResponseEntity<?> getCartList(Principal principal) {
        log.info("장바구니 목록 조회 요청: 사용자 {}", principal.getName());

        try {
            List<CartDetailDto> cartDetailList = cartService.getCartList(principal.getName());
            log.info("장바구니 목록 조회 완료: {}", cartDetailList.size());
            return new ResponseEntity<>(cartDetailList, HttpStatus.OK);
        } catch (Exception e) {
            log.error("장바구니 목록 조회 중 오류 발생", e);
            return new ResponseEntity<>("장바구니 목록을 불러오는 데 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     *  장바구니 상품 수량 수정
     *    PatchMapping  : 데이터 일부만 수정
     *    PutMapping    : 전체 데이터 수정
     *    - PutMapping을 쓰면 데이터 전체가 변경되고, PatchMapping을 쓰면 데이터 일부만 변경
     */
    @PatchMapping("/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity<?> updateCartItem(@PathVariable("cartItemId") Long cartItemId,
                                                          @RequestParam("count") int count,
                                                          Principal principal) {
        log.info("장바구니 상품 수량 수정 요청: 상품ID {}, 수량 {}, 사용자 {}", cartItemId, count, principal.getName());

        if (count <= 0) {
            return new ResponseEntity<>("최소 1개 이상 담아주세요", HttpStatus.BAD_REQUEST);
        } else if (!cartService.validateCartItem(cartItemId, principal.getName())) {
            return new ResponseEntity<>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.updateCartItemCount(cartItemId, count);
        log.info("장바구니 상품 수량 수정 완료: 상품ID {}", cartItemId);
        return new ResponseEntity<>(cartItemId, HttpStatus.OK);
    }

    /**
     * 장바구니 상품 삭제
     */
    @DeleteMapping("/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity<?> deleteCartItem(@PathVariable("cartItemId") Long cartItemId,
                                                          Principal principal) {
        log.info("장바구니 상품 삭제 요청: 상품ID {}, 사용자 {}", cartItemId, principal.getName());

        if (!cartService.validateCartItem(cartItemId, principal.getName())) {
            return new ResponseEntity<>("삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.deleteCartItem(cartItemId);
        log.info("장바구니 상품 삭제 완료: 상품ID {}", cartItemId);
        return new ResponseEntity<>(cartItemId, HttpStatus.OK);
    }

    /**
     * 장바구니 상품 주문처리
     */
    @PostMapping("/orders")
    public @ResponseBody ResponseEntity<?> orderCartItem(@RequestBody CartOrderRequestDto cartOrderRequestDto,
                                                         Principal principal) {
        log.info("장바구니 주문 요청: {}, 사용자 {}", cartOrderRequestDto, principal.getName());

        // 주문할 상품 리스트화
        List<CartOrderItemDto> cartOrderItems = cartOrderRequestDto.getCartOrderItems();
        if (cartOrderItems == null || cartOrderItems.isEmpty()) {
            return new ResponseEntity<>("주문할 상품을 선택해주세요", HttpStatus.FORBIDDEN);
        }

        // 로그인한 회원과 장바구니 소유자가 일치하는지 확인
        for (CartOrderItemDto cartOrderItem : cartOrderItems) {
            if (!cartService.validateCartItem(cartOrderItem.getCartItemId(), principal.getName())) {
                return new ResponseEntity<>("주문 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        }
        log.info("장바구니 아이템이 유효합니다. 주문 처리 시작.");

        Long orderId = cartService.orderCartItem(cartOrderItems, principal.getName());
        log.info("장바구니 주문 완료: 주문ID {}", orderId);
        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }


    /**
     * 구독 상품 주문 처리
     */
    @PostMapping("/subscriptions/orders")
    public @ResponseBody ResponseEntity<?> orderSubscriptionItem(@RequestBody SubscriptionOrderItemDto subscriptionOrderRequestDto,
                                                                 Principal principal) {
        log.info("구독 상품 주문 요청: {}, 사용자 {}", subscriptionOrderRequestDto, principal.getName());

        // 구독 상품 주문할 상품 리스트화
        if (subscriptionOrderRequestDto.getItemId() == null) {
            return new ResponseEntity<>("구독 상품 정보를 선택해주세요", HttpStatus.FORBIDDEN);
        }

        log.info("구독 상품 주문 처리 시작.");

        // 구독 상품 주문 처리
        Long orderId = subscriptionService.orderSubscriptionItem(subscriptionOrderRequestDto, principal.getName());
        log.info("구독 상품 주문 완료: 주문ID {}", orderId);


        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }
}
