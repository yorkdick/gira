package com.rayfay.gira.service;

import com.rayfay.gira.dto.ProjectSettingsDto;
import com.rayfay.gira.dto.RoleDto;
import com.rayfay.gira.dto.PermissionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SettingsService {
    ProjectSettingsDto getProjectSettings();

    ProjectSettingsDto updateProjectSettings(ProjectSettingsDto settingsDto);

    RoleDto createRole(RoleDto roleDto);

    Page<RoleDto> getRoles(Pageable pageable);

    RoleDto updateRole(Long roleId, RoleDto roleDto);

    void deleteRole(Long roleId);

    Page<PermissionDto> getPermissions(Pageable pageable);
}