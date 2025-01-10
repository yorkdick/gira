package com.rayfay.gira.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTaskStatusRequest {
    @NotNull(message = "看板列不能为空")
    private Long columnId;

    private String comment;
}