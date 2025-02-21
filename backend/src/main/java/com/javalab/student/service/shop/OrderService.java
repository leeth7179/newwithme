package com.javalab.student.service.shop;


import com.javalab.student.dto.shop.OrderDto;
import com.javalab.student.dto.shop.OrderHistDto;
import com.javalab.student.dto.shop.OrderItemDto;
import com.javalab.student.entity.Member;
import com.javalab.student.entity.shop.Item;
import com.javalab.student.entity.shop.ItemImg;
import com.javalab.student.entity.shop.Order;
import com.javalab.student.entity.shop.OrderItem;
import com.javalab.student.repository.MemberRepository;
import com.javalab.student.repository.shop.ItemImgRepository;
import com.javalab.student.repository.shop.ItemRepository;
import com.javalab.student.repository.shop.OrderItemRepository;
import com.javalab.student.repository.shop.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class OrderService {

   // 의존성 주입
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final ItemImgRepository itemImgRepository;


    // 주문 엔티티 생성 및 영속화
    /*public Long order(OrderDto orderDto, String email) {
        // 1. 주문할 상품 조회(영속화)
        Item item = itemRepository.findById(orderDto.getItemId())
                                    .orElseThrow(EntityNotFoundException::new);
        // 2. 주문자 조회, 영속화 상태로 만들기
        Member member = memberRepository.findByEmail(email);
        // 3. 주문 생성
        // 3.1. 주문 상품 생성
        List<OrderItem> orderItemList = new ArrayList<>();
        // 3.2. 주문 아이템 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        // 3.3. 주문 아이템 리스트를 주문에 추가
        orderItemList.add(orderItem);
        // 3.4. 주문 생성(주문자, 주문 상품 리스트)
        Order order = Order.createOrder(member, orderItemList);
        // 3.5. 주문 저장, 영속화 상태로 만들기, 주문 저장 쿼리문 실행
        orderRepository.save(order);

        return order.getId();   // 주문 번호 반환
    }*/


    /**
     * 주문 목록 조회
     * - 주문 목록을 조회하기 위해서는 회원의 이메일이 필요하다.
     * - 주문 목록은 페이징 처리가 되어야 한다.
     * - 주문 목록은 주문 번호, 주문 일자, 주문 상품 리스트, 주문 상태로 구성된다.
     * - 주문 상품 리스트는 주문 상품 번호, 상품명, 상품 가격, 상품 이미지로 구성된다.
     * - 주문 목록은 주문 일자를 기준으로 내림차순 정렬한다.
     * @param email 회원 이메일
     * @param pageable 페이징 처리
     * @return
     */
    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {

        // 1. 주문 목록 조회
        List<Order> orders = orderRepository.findOrders(email, pageable);
        // 2. 주문 목록 총 개수 조회
        Long totalCount = orderRepository.countOrder(email);
        // 3. 주문 목록 저장용 ArrayList 생성
        List<OrderHistDto> orderHistDtos = new ArrayList<>();
        // 3.1. 주문 목록을 순회하면서 주문 정보를 DTO로 변환
        for (Order order : orders) {
            // 3.1.1. 주문 한 건에 대한 주문 DTO 생성
            OrderHistDto orderHistDto = new OrderHistDto(order);
            // 3.1.2. 주문 정보에서 주문 상품 리스트 조회
            List<OrderItem> orderItems = order.getOrderItems();
            // 3.2. 하나의 주문에서 여러 상품을 주문할 수 있으므로 상품 리스트를 순회하면서 상품 정보를 DTO로 변환
            for (OrderItem orderItem : orderItems) {
                // 3.2.1 주문 상품 리스트에서 각 상품의 대표 이미지만 조회해서 ItemImg 객체 생성
                ItemImg itemImg = itemImgRepository
                        .findByItemIdAndRepimgYn(orderItem.getItem().getId(), "Y");
                // 3.2.2. 주문 상품 DTO 생성
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
                // 3.2.3 주문 DTO에 주문 상품 추가
                orderHistDto.addOrderItemDto(orderItemDto);
            }
            // 3.3. 주문 목록에 주문 DTO 추가, 한 건의 주문 정보를 전체 주문 목록에 추가
            orderHistDtos.add(orderHistDto);
        }
        // 4. 주문 목록 반환
        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);
    }


    /**
     * 주문 검증 : 주문 취소 권한 확인
     * - 주문 취소를 위해서는 주문 번호와 주문자 이메일이 필요하다.
     * - 주문 번호로 주문을 조회한다.
     * @param orderId
     * @param email
     * @return
     */
    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email){
        // 현재 로그인 한 회원 정보 조회
        Member curMember = memberRepository.findByEmail(email);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        // 주문을 낸 회원 정보 조회
        Member savedMember = order.getMember();
        // 주문을 낸 회원과 현재 로그인한 회원이 같은지 확인
        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return false;
        }

        return true;
    }

    /**
     * 주문 취소
     * - 주문 취소를 위해서는 주문 번호가 필요하다.
     * - 주문 번호로 주문을 조회한다.
     * - 주문 취소를 한다.
     * @param orderId
     */
    public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();
    }


    /**
     * 장바구니에서 주문을 여러개 선택해서 주문
     * - 장바구니에서 여러 상품을 선택해서 주문할 수 있다.
     * - 주문할 상품 리스트와 회원 이메일을 인자로 받는다.
     * - 주문할 회원을 조회한다.
     * - 전달받은 주문 상품 리스트를 순회하면서 장바구니에서 전달받은 dto를 이용해서 주문 상품 엔티티를 생성한다.
     * @param orderItemDtoList
     * @param email
     * @return
     */
    public Long orders(List<OrderItemDto> orderItemDtoList, String email){
        // 1. 주문자 조회
        Member member = memberRepository.findByEmail(email);
        // 2. 주문 상품 리스트 저장을 위한 ArrayList 생성
        List<OrderItem> orderItemList = new ArrayList<>();
        // 3. 장바구니에서 전달받은 dto를 순회하면서 주문 상품 엔티티 생성 후 리스트에 추가
        for (OrderItemDto orderItemDto : orderItemDtoList) {
            Item item = itemRepository.findById(orderItemDto.getItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderItem orderItem = OrderItem.createOrderItem(item, orderItemDto.getCount());
            orderItemList.add(orderItem);
        }
        // 4. 위에서 생성한 주문 상품 리스트와 주문자를 이용해서 주문 엔티티 생성
        Order order = Order.createOrder(member, orderItemList);
        // 5. 주문 저장(영속화)
        orderRepository.save(order);
        // 6. 주문 번호 반환
        return order.getId();
    }

    /**
     * 주문 상세 정보 조회
     * @param orderId 주문 ID
     * @param email 로그인사용자 email
     * @return 주문 상세 DTO
     */
    @Transactional(readOnly = true)
    public List<OrderItemDto> getOrderDetail(Long orderId, String email) {
        log.info("주문 상세 조회: 주문 ID={}, 사용자 이메일={}", orderId, email);

        // 주문 존재 및 사용자 검증
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다."));

        if (!order.getMember().getEmail().equals(email)) {
            throw new SecurityException("해당 주문에 접근할 권한이 없습니다.");
        }

        // 기본 이미지 URL 설정
        String defaultImageUrl = "/assets/images/noImg.jpg";

        // 주문 아이템 조회 및 DTO 변환 (이미지 포함)
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        return orderItems.stream()
                .map(orderItem -> {
                    // 대표 이미지 조회 (Optional.ofNullable 사용)
                    ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn(orderItem.getItem().getId(), "Y");
                    String imgUrl = (itemImg != null) ? itemImg.getImgUrl() : defaultImageUrl;

                    return new OrderItemDto(orderItem, imgUrl);
                })
                .collect(Collectors.toList());
    }
    }


