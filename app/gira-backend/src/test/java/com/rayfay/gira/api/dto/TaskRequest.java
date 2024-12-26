package com.rayfay.gira.api.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TaskRequest {
    private String title;
    private String description;
    private String priority; // HIGH, MEDIUM, LOW
    private String status; // TODO, IN_PROGRESS, DONE
    private String type; // TASK, BUG, IMPROVEMENT
    private Long projectId;
    private Long columnId;
    private Long assigneeId;
    private Long reporterId;
    private Double estimatedHours;
    private LocalDate dueDate;
    private Integer position; // 用于任务排序
}