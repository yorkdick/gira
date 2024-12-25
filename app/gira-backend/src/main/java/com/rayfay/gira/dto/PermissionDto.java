package com.rayfay.gira.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PermissionDto {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}