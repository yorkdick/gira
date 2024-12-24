package com.rayfay.gira.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttachmentDto {
    private Long id;
    private String filename;
    private String contentType;
    private Long size;
    private String path;
    private Long taskId;
    private UserDto user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}