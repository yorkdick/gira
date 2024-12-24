package com.rayfay.gira.controller;

import com.rayfay.gira.dto.BoardDto;
import com.rayfay.gira.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/boards")
@RequiredArgsConstructor
@Tag(name = "Boards", description = "Board management APIs")
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "Create board")
    @PostMapping
    @PreAuthorize("@projectService.hasAccess(#projectId)")
    public ResponseEntity<BoardDto> createBoard(
            @RequestParam Long projectId,
            @Valid @RequestBody BoardDto boardDto) {
        return ResponseEntity.ok(boardService.createBoard(projectId, boardDto));
    }

    @Operation(summary = "Get board by ID")
    @GetMapping("/{id}")
    @PreAuthorize("@boardService.hasAccess(#id)")
    public ResponseEntity<BoardDto> getBoardById(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getBoardById(id));
    }

    @Operation(summary = "Get boards by project")
    @GetMapping("/project/{projectId}")
    @PreAuthorize("@projectService.hasAccess(#projectId)")
    public ResponseEntity<Page<BoardDto>> getBoardsByProject(@PathVariable Long projectId, Pageable pageable) {
        return ResponseEntity.ok(boardService.getBoardsByProject(projectId, pageable));
    }

    @Operation(summary = "Update board")
    @PutMapping("/{id}")
    @PreAuthorize("@boardService.hasAccess(#id)")
    public ResponseEntity<BoardDto> updateBoard(@PathVariable Long id, @Valid @RequestBody BoardDto boardDto) {
        return ResponseEntity.ok(boardService.updateBoard(id, boardDto));
    }

    @Operation(summary = "Delete board")
    @DeleteMapping("/{id}")
    @PreAuthorize("@boardService.hasAccess(#id)")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update board columns order")
    @PutMapping("/{id}/columns/order")
    @PreAuthorize("@boardService.hasAccess(#id)")
    public ResponseEntity<BoardDto> updateColumnsOrder(@PathVariable Long id, @RequestBody List<Long> columnIds) {
        return ResponseEntity.ok(boardService.updateBoardColumnsOrder(id, columnIds));
    }
}