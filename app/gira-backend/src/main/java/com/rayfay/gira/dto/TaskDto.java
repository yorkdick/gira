package com.rayfay.gira.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private Integer position;
    private String priority; // TaskPriority的字符串表示
    private String status; // TaskStatus的字符串表示
    private String type; // TaskType的字符串表示
    private Float estimatedHours;
    private LocalDateTime dueDate;

    // 关联ID
    private Long projectId;
    private String projectName;
    private Long columnId;
    private String columnName;
    private Long assigneeId;
    private String assigneeName;
    private Long reporterId;
    private String reporterName;

    // 关联列表
    private List<LabelDto> labels = new ArrayList<>();
    private List<CommentDto> comments = new ArrayList<>();
    private List<AttachmentDto> attachments = new ArrayList<>();

    // 审计信息
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}