package com.rayfay.gira.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
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