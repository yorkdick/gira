package com.rayfay.gira.api.dto;

import lombok.Data;

@Data
public class BoardRequest {
    private String name;
    private String description;
    private Long projectId;
}