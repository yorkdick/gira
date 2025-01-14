package com.rayfay.gira.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateBoardRequest {
    @NotBlank(message = "看板名称不能为空")
    private String name;

    private String description;

    @NotEmpty(message = "看板列不能为空")
    private List<@Valid BoardColumnRequest> columns;

    @Data
    public static class BoardColumnRequest {
        @NotBlank(message = "列名不能为空")
        private String name;

        private Integer orderIndex;
    }
}