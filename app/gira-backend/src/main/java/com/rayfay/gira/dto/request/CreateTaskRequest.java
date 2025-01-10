package com.rayfay.gira.dto.request;

import com.rayfay.gira.entity.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTaskRequest {
    @NotBlank(message = "任务标题不能为空")
    private String title;

    private String description;

    @NotNull(message = "看板ID不能为空")
    private Long boardId;

    private Long sprintId;

    @NotNull(message = "看板列不能为空")
    private Long columnId;

    private Long assigneeId;

    private TaskPriority priority;
}