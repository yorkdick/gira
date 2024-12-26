package com.rayfay.gira.api.dto;

import lombok.Data;

@Data
public class BacklogItemRequest {
    private String title;
    private String description;
    private String priority; // HIGH, MEDIUM, LOW
    private String type; // STORY, BUG, TASK
    private Long projectId;
    private Long assigneeId;
    private Long reporterId;
    private Long sprintId;
    private Double estimatedHours;
    private String status; // TODO, IN_PROGRESS, DONE
}