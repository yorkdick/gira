package com.rayfay.gira.controller;

import com.rayfay.gira.dto.ProjectSettingsDto;
import com.rayfay.gira.dto.RoleDto;
import com.rayfay.gira.dto.PermissionDto;
import com.rayfay.gira.service.SettingsService;
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
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
@Tag(name = "Settings", description = "System settings management APIs")
public class SettingsController {

    private final SettingsService settingsService;

    @Operation(summary = "Get project settings")
    @GetMapping("/project")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ProjectSettingsDto> getProjectSettings() {
        return ResponseEntity.ok(settingsService.getProjectSettings());
    }

    @Operation(summary = "Update project settings")
    @PutMapping("/project")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProjectSettingsDto> updateProjectSettings(
            @Valid @RequestBody ProjectSettingsDto settingsDto) {
        return ResponseEntity.ok(settingsService.updateProjectSettings(settingsDto));
    }

    @Operation(summary = "Create role")
    @PostMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleDto> createRole(@Valid @RequestBody RoleDto roleDto) {
        return ResponseEntity.ok(settingsService.createRole(roleDto));
    }

    @Operation(summary = "Get roles")
    @GetMapping("/roles")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<RoleDto>> getRoles(Pageable pageable) {
        return ResponseEntity.ok(settingsService.getRoles(pageable));
    }

    @Operation(summary = "Update role")
    @PutMapping("/roles/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleDto> updateRole(
            @PathVariable Long roleId,
            @Valid @RequestBody RoleDto roleDto) {
        return ResponseEntity.ok(settingsService.updateRole(roleId, roleDto));
    }

    @Operation(summary = "Delete role")
    @DeleteMapping("/roles/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRole(@PathVariable Long roleId) {
        settingsService.deleteRole(roleId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get permissions")
    @GetMapping("/permissions")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<PermissionDto>> getPermissions(Pageable pageable) {
        return ResponseEntity.ok(settingsService.getPermissions(pageable));
    }
}