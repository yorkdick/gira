package com.rayfay.gira.service.impl;

import com.rayfay.gira.dto.LabelDto;
import com.rayfay.gira.entity.Label;
import com.rayfay.gira.entity.Project;
import com.rayfay.gira.repository.LabelRepository;
import com.rayfay.gira.repository.ProjectRepository;
import com.rayfay.gira.service.LabelService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;
    private final ProjectRepository projectRepository;

    @Override
    @Transactional
    public LabelDto createLabel(LabelDto labelDto) {
        Project project = projectRepository.findById(labelDto.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        Label label = new Label();
        label.setName(labelDto.getName());
        label.setColor(labelDto.getColor());
        label.setProject(project);

        label = labelRepository.save(label);
        return mapToDto(label);
    }

    @Override
    public LabelDto getLabelById(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Label not found"));
        return mapToDto(label);
    }

    @Override
    public List<LabelDto> getAllLabels() {
        return labelRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<LabelDto> getLabelsByProjectId(Long projectId) {
        return labelRepository.findAllByProjectId(projectId).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional
    public LabelDto updateLabel(Long id, LabelDto labelDto) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Label not found"));

        if (labelDto.getName() != null) {
            label.setName(labelDto.getName());
        }
        if (labelDto.getColor() != null) {
            label.setColor(labelDto.getColor());
        }

        label = labelRepository.save(label);
        return mapToDto(label);
    }

    @Override
    @Transactional
    public void deleteLabel(Long id) {
        if (!labelRepository.existsById(id)) {
            throw new EntityNotFoundException("Label not found");
        }
        labelRepository.deleteById(id);
    }

    private LabelDto mapToDto(Label label) {
        LabelDto dto = new LabelDto();
        dto.setId(label.getId());
        dto.setName(label.getName());
        dto.setColor(label.getColor());
        dto.setProjectId(label.getProject().getId());
        return dto;
    }
}