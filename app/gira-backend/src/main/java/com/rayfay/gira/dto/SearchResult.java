package com.rayfay.gira.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class SearchResult {
    private String id;
    private String type; // 结果类型：TASK, ISSUE, COMMENT等
    private String title; // 标题
    private String description; // 描述
    private String status; // 状态
    private String priority; // 优先级
    private String assignee; // 指派人
    private String creator; // 创建人
    private String createdAt; // 创建时间
    private String updatedAt; // 更新时间
    private String projectId; // 所属项目ID
    private String projectName; // 所属项目名称
    private String url; // 资源URL
}