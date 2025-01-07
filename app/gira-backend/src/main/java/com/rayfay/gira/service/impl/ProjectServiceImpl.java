package com.rayfay.gira.service.impl;

import com.rayfay.gira.dto.ProjectDto;
import com.rayfay.gira.entity.Project;
import com.rayfay.gira.entity.User;
import com.rayfay.gira.exception.ResourceNotFoundException;
import com.rayfay.gira.mapper.ProjectMapper;
import com.rayfay.gira.repository.ProjectRepository;
import com.rayfay.gira.repository.UserRepository;
import com.rayfay.gira.service.ProjectService;
import com.rayfay.gira.service.UserService;
import com.rayfay.gira.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;
    private final UserService userService;

    @Override
    @Transactional
    public ProjectDto createProject(ProjectDto projectDto) {
        // Check if project name already exists
        if (projectRepository.findByName(projectDto.getName()).isPresent()) {
            throw new IllegalArgumentException("项目名称已存在");
        }

        Project project = projectMapper.toEntity(projectDto);
        project.setStatus(1); // Set default status

        // Generate project key if not provided
        if (project.getKey() == null || project.getKey().isEmpty()) {
            String key = generateProjectKey(project.getName());
            project.setKey(key);
        }

        // Set current user as project owner
        UserDto currentUser = userService.getCurrentUser();
        User owner = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        project.setOwner(owner);

        // Add owner to members
        project.getMembers().add(owner);

        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    private String generateProjectKey(String projectName) {
        if (projectName == null || projectName.trim().isEmpty()) {
            return "PRJ";
        }
        // Generate key from project name (first 3 letters uppercase)
        String baseKey = projectName.replaceAll("[^a-zA-Z0-9]", "")
                .toUpperCase();

        // If the baseKey is empty after removing non-alphanumeric characters
        if (baseKey.isEmpty()) {
            baseKey = "PRJ";
        } else {
            // Take first 3 characters, or all if less than 3
            baseKey = baseKey.substring(0, Math.min(3, baseKey.length()));
        }

        // Find next available key
        String key = baseKey;
        int suffix = 1;
        while (projectRepository.findByKey(key).isPresent()) {
            key = baseKey + suffix++;
        }
        return key;
    }

    @Override
    public ProjectDto getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        return projectMapper.toDto(project);
    }

    @Override
    public ProjectDto getProjectByKey(String key) {
        Project project = projectRepository.findByKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        return projectMapper.toDto(project);
    }

    @Override
    public Page<ProjectDto> getAllProjects(Pageable pageable) {
        return projectRepository.findAll(pageable).map(projectMapper::toDto);
    }

    @Override
    public Page<ProjectDto> getProjectsByOwner(Long ownerId, Pageable pageable) {
        return projectRepository.findByOwnerId(ownerId, pageable).map(projectMapper::toDto);
    }

    @Override
    public Page<ProjectDto> getProjectsByMember(Long memberId, Pageable pageable) {
        return projectRepository.findByMembersId(memberId, pageable).map(projectMapper::toDto);
    }

    @Override
    @Transactional
    public ProjectDto updateProject(Long id, ProjectDto projectDto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (projectDto.getName() != null) {
            project.setName(projectDto.getName());
        }
        if (projectDto.getDescription() != null) {
            project.setDescription(projectDto.getDescription());
        }
        if (projectDto.getKey() != null) {
            project.setKey(projectDto.getKey());
        }
        if (projectDto.getStatus() > 0) {
            project.setStatus(projectDto.getStatus());
        }

        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        projectRepository.deleteById(id);
        // projectRepository.save(project);
    }

    @Override
    @Transactional
    public ProjectDto archiveProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        project.setStatus(0); // 0 for archived
        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    @Override
    @Transactional
    public ProjectDto unarchiveProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        project.setStatus(1); // 1 for active
        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    @Override
    @Transactional
    public ProjectDto addMember(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        project.getMembers().add(user);
        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    @Override
    @Transactional
    public ProjectDto removeMember(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        project.getMembers().remove(user);
        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    @Override
    public Page<ProjectDto> getProjectsByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return projectRepository.findByMembersContaining(user, pageable)
                .map(projectMapper::toDto);
    }

    @Override
    @Transactional
    public ProjectDto addUserToProject(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        project.getMembers().add(user);
        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    @Override
    @Transactional
    public ProjectDto removeUserFromProject(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        project.getMembers().remove(user);
        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    @Override
    @Transactional
    public ProjectDto setProjectManager(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        project.setOwner(user);
        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    @Override
    public boolean hasAccess(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        UserDto currentUser = userService.getCurrentUser();
        return project.getOwner().getId().equals(currentUser.getId()) ||
                project.getMembers().stream().anyMatch(member -> member.getId().equals(currentUser.getId()));
    }

    @Override
    public boolean isProjectManager(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        UserDto currentUser = userService.getCurrentUser();
        return project.getOwner().getId().equals(currentUser.getId());
    }
}