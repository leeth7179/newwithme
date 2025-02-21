package com.javalab.student.dto;

import com.javalab.student.entity.PetAllergy;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetDto implements Serializable {
    private Long petId;
    private Long userId;
    private String name;
    private Integer age;
    private Boolean neutered;
    private String healthConditions;
    private String breed;
    private Integer weight;
    private String gender;
    private String imageUrl;
    private String imageName;
    private List<Long> allergyIds;

   
}