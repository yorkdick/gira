package com.rayfay.gira.service;

import com.rayfay.gira.dto.LabelDto;
import java.util.List;

public interface LabelService {
    LabelDto createLabel(LabelDto labelDto);

    LabelDto getLabelById(Long id);

    List<LabelDto> getAllLabels();

    List<LabelDto> getLabelsByProjectId(Long projectId);

    LabelDto updateLabel(Long id, LabelDto labelDto);

    void deleteLabel(Long id);
}