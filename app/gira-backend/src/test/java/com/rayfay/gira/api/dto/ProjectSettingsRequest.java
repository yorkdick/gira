package com.rayfay.gira.api.dto;

import lombok.Data;

@Data
public class ProjectSettingsRequest {
    private Boolean allowGuestAccess;
    private Boolean requireApprovalForJoin;
    private String defaultRole; // 新成员的默认角色
    private String workflowType; // SIMPLE, SCRUM, KANBAN
}