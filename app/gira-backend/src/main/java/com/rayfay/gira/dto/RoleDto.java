package com.rayfay.gira.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class RoleDto {
    private Long id;

    @NotBlank
    private String name;

    private String description;

    private boolean isSystem;

    private Set<PermissionDto> permissions = new HashSet<>();

    private LocalDateTime createdAt;
}