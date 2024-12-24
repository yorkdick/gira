package com.rayfay.gira.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private Long columnId;
    private Integer position;
    private String priority;
    private String status;
    private String type;
    private Long assigneeId;
    private List<Long> labelIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}