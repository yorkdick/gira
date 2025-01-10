package com.rayfay.gira.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Future;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateSprintRequest {
    @NotNull(message = "看板ID不能为空")
    private Long boardId;

    @NotBlank(message = "Sprint名称不能为空")
    private String name;

    @NotNull(message = "开始日期不能为空")
    // @FutureOrPresent(message = "开始日期不能早于今天")
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    // @Future(message = "结束日期必须是将来的日期")
    private LocalDate endDate;
}