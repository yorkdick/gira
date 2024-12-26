package com.rayfay.gira.api.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SprintRequest {
    private String name;
    private String goal;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status; // PLANNING, ACTIVE, COMPLETED
    private Long projectId;
    private Long[] backlogItemIds; // 用于将 Backlog 项移动到 Sprint
}