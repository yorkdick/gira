package com.rayfay.gira.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class UpdateBoardColumnsRequest {
    @NotEmpty(message = "看板列不能为空")
    private List<@Valid BoardColumnRequest> columns;

    @Data
    public static class BoardColumnRequest {
        private Long id;
        private String name;
        private Integer orderIndex;
        private Integer wipLimit;
    }
}