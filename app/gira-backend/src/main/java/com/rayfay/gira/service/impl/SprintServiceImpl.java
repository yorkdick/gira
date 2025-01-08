package com.rayfay.gira.service.impl;

import com.rayfay.gira.dto.SprintRequest;
import com.rayfay.gira.dto.SprintResponse;
import com.rayfay.gira.entity.Project;
import com.rayfay.gira.entity.Sprint;
import com.rayfay.gira.exception.ResourceNotFoundException;
import com.rayfay.gira.mapper.SprintMapper;
import com.rayfay.gira.repository.ProjectRepository;
import com.rayfay.gira.repository.SprintRepository;
import com.rayfay.gira.security.SecurityUtils;
import com.rayfay.gira.service.SprintService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.OffsetDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SprintServiceImpl implements SprintService {
    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;
    private final SprintMapper sprintMapper;

    @Override
    @Transactional
    public SprintResponse createSprint(SprintRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        Sprint sprint = sprintMapper.toEntity(request);
        sprint.setProject(project);
        sprint.setStatus("PLANNING");
        sprint.setCreatedBy(SecurityUtils.getCurrentUserId().toString());
        sprint.setCreatedAt(OffsetDateTime.now());

        sprint = sprintRepository.save(sprint);
        return sprintMapper.toResponse(sprint);
    }

    @Override
    public Page<SprintResponse> getSprintsByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        Page<Sprint> sprints = sprintRepository.findByProject(project, PageRequest.of(0, 10));
        return sprints.map(sprintMapper::toResponse);
    }

    @Override
    @Transactional
    public SprintResponse updateSprint(Long id, SprintRequest request) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found"));
        log.info("Sprint: {}", sprint);
        if (request.getStatus() != null) {
            // 检查状态值是否合法
            if (!request.getStatus().equals("PLANNING") &&
                    !request.getStatus().equals("ACTIVE") &&
                    !request.getStatus().equals("COMPLETED")) {
                throw new IllegalArgumentException("Invalid sprint status");
            }
            sprint.setStatus(request.getStatus());
        }

        if (request.getName() != null) {
            sprint.setName(request.getName());
        }
        if (request.getGoal() != null) {
            sprint.setGoal(request.getGoal());
        }
        sprint.setUpdatedBy(SecurityUtils.getCurrentUserId().toString());
        sprint.setUpdatedAt(OffsetDateTime.now());
        sprint = sprintRepository.save(sprint);
        return sprintMapper.toResponse(sprint);
    }

    @Override
    @Transactional
    public SprintResponse startSprint(Long id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found"));

        sprint.setStatus("ACTIVE");
        sprint.setUpdatedBy(SecurityUtils.getCurrentUserId().toString());
        sprint.setUpdatedAt(OffsetDateTime.now());
        sprint.setStartDate(OffsetDateTime.now());
        sprint = sprintRepository.save(sprint);
        return sprintMapper.toResponse(sprint);
    }

    @Override
    @Transactional
    public SprintResponse completeSprint(Long id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found"));

        sprint.setStatus("COMPLETED");
        sprint.setUpdatedBy(SecurityUtils.getCurrentUserId().toString());
        sprint.setUpdatedAt(OffsetDateTime.now());
        sprint.setEndDate(OffsetDateTime.now());
        sprint = sprintRepository.save(sprint);
        return sprintMapper.toResponse(sprint);
    }

    @Override
    @Transactional
    public void deleteSprint(Long id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found"));
        sprintRepository.delete(sprint);
    }
}