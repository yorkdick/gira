package com.rayfay.gira.service.impl;

import com.rayfay.gira.dto.SearchRequest;
import com.rayfay.gira.dto.SearchResult;
import com.rayfay.gira.service.SearchService;
import com.rayfay.gira.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final TaskRepository taskRepository;

    @Override
    public Page<SearchResult> quickSearch(String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        // 并行搜索不同类型的内容
        List<SearchResult> results = Stream.of(
                searchTasks(keyword),
                searchIssues(keyword),
                searchComments(keyword))
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(SearchResult::getUpdatedAt).reversed())
                .collect(Collectors.toList());

        // 分页处理
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), results.size());

        return new PageImpl<>(
                results.subList(start, end),
                pageRequest,
                results.size());
    }

    @Override
    public Page<SearchResult> advancedSearch(SearchRequest request) {
        PageRequest pageRequest = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(Sort.Direction.fromString(
                        request.getSortDirection() != null ? request.getSortDirection() : "DESC"),
                        request.getSortBy() != null ? request.getSortBy() : "updatedAt"));

        List<SearchResult> results = new ArrayList<>();

        // 根据类型进行搜索
        if (request.getTypes() == null || request.getTypes().isEmpty()) {
            results.addAll(searchTasks(request));
            results.addAll(searchIssues(request));
            results.addAll(searchComments(request));
        } else {
            if (request.getTypes().contains("TASK")) {
                results.addAll(searchTasks(request));
            }
            if (request.getTypes().contains("ISSUE")) {
                results.addAll(searchIssues(request));
            }
            if (request.getTypes().contains("COMMENT")) {
                results.addAll(searchComments(request));
            }
        }

        // 分页处理
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), results.size());

        return new PageImpl<>(
                results.subList(start, end),
                pageRequest,
                results.size());
    }

    @Override
    public void saveSearchFilter(String userId, SearchRequest filter, String filterName) {
        // TODO: 实现保存搜索条件的逻辑
    }

    @Override
    public List<SearchRequest> getSavedFilters(String userId) {
        // TODO: 实现获取保存的搜索条件的逻辑
        return new ArrayList<>();
    }

    // 私有辅助方法
    private List<SearchResult> searchTasks(String keyword) {
        return taskRepository.findByTitleContainingOrDescriptionContaining(keyword, keyword)
                .stream()
                .<SearchResult>map(task -> SearchResult.builder()
                        .id(task.getId().toString())
                        .type("TASK")
                        .title(task.getTitle())
                        .description(task.getDescription())
                        .status(task.getStatus().toString())
                        .priority(task.getPriority().toString())
                        .assignee(task.getAssignee() != null ? task.getAssignee().getUsername() : null)
                        .creator(task.getCreatedBy())
                        .createdAt(task.getCreatedAt().toString())
                        .updatedAt(task.getUpdatedAt().toString())
                        .projectId(task.getProject().getId().toString())
                        .projectName(task.getProject().getName())
                        .url("/tasks/" + task.getId())
                        .build())
                .collect(Collectors.toList());
    }

    private List<SearchResult> searchTasks(SearchRequest request) {
        // TODO: 实现基于高级搜索条件的任务搜索
        return new ArrayList<>();
    }

    private List<SearchResult> searchIssues(String keyword) {
        // TODO: 实现Issue搜索
        return new ArrayList<>();
    }

    private List<SearchResult> searchIssues(SearchRequest request) {
        // TODO: 实现基于高级搜索条件的Issue搜索
        return new ArrayList<>();
    }

    private List<SearchResult> searchComments(String keyword) {
        // TODO: 实现评论搜索
        return new ArrayList<>();
    }

    private List<SearchResult> searchComments(SearchRequest request) {
        // TODO: 实现基于高级搜索条件的评论搜索
        return new ArrayList<>();
    }
}