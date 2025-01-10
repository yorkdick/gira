package com.rayfay.gira.controller;

import com.rayfay.gira.dto.request.CreateTaskRequest;
import com.rayfay.gira.dto.request.UpdateTaskRequest;
import com.rayfay.gira.dto.request.UpdateTaskStatusRequest;
import com.rayfay.gira.dto.response.TaskResponse;
import com.rayfay.gira.service.interfaces.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        return ResponseEntity.ok(taskService.createTask(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskStatusRequest request) {
        return ResponseEntity.ok(taskService.updateTaskStatus(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getAllTasks(
            @RequestParam(required = false) Long sprintId,
            @RequestParam(required = false) Long assigneeId,
            Pageable pageable) {
        return ResponseEntity.ok(taskService.getAllTasks(sprintId, assigneeId, pageable));
    }

    @GetMapping("/backlog")
    public ResponseEntity<Page<TaskResponse>> getBacklogTasks(Pageable pageable) {
        return ResponseEntity.ok(taskService.getBacklogTasks(pageable));
    }

    @PutMapping("/{id}/sprint")
    public ResponseEntity<TaskResponse> moveTaskToSprint(
            @PathVariable Long id,
            @RequestParam(required = false) Long sprintId) {
        return ResponseEntity.ok(taskService.moveTaskToSprint(id, sprintId));
    }

    @GetMapping("/boards/{boardId}")
    public ResponseEntity<Page<TaskResponse>> getTasksByBoard(
            @PathVariable Long boardId,
            Pageable pageable) {
        return ResponseEntity.ok(taskService.getTasksByBoard(boardId, pageable));
    }

    @GetMapping("/assignee/{assigneeId}")
    public ResponseEntity<Page<TaskResponse>> getTasksByAssignee(
            @PathVariable Long assigneeId,
            Pageable pageable) {
        return ResponseEntity.ok(taskService.getTasksByAssignee(assigneeId, pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }
}