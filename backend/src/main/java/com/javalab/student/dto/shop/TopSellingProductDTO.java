package com.javalab.student.dto.shop;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopSellingProductDTO {
    private String itemName;
    private int count;
}
