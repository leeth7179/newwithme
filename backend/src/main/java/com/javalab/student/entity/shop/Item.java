package com.javalab.student.entity.shop;


import com.javalab.student.constant.ItemSellStatus;
import com.javalab.student.dto.shop.ItemFormDto;
import com.javalab.student.entity.BaseEntity;
import com.javalab.student.exception.OutOfStockException;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="item")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item extends BaseEntity {


    @Id
    @Column(name="item_id") // 컬럼명 지정
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;       //상품 코드

    @Column(nullable = false, length = 50)  // 길이 50, null 불가
    private String itemNm; //상품명, 이름을 지정하지 않음 item_nm

    @Column(name="price", nullable = false)
    private Long price; //가격

    @Column(nullable = false)
    private int stockNumber; //재고수량

    // @Lob: 데이터베이스의 BLOB, CLOB 타입과 매핑
    @Lob
    @Column(nullable = false)
    private String itemDetail; //상품 상세 설명

    // EnumType.STRING: Enum의 이름을 DB에 저장
    // 실제 SQL : `item_sell_status` enum('SELL','SOLD_OUT') DEFAULT NULL,
    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; //상품 판매 상태



/**
 * ItemFormDto를 받아서 Item 엔티티를 업데이트
 * @param itemFormDto : 상품 등록, 수정시 상품 정보를 전달하는 DTO
 * - 영속화 되어 있는 Item Entity의 값을 수정하면 더티체킹에 의해서 자동으로 업데이트 쿼리 실행
 * - 주로 상품 이미지를 제외한 나머지 상품 정보를 변경할 때 사용
 */

    public void updateItem(ItemFormDto itemFormDto) {
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }


/**
 * 상품의 재고를 감소시킨다.
 * @param stockNumber
 */

    public void removeStock(int stockNumber){
        int restStock = this.stockNumber - stockNumber;
        if(restStock<0){
            throw new OutOfStockException("상품의 재고가 부족 합니다. (현재 재고 수량: " + this.stockNumber + ")");
        }
        this.stockNumber = restStock;
    }


/**
 * 상품의 재고를 증가시킨다.
 * @param stockNumber
 */

    public void addStock(int stockNumber){
        this.stockNumber += stockNumber;
    }

}
