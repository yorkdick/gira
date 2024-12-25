package com.rayfay.gira.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeamMemberDto {
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private Long teamId;

    private String role;

    private LocalDateTime joinedAt;

    private UserDto user;
}