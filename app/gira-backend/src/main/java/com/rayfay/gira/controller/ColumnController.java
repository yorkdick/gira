package com.rayfay.gira.controller;

import com.rayfay.gira.dto.ColumnDto;
import com.rayfay.gira.service.ColumnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/columns")
@RequiredArgsConstructor
@Tag(name = "Columns", description = "Column management APIs")
public class ColumnController {

    private final ColumnService columnService;

    @Operation(summary = "Create column")
    @PostMapping
    @PreAuthorize("@boardService.hasAccess(#columnDto.boardId)")
    public ResponseEntity<ColumnDto> createColumn(@Valid @RequestBody ColumnDto columnDto) {
        return ResponseEntity.ok(columnService.createColumn(columnDto.getBoardId(), columnDto));
    }

    @Operation(summary = "Get column by ID")
    @GetMapping("/{id}")
    @PreAuthorize("@columnService.hasAccess(#id)")
    public ResponseEntity<ColumnDto> getColumnById(@PathVariable Long id) {
        return ResponseEntity.ok(columnService.getColumnById(id));
    }

    @Operation(summary = "Get columns by board")
    @GetMapping("/board/{boardId}")
    @PreAuthorize("@boardService.hasAccess(#boardId)")
    public ResponseEntity<List<ColumnDto>> getColumnsByBoard(@PathVariable Long boardId) {
        return ResponseEntity.ok(columnService.getColumnsByBoard(boardId));
    }

    @Operation(summary = "Update column")
    @PutMapping("/{id}")
    @PreAuthorize("@columnService.hasAccess(#id)")
    public ResponseEntity<ColumnDto> updateColumn(@PathVariable Long id, @Valid @RequestBody ColumnDto columnDto) {
        return ResponseEntity.ok(columnService.updateColumn(id, columnDto));
    }

    @Operation(summary = "Delete column")
    @DeleteMapping("/{id}")
    @PreAuthorize("@columnService.hasAccess(#id)")
    public ResponseEntity<Void> deleteColumn(@PathVariable Long id) {
        columnService.deleteColumn(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update tasks order in column")
    @PutMapping("/{id}/tasks/order")
    @PreAuthorize("@columnService.hasAccess(#id)")
    public ResponseEntity<ColumnDto> updateTasksOrder(@PathVariable Long id, @RequestBody List<Long> taskIds) {
        return ResponseEntity.ok(columnService.updateTasksOrder(id, taskIds));
    }
}