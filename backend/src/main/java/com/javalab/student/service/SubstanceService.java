package com.javalab.student.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.javalab.student.dto.SubstanceDto;
import com.javalab.student.repository.SubstanceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubstanceService {
    private final SubstanceRepository substanceRepository;

    public List<SubstanceDto> findAllSubstances() {
        return substanceRepository.findAll().stream()
            .map(substance -> SubstanceDto.builder()
                .substanceId(substance.getSubstanceId())
                .name(substance.getName())
                .build())
            .collect(Collectors.toList());
    }
}
