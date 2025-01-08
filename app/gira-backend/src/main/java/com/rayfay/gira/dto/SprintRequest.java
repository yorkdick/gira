package com.rayfay.gira.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SprintRequest {
    @NotBlank(message = "Sprint名称不能为空")
    private String name;

    private String goal;

    private String status;

    @NotNull(message = "项目ID不能为空")
    private Long projectId;
}