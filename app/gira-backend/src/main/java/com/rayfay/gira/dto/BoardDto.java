package com.rayfay.gira.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BoardDto {
    private Long id;
    private String name;
    private String description;
    private Long projectId;
    private List<ColumnDto> columns;
    private boolean archived;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}