package com.rayfay.gira.service.impl;

import com.rayfay.gira.dto.SprintRequest;
import com.rayfay.gira.dto.SprintResponse;
import com.rayfay.gira.entity.Project;
import com.rayfay.gira.entity.Sprint;
import com.rayfay.gira.exception.ResourceNotFoundException;
import com.rayfay.gira.mapper.SprintMapper;
import com.rayfay.gira.repository.ProjectRepository;
import com.rayfay.gira.repository.SprintRepository;
import com.rayfay.gira.service.SprintService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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

        sprint = sprintRepository.save(sprint);
        return sprintMapper.toResponse(sprint);
    }

    @Override
    public Page<SprintResponse> getSprintsByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        Page<Sprint> sprints = sprintRepository.findByProject(project, Pageable.unpaged());
        return sprints.map(sprintMapper::toResponse);
    }

    @Override
    @Transactional
    public SprintResponse updateSprint(Long id, SprintRequest request) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found"));

        sprintMapper.updateEntity(request, sprint);
        sprint = sprintRepository.save(sprint);
        return sprintMapper.toResponse(sprint);
    }

    @Override
    @Transactional
    public SprintResponse startSprint(Long id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found"));

        sprint.setStatus("ACTIVE");
        sprint = sprintRepository.save(sprint);
        return sprintMapper.toResponse(sprint);
    }

    @Override
    @Transactional
    public SprintResponse completeSprint(Long id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found"));

        sprint.setStatus("COMPLETED");
        sprint = sprintRepository.save(sprint);
        return sprintMapper.toResponse(sprint);
    }
}