package com.rayfay.gira.api.dto;

import lombok.Data;

@Data
public class ProjectRequest {
    private String name;
    private String key;
    private String description;
    private Long leaderId;
    private String type;
    private String template;
}