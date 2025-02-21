package com.javalab.student.service.shop;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.javalab.student.constant.ItemSellStatus;
import com.javalab.student.dto.shop.ItemFormDto;
import com.javalab.student.dto.shop.ItemImgDto;
import com.javalab.student.entity.Substance;
import com.javalab.student.entity.shop.Item;
import com.javalab.student.entity.shop.ItemImg;
import com.javalab.student.entity.shop.ItemSubstance; 
import com.javalab.student.repository.shop.ItemImgRepository;
import com.javalab.student.repository.shop.ItemRepository;
import com.javalab.student.repository.shop.ItemSubstanceRepository;
import com.javalab.student.repository.SubstanceRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    // 의존성 주입
    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;
    private final ItemSubstanceRepository itemSubstanceRepository;
    private final SubstanceRepository substanceRepository;

    // 상품 등록
    @Transactional
public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
    // 1. 상품 등록, 저장(영속화)
    Item item = itemFormDto.crateItem();
    itemRepository.save(item);

// 2. 이미지 등록
        if (!itemImgFileList.isEmpty()) {  // 리스트가 비어있지 않을 때만 실행
            for (int i = 0; i < itemImgFileList.size(); i++) {
                ItemImg itemImg = new ItemImg();
                itemImg.setItem(item);

                // 첫 번째 이미지는 대표 이미지로 설정
                itemImg.setRepimgYn(i == 0 ? "Y" : "N");

                // 이미지 저장 서비스 호출
                itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
            }
        }

    // 3. 알러지 성분 저장 로직
    if (itemFormDto.getSubstanceIds() != null && !itemFormDto.getSubstanceIds().isEmpty()) {
        List<ItemSubstance> itemSubstances = itemFormDto.getSubstanceIds().stream()
            .map(substanceId -> {
                Substance substance = substanceRepository.findById(substanceId)
                    .orElseThrow(() -> new EntityNotFoundException("Substance not found"));
                
                ItemSubstance itemSubstance = new ItemSubstance();
                itemSubstance.setItem(item); // 엔티티 연관관계 설정
                itemSubstance.setSubstance(substance);
                
                return itemSubstance;
            })
            .collect(Collectors.toList());
        
        itemSubstanceRepository.saveAll(itemSubstances);
    }

    return item.getId();
}


    
    /**
     * 상품 상세 조회
     * - 한 개의 상품과 여러 개의 상품 이미지 정보를 조회하는 메서드
     * - 상품 ID를 전달받아 상품 상세 정보를 조회하는 메서드 상품 이미지 정보를 조회한다.
     * - 상품 정보와 상품 이미지 정보를 조합하여 상품 상세 정보를 반환한다.
     * - 트랜잭션 내에서 INSERT, UPDATE, DELETE 쿼리가 발생하지 않도록 보장.
     *   혹시 다른 레이어에서 여기서 영속화한 엔티티를 수정하거나 삭제하는 경우가 있을 수 있기 때문에
     *   readOnly = true 옵션을 사용하여 트랜잭션 내에서 SELECT 쿼리만 실행하도록 설정한다.
     * - readOnly = true 옵션을 사용하여 트랜잭션 내에서 SELECT 쿼리만 실행하도록 설정한다.
     * @param itemId
     */
    @Transactional(readOnly = true)
    public ItemFormDto getItemDetail(Long itemId) {

        // 1. 상품 번호로 해당 상품의 이미지들을 조회한다.
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);

        // 2. 조회한 이미지들을 ItemImgDto로 변환하기 위해 List에 담는다.
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        for(ItemImg itemImg : itemImgList){
            ItemImgDto itemImgDto = ItemImgDto.entityToDto(itemImg);
            itemImgDtoList.add(itemImgDto);
        }

       
        // 3. 상품 번호로 해당 상품을 조회한다. 이렇게 조회하면 영속성 컨텍스트에 해당 엔티티가 영속화된다.
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);

        // 조회한 3.상품 정보와 2.이미지 정보를 조합하여 ItemFormDto로 변환한다.
        // 변환하는 이유는 화면에 출력하기 위함이다.
        ItemFormDto itemFormDto = ItemFormDto.of(item);

        // 알러지 성분 ID 추가
    List<Long> substanceIds = itemSubstanceRepository.findByItemId(itemId).stream()
    .map(is -> is.getSubstance().getSubstanceId())
    .collect(Collectors.toList());

