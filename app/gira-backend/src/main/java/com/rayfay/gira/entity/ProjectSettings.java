package com.rayfay.gira.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.util.Map;

@Data
@Entity
@Table(name = "project_settings")
public class ProjectSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "project_key", nullable = false, unique = true)
    private String projectKey;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "issue_types", nullable = false, columnDefinition = "jsonb")
    private Map<String, String> issueTypes;

    @Column(name = "issue_statuses", nullable = false, columnDefinition = "jsonb")
    private Map<String, String> issueStatuses;

    @Column(name = "issue_priorities", nullable = false, columnDefinition = "jsonb")
    private Map<String, String> issuePriorities;

    @Column(name = "custom_fields", columnDefinition = "jsonb")
    private Map<String, Object> customFields;

    @Column(name = "workflow_settings", columnDefinition = "jsonb")
    private Map<String, Object> workflowSettings;

    @Column(name = "notification_settings", columnDefinition = "jsonb")
    private Map<String, Object> notificationSettings;
}