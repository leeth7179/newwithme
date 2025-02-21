package com.javalab.student.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDto {
    
    private Long id;
    private String title;
    private String content;
    private String category;
    private Boolean important = Boolean.FALSE;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}