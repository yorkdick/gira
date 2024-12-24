package com.rayfay.gira.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;

    @NotBlank(message = "Comment content is required")
    @Size(min = 1, max = 5000, message = "Comment content must be between 1 and 5000 characters")
    private String content;

    private Long taskId;
    private Long userId;
    private Long parentId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}