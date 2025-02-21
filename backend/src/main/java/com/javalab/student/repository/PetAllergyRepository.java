package com.javalab.student.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.javalab.student.entity.PetAllergy;

@Repository
public interface PetAllergyRepository extends JpaRepository<PetAllergy, Long> {
    @Query("SELECT pa.substanceId FROM PetAllergy pa WHERE pa.petId = :petId")
    List<Long> findSubstanceIdsByPetId(@Param("petId") Long petId);

    @Modifying
    @Query("DELETE FROM PetAllergy pa WHERE pa.petId = :petId")
    void deleteByPetId(@Param("petId") Long petId);
}
