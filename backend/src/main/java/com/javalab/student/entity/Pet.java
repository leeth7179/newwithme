package com.javalab.student.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import com.javalab.student.constant.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pet")
@Getter
@Setter
@NoArgsConstructor
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_id", nullable = false, updatable = false)
    private Long petId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "neutered")
    private Boolean neutered = false;

    @Column(name = "health_conditions")
    private String healthConditions;

    @Column(name = "breed", length = 50)
    private String breed;

    @Column(name = "weight", nullable = false)
    private Integer weight;

    @Column(name = "gender", nullable = false, length = 1)
    private String gender;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "image_name")
    private String imageName;

    // Builder 패턴 추가
    @Builder
    public Pet(Long userId, String name, Integer age, Boolean neutered, 
               String healthConditions, String breed, Integer weight, 
               String gender, String imageUrl, String imageName) {
        this.userId = userId;
        this.name = name;
        this.age = age;
        this.neutered = neutered;
        this.healthConditions = healthConditions;
        this.breed = breed;
        this.weight = weight;
        this.gender = gender;
        this.imageUrl = imageUrl;
        this.imageName = imageName;
    }
}