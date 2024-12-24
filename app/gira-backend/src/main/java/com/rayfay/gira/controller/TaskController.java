package com.rayfay.gira.controller;

import com.rayfay.gira.dto.TaskDto;
import com.rayfay.gira.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Tag(name = "Task", description = "Task management APIs")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Create a new task")
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskDto taskDto) {
        return ResponseEntity.ok(taskService.createTask(taskDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a task by ID")
    public ResponseEntity<TaskDto> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping
    @Operation(summary = "Get all tasks")
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/column/{columnId}")
    @Operation(summary = "Get tasks by column ID")
    public ResponseEntity<List<TaskDto>> getTasksByColumnId(@PathVariable Long columnId) {
        return ResponseEntity.ok(taskService.getTasksByColumnId(columnId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id, @Valid @RequestBody TaskDto taskDto) {
        return ResponseEntity.ok(taskService.updateTask(id, taskDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/move")
    @Operation(summary = "Move a task to a different column")
    public ResponseEntity<TaskDto> moveTask(
            @PathVariable Long id,
            @RequestParam Long targetColumnId,
            @RequestParam(required = false) Integer position) {
        return ResponseEntity.ok(taskService.moveTask(id, targetColumnId, position));
    }

    @PutMapping("/{id}/assign")
    @Operation(summary = "Assign a task to a user")
    public ResponseEntity<TaskDto> assignTask(
            @PathVariable Long id,
            @RequestParam Long userId) {
        return ResponseEntity.ok(taskService.assignTask(id, userId));
    }

    @PutMapping("/{id}/labels")
    @Operation(summary = "Update task labels")
    public ResponseEntity<TaskDto> updateTaskLabels(
            @PathVariable Long id,
            @RequestBody List<Long> labelIds) {
        return ResponseEntity.ok(taskService.updateTaskLabels(id, labelIds));
    }
}