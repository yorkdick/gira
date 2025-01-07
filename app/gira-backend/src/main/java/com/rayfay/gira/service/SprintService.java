package com.rayfay.gira.service;

import com.rayfay.gira.dto.SprintRequest;
import com.rayfay.gira.dto.SprintResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SprintService {
    SprintResponse createSprint(SprintRequest request);

    Page<SprintResponse> getSprintsByProject(Long projectId);

    SprintResponse updateSprint(Long id, SprintRequest request);

    SprintResponse startSprint(Long id);

    SprintResponse completeSprint(Long id);
}