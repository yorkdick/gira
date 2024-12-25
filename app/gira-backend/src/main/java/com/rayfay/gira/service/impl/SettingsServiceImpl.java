package com.rayfay.gira.service.impl;

import com.rayfay.gira.dto.ProjectSettingsDto;
import com.rayfay.gira.dto.RoleDto;
import com.rayfay.gira.dto.PermissionDto;
import com.rayfay.gira.entity.ProjectSettings;
import com.rayfay.gira.entity.Role;
import com.rayfay.gira.exception.ResourceNotFoundException;
import com.rayfay.gira.mapper.ProjectSettingsMapper;
import com.rayfay.gira.mapper.RoleMapper;
import com.rayfay.gira.mapper.PermissionMapper;
import com.rayfay.gira.repository.ProjectSettingsRepository;
import com.rayfay.gira.repository.RoleRepository;
import com.rayfay.gira.repository.PermissionRepository;
import com.rayfay.gira.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettingsServiceImpl implements SettingsService {

    private final ProjectSettingsRepository projectSettingsRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final ProjectSettingsMapper projectSettingsMapper;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    @Override
    @Cacheable(value = "projectSettings")
    public ProjectSettingsDto getProjectSettings() {
        ProjectSettings settings = projectSettingsRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Project settings not found"));
        return projectSettingsMapper.toDto(settings);
    }

    @Override
    @Transactional
    @CacheEvict(value = "projectSettings", allEntries = true)
    public ProjectSettingsDto updateProjectSettings(ProjectSettingsDto settingsDto) {
        ProjectSettings settings = projectSettingsRepository.findAll()
                .stream()
                .findFirst()
                .orElse(new ProjectSettings());

        projectSettingsMapper.updateEntity(settingsDto, settings);
        settings = projectSettingsRepository.save(settings);
        return projectSettingsMapper.toDto(settings);
    }

    @Override
    @Transactional
    public RoleDto createRole(RoleDto roleDto) {
        Role role = roleMapper.toEntity(roleDto);
        role = roleRepository.save(role);
        return roleMapper.toDto(role);
    }

    @Override
    @Cacheable(value = "roles")
    public Page<RoleDto> getRoles(Pageable pageable) {
        return roleRepository.findAll(pageable)
                .map(roleMapper::toDto);
    }

    @Override
    @Transactional
    @CacheEvict(value = "roles", allEntries = true)
    public RoleDto updateRole(Long roleId, RoleDto roleDto) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        roleMapper.updateEntity(roleDto, role);
        role = roleRepository.save(role);
        return roleMapper.toDto(role);
    }

    @Override
    @Transactional
    @CacheEvict(value = "roles", allEntries = true)
    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        if (role.isSystem()) {
            throw new IllegalStateException("System roles cannot be deleted");
        }
        roleRepository.delete(role);
    }

    @Override
    @Cacheable(value = "permissions")
    public Page<PermissionDto> getPermissions(Pageable pageable) {
        return permissionRepository.findAll(pageable)
                .map(permissionMapper::toDto);
    }
}