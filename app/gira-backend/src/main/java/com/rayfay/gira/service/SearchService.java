package com.rayfay.gira.service;

import com.rayfay.gira.dto.SearchRequest;
import com.rayfay.gira.dto.SearchResult;
import org.springframework.data.domain.Page;
import java.util.List;

public interface SearchService {
    // 快速搜索
    Page<SearchResult> quickSearch(String keyword, int page, int size);

    // 高级搜索
    Page<SearchResult> advancedSearch(SearchRequest request);

    // 保存搜索条件
    void saveSearchFilter(String userId, SearchRequest filter, String filterName);

    // 获取用户保存的搜索条件
    List<SearchRequest> getSavedFilters(String userId);
}