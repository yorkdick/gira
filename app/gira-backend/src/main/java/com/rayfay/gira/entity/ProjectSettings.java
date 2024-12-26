package com.rayfay.gira.entity;

import com.rayfay.gira.converter.JsonMapConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Entity
@Table(name = "project_settings")
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @Convert(converter = JsonMapConverter.class)
    private Map<String, String> issueTypes;

    @Column(name = "issue_statuses", nullable = false, columnDefinition = "jsonb")
    @Convert(converter = JsonMapConverter.class)
    private Map<String, String> issueStatuses;

    @Column(name = "issue_priorities", nullable = false, columnDefinition = "jsonb")
    @Convert(converter = JsonMapConverter.class)
    private Map<String, String> issuePriorities;

    @Column(name = "custom_fields", columnDefinition = "jsonb")
    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> customFields;

    @Column(name = "workflow_settings", columnDefinition = "jsonb")
    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> workflowSettings;

    @Column(name = "notification_settings", columnDefinition = "jsonb")
    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> notificationSettings;
}