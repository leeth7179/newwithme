package com.javalab.student.entity.shop;

import com.javalab.student.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name="cart_item")
public class CartItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키 자동 생성, 마리아디비, MySQL은 AUTO increment
    @Column(name="cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  // 다대일 관계, 카트 아이템 입장에서 바라보는 카트는 하나, 기본전략이 EAGER
    @JoinColumn(name="cart_id") // 외래키 지정
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY) // 다대일 관계, 카트 아이템 입장에서 바라보는 상품은 하나, 기본전략이 EAGER
    @JoinColumn(name="item_id")
    private Item item;

    private int count;  // 장바구니 아이템 수량

    /**
     * 장바구니에 담을 장바구니 상품 엔티티를 생성하는 메소드
     * 장바구니에 담을 수량을 증가시켜주는 메소드
     * @param cart : CartItem 의 상위 엔티티
     * @param item
     * @param count
     */
    public static CartItem createCartItem(Cart cart, Item item, int count) {
        CartItem cartItem = new CartItem(); // 카트 아이템 엔티티 생성
        cartItem.setCart(cart); // 카트 아이템에 카트 설정, 카트 아이템과 카트가 연결
        cartItem.setItem(item); // 카트 아이템에 상품 설정, 카트 아이템과 상품이 연결
        cartItem.setCount(count);// 카트 아이템에 수량 설정
        return cartItem;
    }

    /**
     * 장바구니에 기존에 담겨있는 상품을 추가로 또 담았을 경우 수량을 증가시켜주는 메소드
     * @param count : 증가시킬 수량
     */
    public void addCount(int count){
        this.count += count;
    }

    /**
     * 장바구니에 담긴 상품의 수량을 수정하는 메소드
     * @param count : 수정할 수량
     */
    public void updateCount(int count){
        this.count = count;
    }
}
