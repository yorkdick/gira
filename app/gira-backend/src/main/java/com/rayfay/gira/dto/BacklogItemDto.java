package com.rayfay.gira.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BacklogItemDto {
    private Long id;
    private String title;
    private String description;
    private String priority;
    private String status;
    private String type;
    private Long projectId;
    private Long sprintId;
    private Long assigneeId;
    private LocalDateTime dueDate;
    private Integer storyPoints;
}