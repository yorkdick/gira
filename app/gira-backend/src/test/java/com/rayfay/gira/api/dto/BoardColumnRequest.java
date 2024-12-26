package com.rayfay.gira.api.dto;

import lombok.Data;

@Data
public class BoardColumnRequest {
    private String name;
    private Integer position;
    private Long boardId;
}