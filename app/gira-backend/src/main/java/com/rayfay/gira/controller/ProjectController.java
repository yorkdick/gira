package com.rayfay.gira.controller;

import com.rayfay.gira.dto.ProjectDto;
import com.rayfay.gira.service.ProjectService;
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
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Project management APIs")
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "Create project")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectDto projectDto) {
        return ResponseEntity.ok(projectService.createProject(projectDto));
    }

    @Operation(summary = "Get project by ID")
    @GetMapping("/{id}")
    @PreAuthorize("@projectService.hasAccess(#id)")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @Operation(summary = "Get all projects")
    @GetMapping
    public ResponseEntity<Page<ProjectDto>> getAllProjects(Pageable pageable) {
        return ResponseEntity.ok(projectService.getAllProjects(pageable));
    }

    @Operation(summary = "Get projects by user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ProjectDto>> getProjectsByUser(@PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(projectService.getProjectsByUser(userId, pageable));
    }

    @Operation(summary = "Update project")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @projectService.isProjectManager(#id)")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectDto projectDto) {
        return ResponseEntity.ok(projectService.updateProject(id, projectDto));
    }

    @Operation(summary = "Delete project")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Add user to project")
    @PostMapping("/{id}/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @projectService.isProjectManager(#id)")
    public ResponseEntity<ProjectDto> addUserToProject(@PathVariable Long id, @PathVariable Long userId) {
        return ResponseEntity.ok(projectService.addUserToProject(id, userId));
    }

    @Operation(summary = "Remove user from project")
    @DeleteMapping("/{id}/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @projectService.isProjectManager(#id)")
    public ResponseEntity<ProjectDto> removeUserFromProject(@PathVariable Long id, @PathVariable Long userId) {
        return ResponseEntity.ok(projectService.removeUserFromProject(id, userId));
    }

    @Operation(summary = "Set project manager")
    @PutMapping("/{id}/manager/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProjectDto> setProjectManager(@PathVariable Long id, @PathVariable Long userId) {
        return ResponseEntity.ok(projectService.setProjectManager(id, userId));
    }
}