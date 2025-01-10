package com.rayfay.gira.controller;

import com.rayfay.gira.dto.request.CreateBoardRequest;
import com.rayfay.gira.dto.request.UpdateBoardColumnsRequest;
import com.rayfay.gira.dto.request.UpdateBoardRequest;
import com.rayfay.gira.dto.response.BoardResponse;
import com.rayfay.gira.dto.response.TaskResponse;
import com.rayfay.gira.entity.BoardStatus;
import com.rayfay.gira.service.interfaces.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<BoardResponse> createBoard(@Valid @RequestBody CreateBoardRequest request) {
        return ResponseEntity.ok(boardService.createBoard(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<BoardResponse> updateBoard(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBoardRequest request) {
        return ResponseEntity.ok(boardService.updateBoard(id, request));
    }

    @PutMapping("/{id}/columns")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<BoardResponse> updateBoardColumns(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBoardColumnsRequest request) {
        return ResponseEntity.ok(boardService.updateBoardColumns(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> getBoard(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getBoardById(id));
    }

    @GetMapping
    public ResponseEntity<Page<BoardResponse>> getAllBoards(
            @RequestParam(required = false, defaultValue = "ACTIVE") BoardStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(boardService.getAllBoards(status, pageable));
    }

    @PutMapping("/{id}/archive")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> archiveBoard(@PathVariable Long id) {
        boardService.archiveBoard(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<TaskResponse>> getBoardTasks(
            @PathVariable Long id,
            @RequestParam(required = false) Long columnId) {
        return ResponseEntity.ok(boardService.getBoardTasks(id, columnId));
    }
}