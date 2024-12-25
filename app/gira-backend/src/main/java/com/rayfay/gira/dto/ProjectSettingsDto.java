package com.rayfay.gira.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class ProjectSettingsDto {
    private Long id;

    @NotBlank
    private String projectName;

    @NotBlank
    private String projectKey;

    private String description;

    @NotNull
    private Map<String, String> issueTypes;

    @NotNull
    private Map<String, String> issueStatuses;

    @NotNull
    private Map<String, String> issuePriorities;

    private Map<String, Object> customFields;

    private Map<String, Object> workflowSettings;

    private Map<String, Object> notificationSettings;
}