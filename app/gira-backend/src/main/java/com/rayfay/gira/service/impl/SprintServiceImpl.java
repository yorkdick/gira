package com.rayfay.gira.service.impl;

import com.rayfay.gira.dto.request.CreateSprintRequest;
import com.rayfay.gira.dto.request.UpdateSprintRequest;
import com.rayfay.gira.dto.response.SprintResponse;
import com.rayfay.gira.dto.response.TaskResponse;
import com.rayfay.gira.entity.Board;
import com.rayfay.gira.entity.Sprint;
import com.rayfay.gira.entity.SprintStatus;
import com.rayfay.gira.entity.Task;
import com.rayfay.gira.entity.TaskStatus;
import com.rayfay.gira.entity.User;
import com.rayfay.gira.entity.TaskPriority;
import com.rayfay.gira.exception.ResourceNotFoundException;
import com.rayfay.gira.mapper.SprintMapper;
import com.rayfay.gira.mapper.TaskMapper;
import com.rayfay.gira.repository.SprintRepository;
import com.rayfay.gira.repository.TaskRepository;
import com.rayfay.gira.repository.UserRepository;
import com.rayfay.gira.repository.BoardRepository;
import com.rayfay.gira.service.interfaces.SprintService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SprintServiceImpl implements SprintService {

    private final SprintRepository sprintRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final SprintMapper sprintMapper;
    private final TaskMapper taskMapper;

    @Override
    @Transactional
    public SprintResponse createSprint(CreateSprintRequest request) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }

        if (request.getStartDate().isBefore(LocalDate.now().atStartOfDay().toLocalDate())) {
            throw new IllegalArgumentException("开始日期不能早于今天");
        }

        // if (sprintRepository.existsByStatusAndEndDateAfter(SprintStatus.ACTIVE,
        // request.getStartDate())) {
        // throw new IllegalStateException("已存在活动中的Sprint");
        // }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        Board board = boardRepository.findById(request.getBoardId())
                .orElseThrow(() -> new ResourceNotFoundException("看板不存在"));
        Sprint sprint = Sprint.builder()
                .name(request.getName())
                .board(board)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(SprintStatus.PLANNING)
                .createdBy(currentUser)
                .build();

        return sprintMapper.toResponse(sprintRepository.save(sprint), taskRepository);
    }

    @Override
    public SprintResponse getSprintById(Long id) {
        return sprintMapper.toResponse(getSprintOrThrow(id), taskRepository);
    }

    @Override
    public Page<SprintResponse> getAllSprints(SprintStatus status, Pageable pageable) {
        if (status != null) {
            return sprintRepository.findByStatus(status, pageable)
                    .map(sprint -> sprintMapper.toResponse(sprint, taskRepository));
        }
        return sprintRepository.findAll(pageable)
                .map(sprint -> sprintMapper.toResponse(sprint, taskRepository));
    }

    @Override
    @Transactional
    public SprintResponse startSprint(Long id) {
        Sprint sprint = getSprintOrThrow(id);

        if (sprint.getStatus() != SprintStatus.PLANNING) {
            throw new IllegalStateException("只能启动计划中的Sprint");
        }

        if (sprintRepository.existsByStatusAndEndDateAfter(SprintStatus.ACTIVE, LocalDate.now())) {
            throw new IllegalStateException("已存在活动中的Sprint");
        }

        if (sprint.getStartDate().isBefore(LocalDate.now())) {
            sprint.setStartDate(LocalDate.now());
        }

        sprint.setStatus(SprintStatus.ACTIVE);
        return sprintMapper.toResponse(sprintRepository.save(sprint), taskRepository);
    }

    @Override
    @Transactional
    public SprintResponse completeSprint(Long id) {
        Sprint sprint = getSprintOrThrow(id);

        if (sprint.getStatus() != SprintStatus.ACTIVE) {
            throw new IllegalStateException("只能完成活动中的Sprint");
        }

        List<Task> uncompletedTasks = taskRepository.findBySprintIdAndStatus(sprint.getId(), TaskStatus.IN_PROGRESS);
        if (!uncompletedTasks.isEmpty()) {
            throw new IllegalStateException("Sprint中还有未完成的任务");
        }

        sprint.setStatus(SprintStatus.COMPLETED);
        return sprintMapper.toResponse(sprintRepository.save(sprint), taskRepository);
    }

    @Override
    public List<TaskResponse> getSprintTasks(Long id) {
        Sprint sprint = getSprintOrThrow(id);
        return taskRepository.findBySprintId(sprint.getId(), Pageable.unpaged())
                .stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getSprintTasks(Long id, TaskStatus status, TaskPriority priority) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found"));
        return taskRepository.findBySprintId(sprint.getId(), Pageable.unpaged())
                .stream()
                .filter(task -> (status == null || task.getStatus() == status)
                        && (priority == null || task.getPriority() == priority))
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *") // 每天凌晨执行
    @Transactional
    public void checkAndCompleteExpiredSprints() {
        List<Sprint> expiredSprints = sprintRepository.findByStatusAndEndDateBefore(
                SprintStatus.ACTIVE, LocalDate.now());

        for (Sprint sprint : expiredSprints) {
            sprint.setStatus(SprintStatus.COMPLETED);
            sprintRepository.save(sprint);
        }
    }

    @Override
    @Transactional
    public SprintResponse updateSprint(Long id, UpdateSprintRequest request) {
        Sprint sprint = getSprintOrThrow(id);

        if (sprint.getStatus() != SprintStatus.PLANNING) {
            throw new IllegalStateException("只能修改计划中的Sprint");
        }

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }

        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("开始日期不能早于今天");
        }

        sprint.setName(request.getName());
        sprint.setStartDate(request.getStartDate());
        sprint.setEndDate(request.getEndDate());

        return sprintMapper.toResponse(sprintRepository.save(sprint), taskRepository);
    }

    @Override
    public void cancelSprint(Long id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found"));
        sprint.setStatus(SprintStatus.COMPLETED);
        sprintRepository.save(sprint);
    }

    @Override
    public Page<SprintResponse> getSprintsByBoard(Long boardId, Pageable pageable) {
        if (!boardRepository.existsById(boardId)) {
            throw new ResourceNotFoundException("看板不存在");
        }
        return sprintRepository.findByBoardId(boardId, pageable)
                .map(sprint -> sprintMapper.toResponse(sprint, taskRepository));
    }

    private Sprint getSprintOrThrow(Long id) {
        return sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint不存在"));
    }
}