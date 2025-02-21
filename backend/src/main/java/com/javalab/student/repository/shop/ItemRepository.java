package com.javalab.student.repository.shop;

import com.javalab.student.constant.ItemSellStatus;
import com.javalab.student.entity.shop.Item;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 상품 레파지토리
 * - 상품과 관련된 데이터베이스 처리를 담당
 * JpaRepository<Item, Long>를 상속받아 Item 엔티티를 관리
 * Item : 엔티티 클래스 이름
 * Long : 엔티티 클래스의 ID(기본키) 필드 타입(포장클래스)
 * 실행시 Item 엔티티와 관련된 CRUD 메서드가 자동으로 생성된다.
 * - 생성 원리는 JpaRepository 인터페이스가 제네릭으로 받은 엔티티 클래스를 기반으로
 *   해당 엔티티에 대한 CRUD 메서드를 자동으로 생성하기 때문
 * QuerydslPredicateExecutor<Item> : Querydsl을 사용하여 동적 쿼리를 생성하기 위한 인터페이스
 * - Querydsl은 JPA를 사용하는 동적 쿼리를 생성하기 위한 프레임워크
 * - findAll(Predicate predicate) : 동적 쿼리를 생성하기 위한 메서드 여러개 존재함.
 *   이 추상 메소드들은 런티임에 Querydsl이 구현체를 생성하여 추상메소드들의 구현체를 제공한다.
 *   이와 같은 프록시 패턴을 사용하여 동적 쿼리를 생성한다.(프록시가 대신한다)
 */
public interface ItemRepository extends JpaRepository<Item, Long>,
        QuerydslPredicateExecutor<Item>, ItemRepositoryCustom {
    // 1. 상품명으로 상품 검색하는 쿼리메소드
    // 찾는 상품이 있으면 List로 반환, 없으면 빈 List 반환, 널이 아님
    List<Item> findByItemNm(String itemNm);

    // 2. 상품명과 상품 설명으로 상품 검색 쿼리메소드
    // 찾는 상품이 있으면 List로 반환, 없으면 빈 List 반환, 널이 아님
    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);

    // 3. 상품 가격으로 조회
    // 가격이 price보다 작은 상품을 조회하는 쿼리메소드
    List<Item> findByPriceLessThan(Integer price);

    // 4. [JPQL] 상품 상세 설명을 받아서 조회하고 정렬순서는 가격이 높은 순서로 한다
    // JPQL : JPA에서 사용하는 (객체지향형) 쿼리 언어
    // JPQL은 엔티티 객체를 대상으로 쿼리를 작성하고, SQL은 데이터베이스 테이블을 대상으로 쿼리를 작성
    // 1) from Item i : Item 엔티티를 i라는 별칭으로 사용
    // 2) select i : Item 엔티티를 조회
    // 3) where i.itemDetail : i 엔티티의 itemDetail 필드를 사용
    // 4) :itemDetail : 파라미터로 받은 itemDetail 값을 사용(매개변수 바인딩-String itemDetail)
    // 5) like %:itemDetail% : itemDetail 필드에 itemDetail 문자열이 포함된 엔티티를 조회
    // 6) order by i.price desc : 조회된 엔티티를 price 필드를 기준으로 내림차순 정렬
    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);

    // 5. [Native Query] 상품 상세 설명을 받아서 조회하고 정렬순서는 가격이 높은 순서로 한다
    // Native Query : SQL을 직접 작성하여 실행하는 방식
    // - JPQL은 엔티티 객체를 대상으로 쿼리를 작성하고, SQL은 데이터베이스 테이블을 대상으로 쿼리를 작성
    // - nativeQuery = true : SQL을 직접 작성하여 실행
    @Query(value="select * from item i where i.item_detail like " +
            "%:itemDetail% order by i.price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);

    List<Item> findByItemSellStatus(ItemSellStatus itemSellStatus);



    @Query("UPDATE Item i SET i.itemSellStatus = :itemSellStatus WHERE i.id = :itemId")
    void updateItemSellStatus(@Param("itemId") Long itemId, @Param("itemSellStatus") ItemSellStatus itemSellStatus);
}
