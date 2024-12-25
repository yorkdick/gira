package com.rayfay.gira.controller;

import com.rayfay.gira.dto.SearchRequest;
import com.rayfay.gira.dto.SearchResult;
import com.rayfay.gira.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/quick")
    public ResponseEntity<Page<SearchResult>> quickSearch(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(searchService.quickSearch(keyword, page, size));
    }

    @PostMapping("/advanced")
    public ResponseEntity<Page<SearchResult>> advancedSearch(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(searchService.advancedSearch(request));
    }

    @PostMapping("/filters")
    public ResponseEntity<Void> saveSearchFilter(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SearchRequest filter,
            @RequestParam String filterName) {
        searchService.saveSearchFilter(userDetails.getUsername(), filter, filterName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/filters")
    public ResponseEntity<List<SearchRequest>> getSavedFilters(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(searchService.getSavedFilters(userDetails.getUsername()));
    }
}