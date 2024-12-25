package com.rayfay.gira.controller;

import com.rayfay.gira.dto.IssueDto;
import com.rayfay.gira.service.BacklogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/backlog")
@RequiredArgsConstructor
@Tag(name = "Backlog", description = "Backlog management APIs")
public class BacklogController {

    private final BacklogService backlogService;

    @Operation(summary = "Create issue")
    @PostMapping("/issues")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<IssueDto> createIssue(@Valid @RequestBody IssueDto issueDto) {
        return ResponseEntity.ok(backlogService.createIssue(issueDto));
    }

    @Operation(summary = "Get issues")
    @GetMapping("/issues")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<IssueDto>> getIssues(
            @RequestParam(required = false) String filter,
            Pageable pageable) {
        return ResponseEntity.ok(backlogService.getIssues(filter, pageable));
    }

    @Operation(summary = "Get issue by ID")
    @GetMapping("/issues/{issueId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<IssueDto> getIssueById(@PathVariable Long issueId) {
        return ResponseEntity.ok(backlogService.getIssueById(issueId));
    }

    @Operation(summary = "Update issue")
    @PutMapping("/issues/{issueId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<IssueDto> updateIssue(
            @PathVariable Long issueId,
            @Valid @RequestBody IssueDto issueDto) {
        return ResponseEntity.ok(backlogService.updateIssue(issueId, issueDto));
    }

    @Operation(summary = "Delete issue")
    @DeleteMapping("/issues/{issueId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteIssue(@PathVariable Long issueId) {
        backlogService.deleteIssue(issueId);
        return ResponseEntity.ok().build();
    }
}