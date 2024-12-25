package com.rayfay.gira.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class IssueDto {
    private Long id;

    @NotBlank
    @Size(min = 3, max = 100)
    private String title;

    @NotBlank
    @Size(min = 10, max = 2000)
    private String description;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer priority;

    private Long assigneeId;

    @Future
    private LocalDateTime dueDate;

    private List<String> labels;

    private List<String> attachments;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String status;

    private Long projectId;

    private Long sprintId;

    private String type;

    @Min(0)
    private Integer storyPoints;
}