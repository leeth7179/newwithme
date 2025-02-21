package com.javalab.student.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javalab.student.dto.SubstanceDto;
import com.javalab.student.service.SubstanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/substances")
@RequiredArgsConstructor
public class SubstanceController {
    private final SubstanceService substanceService;

    @GetMapping("/list")
    public ResponseEntity<List<SubstanceDto>> getAllSubstances() {
        List<SubstanceDto> substances = substanceService.findAllSubstances();
        return ResponseEntity.ok(substances);
    }
}
