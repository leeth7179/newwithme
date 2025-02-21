package com.javalab.student.repository;

// import com.javalab.student.constant.ItemSellStatus;
// import com.javalab.student.entity.shop.Item;
// import com.javalab.student.repository.shop.ItemRepository;
// import jakarta.transaction.Transactional;
// import lombok.extern.log4j.Log4j2;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.annotation.Commit;

// import java.util.Random;

// @SpringBootTest
// @Log4j2
// @Transactional
// public class ItemTest {
//     @Autowired
//     ItemRepository itemRepository;

//     private final Random random = new Random();

//     @Test
//     @DisplayName("랜덤 상품 20개 저장 테스트")
//     @Commit
//     public void saveRandomItemsTest() {
//         for (int i = 0; i < 20; i++) {
//             Item item = Item.builder()
//                     .itemNm("테스트상품" + i) // 랜덤 상품명
//                     .price(random.nextInt(50000) + 1000) // 1,000 ~ 50,000원 랜덤 가격
//                     .stockNumber(random.nextInt(100) + 1) // 1 ~ 100개 랜덤 재고
//                     .itemDetail("상품테스트중" + i) // 랜덤 상품 설명
//                     .itemSellStatus(ItemSellStatus.SELL)
//                     .build();

           // itemRepository.save(item);
           // log.info("Saved Item {}: {}", i + 1, item);
       // }
    //}
//}