itemFormDto.setSubstanceIds(substanceIds);

        // 4. ItemFormDto에 이미지 정보를 설정한다.
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        // 상품정보와 상품의 이미지 정보들에 대한 조회 완료

        return itemFormDto;
    }

    /**
     * 상품 수정(기존)
     * 상품 이미지가 여러 개 일경우
     * @param itemFormDto
     * @param itemImgFileList
     */
    /*public long updateItem(ItemFormDto itemFormDto,
                           List<MultipartFile> itemImgFileList) throws Exception {
        // 1. 수정할 상품 조회, 영속화 - 상품 정보를 수정하기 위해 조회
        Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(EntityNotFoundException::new);

        // 2. 영속화 되어 있는 상품의 정보를 수정한다. - 변경 감지(dirty checking) - 자동감지후 자동 저장됨.
        item.updateItem(itemFormDto);

        // 3. 화면에서 전달된 상품 이미지의 키(기본키)를  arrayList로 받아온다.
        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        // 4. 화면에서 전달된 상품 이미지 파일을 업데이트한다.
        for(int i = 0; i < itemImgFileList.size(); i++){
            // 4.1. 상품 이미지 파일을 업데이트한다.(상품 이미지 id, 상품 이미지 파일)
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
        }
        return item.getId();
    }*/

   /* public long updateItem(ItemFormDto itemFormDto,
                           List<MultipartFile> itemImgFileList) throws Exception {
        // 1. 수정할 상품 조회, 영속화 - 상품 정보를 수정하기 위해 조회
        Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(EntityNotFoundException::new);

        // 2. 영속화 되어 있는 상품의 정보를 수정한다. - 변경 감지(dirty checking) - 자동감지후 자동 저장됨.
        item.updateItem(itemFormDto);

        // 3. 화면에서 전달된 상품 이미지의 키(기본키)를  arrayList로 받아온다.
        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        // 4. 화면에서 전달된 상품 이미지 파일을 업데이트한다.
        for(int i = 0; i < itemImgFileList.size(); i++){
            // 4.1. 상품 이미지 파일을 업데이트한다.(상품 이미지 id, 상품 이미지 파일)
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
        }
        return item.getId();
    }*/
    public long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        // 1. 수정할 상품 조회 (영속화)
        Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(EntityNotFoundException::new);

        // 2. 영속화된 상품 정보 수정 (변경 감지)
        item.updateItem(itemFormDto);

        // 3. 기존 상품 이미지 리스트 조회 (DB에서 가져옴)
        List<ItemImg> itemImgList = itemImgRepository.findByItemId(item.getId());

        // 4. 새로운 이미지 리스트를 기존 이미지와 매칭하여 업데이트
        for (int i = 0; i < itemImgFileList.size(); i++) {
            MultipartFile newFile = itemImgFileList.get(i);

            // 기존 이미지가 존재하면 업데이트
            if (i < itemImgList.size()) {
                ItemImg existingItemImg = itemImgList.get(i);
                itemImgService.updateItemImg(existingItemImg.getId(), newFile);
            } else {
                // 기존 이미지 개수를 초과하는 경우 새 이미지 추가 가능 (필요하면 추가)
                ItemImg newItemImg = new ItemImg();
                newItemImg.setItem(item);
                itemImgService.saveItemImg(newItemImg, newFile);
            }
        }

        return item.getId();
    }



    /**
     * 아이템 전체 리스트
     */
    public List<Item> getItemList() {
        return itemRepository.findAll();
    }

    /**
     * 판매 상태로 검색
     */
    /*public List<Item> getItemListByItemSellStatus(ItemSellStatus itemSellStatus) {
        return itemRepository.findByItemSellStatus(itemSellStatus);
    }*/

    /**
     * 판매중인 상품 리스트
     * 대표 이미지만 반환
     * @param itemSellStatus
     * @return
     */
    @Transactional(readOnly = true)
    public List<ItemFormDto> getItemListByItemSellStatus(ItemSellStatus itemSellStatus) {
        // 1. 판매 상태에 따라 상품 목록 조회
        List<Item> itemList = itemRepository.findByItemSellStatus(itemSellStatus);

        // 2. 조회된 상품 목록을 ItemFormDto 리스트로 변환
        List<ItemFormDto> itemFormDtoList = new ArrayList<>();

        for (Item item : itemList) {
            // 3. 해당 상품의 대표 이미지 조회 (repimgYn = 'Y'인 이미지만 가져오기)
            ItemImg repItemImg = itemImgRepository.findByItemIdAndRepimgYn(item.getId(), "Y");

            // 4. 대표 이미지가 있을 경우, ItemImgDto로 변환
            ItemImgDto repItemImgDto = null;
            if (repItemImg != null) {
                repItemImgDto = ItemImgDto.entityToDto(repItemImg); // 대표 이미지 DTO 변환
            }

            // 5. 상품 정보를 ItemFormDto로 변환하고, 대표 이미지 정보 추가
            ItemFormDto itemFormDto = ItemFormDto.of(item);
            if (repItemImgDto != null) {
                itemFormDto.setItemImgDtoList(List.of(repItemImgDto)); // 대표 이미지만 리스트에 추가
            } else {
                itemFormDto.setItemImgDtoList(new ArrayList<>()); // 대표 이미지가 없는 경우 빈 리스트
            }


            // 알러지 성분 ID 추가
        List<Long> substanceIds = itemSubstanceRepository.findByItemId(item.getId()).stream()
        .map(is -> is.getSubstance().getSubstanceId())
        .collect(Collectors.toList());
    
        itemFormDto.setSubstanceIds(substanceIds);


            // 6. 변환된 DTO를 리스트에 추가
            itemFormDtoList.add(itemFormDto);
        }

        return itemFormDtoList;
    }


    /**
     * 아이템 판매 상태 변경
     */
    @Transactional
    public void updateItemStatus(Long itemId, ItemSellStatus itemSellStatus) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다."));
        item.setItemSellStatus(itemSellStatus); // JPA 변경 감지로 자동 업데이트
    }

