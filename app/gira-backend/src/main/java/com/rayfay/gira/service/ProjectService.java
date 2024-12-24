package com.rayfay.gira.service;

import com.rayfay.gira.dto.ProjectDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
    ProjectDto createProject(ProjectDto projectDto);

    ProjectDto getProjectById(Long id);

    ProjectDto getProjectByKey(String key);

    Page<ProjectDto> getAllProjects(Pageable pageable);

    Page<ProjectDto> getProjectsByOwner(Long ownerId, Pageable pageable);

    Page<ProjectDto> getProjectsByMember(Long memberId, Pageable pageable);

    ProjectDto updateProject(Long id, ProjectDto projectDto);

    void deleteProject(Long id);

    ProjectDto addMember(Long projectId, Long userId);

    ProjectDto removeMember(Long projectId, Long userId);

    ProjectDto archiveProject(Long id);

    ProjectDto unarchiveProject(Long id);

    boolean hasAccess(Long projectId);

    boolean isProjectManager(Long projectId);

    Page<ProjectDto> getProjectsByUser(Long userId, Pageable pageable);

    ProjectDto addUserToProject(Long projectId, Long userId);

    ProjectDto removeUserFromProject(Long projectId, Long userId);

    ProjectDto setProjectManager(Long projectId, Long userId);
}