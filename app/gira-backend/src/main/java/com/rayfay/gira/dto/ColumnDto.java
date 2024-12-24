package com.rayfay.gira.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ColumnDto {
    private Long id;
    private String name;
    private String description;
    private Long boardId;
    private Integer position;
    private List<TaskDto> tasks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}