// 상품 등록 시 알러지 정보 저장 메서드
@Transactional
public void saveItemWithSubstances(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList, List<Long> safeSubstanceIds) throws Exception {
    // 1. 기존 상품 저장 로직 실행
    Long itemId = saveItem(itemFormDto, itemImgFileList);

    // 2. 알러지 안전 정보 저장
    if (safeSubstanceIds != null && !safeSubstanceIds.isEmpty()) {
        safeSubstanceIds.forEach(substanceId -> {
            ItemSubstance itemSubstance = new ItemSubstance();
            itemSubstance.setId(itemId);
            itemSubstance.setSubstance(null);
            itemSubstanceRepository.save(itemSubstance);
        });
    }
}

// 상품의 알러지 안전 정보 수정
@Transactional
public void updateItemSubstances(Long itemId, List<Long> safeSubstanceIds) {
    // 1. 기존 알러지 정보 삭제
    itemSubstanceRepository.deleteByItemId(itemId);

    // 2. 새로운 알러지 정보 저장
    if (safeSubstanceIds != null && !safeSubstanceIds.isEmpty()) {
        safeSubstanceIds.forEach(substanceId -> {
            ItemSubstance itemSubstance = new ItemSubstance();
            itemSubstance.setId(itemId);
            itemSubstance.setSubstance(null);
            itemSubstanceRepository.save(itemSubstance);
        });
    }
}

// 상품의 알러지 안전 정보 조회
@Transactional(readOnly = true)
public List<Long> getItemSafeSubstances(Long itemId) {
    return itemSubstanceRepository.findSubstanceIdsByItemId(itemId);
}

}
