package com.rayfay.gira.api.dto;

import lombok.Data;

@Data
public class ProjectMemberRequest {
    private Long userId;
    private String role; // OWNER, MANAGER, MEMBER
}