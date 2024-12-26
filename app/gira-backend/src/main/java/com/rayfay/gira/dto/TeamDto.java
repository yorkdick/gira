package com.rayfay.gira.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class TeamDto {
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    private String name;

    @Size(max = 500)
    private String description;

    private Long createdBy;

    private List<TeamMemberDto> members;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}