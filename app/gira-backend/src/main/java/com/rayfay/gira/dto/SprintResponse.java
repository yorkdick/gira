package com.rayfay.gira.dto;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class SprintResponse {
    private Long id;
    private String name;
    private String goal;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private String status;
    private Long projectId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}