package com.rayfay.gira.dto;

import lombok.Data;
import java.util.List;

@Data
public class SearchRequest {
    // 快速搜索关键词
    private String keyword;

    // 高级搜索条件
    private List<String> types; // 搜索类型：TASK, ISSUE, COMMENT等
    private String status; // 状态
    private String priority; // 优先级
    private String assignee; // 指派人
    private String creator; // 创建人
    private String label; // 标签
    private String dateFrom; // 起始日期
    private String dateTo; // 结束日期

    // 分页参数
    private int page = 0;
    private int size = 20;
    private String sortBy;
    private String sortDirection;
}