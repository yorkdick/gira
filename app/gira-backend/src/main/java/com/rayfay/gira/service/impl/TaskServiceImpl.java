package com.rayfay.gira.service.impl;

import com.rayfay.gira.dto.request.CreateTaskRequest;
import com.rayfay.gira.dto.request.UpdateTaskRequest;
import com.rayfay.gira.dto.request.UpdateTaskStatusRequest;
import com.rayfay.gira.dto.response.TaskResponse;
import com.rayfay.gira.entity.*;
import com.rayfay.gira.exception.ResourceNotFoundException;
import com.rayfay.gira.mapper.TaskMapper;
import com.rayfay.gira.repository.BoardColumnRepository;
import com.rayfay.gira.repository.BoardRepository;
import com.rayfay.gira.repository.SprintRepository;
import com.rayfay.gira.repository.TaskRepository;
import com.rayfay.gira.repository.UserRepository;
import com.rayfay.gira.service.interfaces.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final SprintRepository sprintRepository;
    private final BoardRepository boardRepository;
    private final BoardColumnRepository boardColumnRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Override
    @Transactional
    public TaskResponse createTask(CreateTaskRequest request) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        Board board = boardRepository.findById(request.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("看板不存在"));

        BoardColumn column = boardColumnRepository.findById(request.getColumnId())
                .orElseThrow(() -> new ResourceNotFoundException("看板列不存在"));

        if (!column.getBoard().getId().equals(board.getId())) {
            throw new IllegalArgumentException("看板列不属于指定的看板");
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .column(column)
                .board(board)
                .reporter(currentUser)
                .priority(request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM)
                .status(TaskStatus.TODO)
                .build();

        if (request.getSprintId() != null) {
            Sprint sprint = sprintRepository.findById(request.getSprintId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sprint不存在"));
            if (sprint.getStatus() != SprintStatus.PLANNING && sprint.getStatus() != SprintStatus.ACTIVE) {
                throw new IllegalStateException("只能将任务添加到计划中或活动中的Sprint");
            }
            task.setSprint(sprint);
        }

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("指派人不存在"));
            task.setAssignee(assignee);
        }

        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Long id, UpdateTaskRequest request) {
        Task task = getTaskOrThrow(id);

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getStatus() != null) {
            // 检查状态转换是否合法
            if (task.getStatus() == TaskStatus.TODO && request.getStatus() == TaskStatus.DONE) {
                throw new IllegalStateException("无效的状态变更");
            }
            task.setStatus(request.getStatus());
        }
        if (request.getColumnId() != null) {
            BoardColumn column = boardColumnRepository.findById(request.getColumnId())
                    .orElseThrow(() -> new ResourceNotFoundException("看板列不存在"));

            task.setColumn(column);
        }

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("指派人不存在"));
            task.setAssignee(assignee);
        }

        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    @Transactional
    public TaskResponse updateTaskStatus(Long id, UpdateTaskStatusRequest request) {
        Task task = getTaskOrThrow(id);
        BoardColumn column = boardColumnRepository.findById(request.getColumnId())
                .orElseThrow(() -> new ResourceNotFoundException("看板列不存在"));

        task.setColumn(column);
        if (column.getName().equalsIgnoreCase("Done") ||
                column.getName().equalsIgnoreCase("完成")) {
            task.setStatus(TaskStatus.DONE);
        } else {
            task.setStatus(TaskStatus.IN_PROGRESS);
        }

        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    public TaskResponse getTaskById(Long id) {
        return taskMapper.toResponse(getTaskOrThrow(id));
    }

    @Override
    public Page<TaskResponse> getAllTasks(Long sprintId, Long assigneeId, Pageable pageable) {
        if (sprintId != null) {
            return taskRepository.findBySprintId(sprintId, pageable)
                    .map(taskMapper::toResponse);
        }
        if (assigneeId != null) {
            return taskRepository.findByAssigneeId(assigneeId, pageable)
                    .map(taskMapper::toResponse);
        }
        return taskRepository.findAll(pageable)
                .map(taskMapper::toResponse);
    }

    @Override
    public Page<TaskResponse> getBacklogTasks(Pageable pageable) {
        return taskRepository.findBySprintIdIsNull(pageable)
                .map(taskMapper::toResponse);
    }

    @Override
    @Transactional
    public TaskResponse moveTaskToSprint(Long id, Long sprintId) {
        Task task = getTaskOrThrow(id);

        if (sprintId == null) {
            task.setSprint(null);
        } else {
            Sprint sprint = sprintRepository.findById(sprintId)
                    .orElseThrow(() -> new ResourceNotFoundException("Sprint不存在"));

            if (sprint.getStatus() != SprintStatus.PLANNING && sprint.getStatus() != SprintStatus.ACTIVE) {
                throw new IllegalStateException("只能将任务移动到计划中或活动中的Sprint");
            }

            task.setSprint(sprint);
        }

        return taskMapper.toResponse(taskRepository.save(task));
    }

    private Task getTaskOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("任务不存在"));
    }

    @Override
    public Page<TaskResponse> getTasksByBoard(Long boardId, Pageable pageable) {
        return taskRepository.findByColumnBoardId(boardId, pageable)
                .map(taskMapper::toResponse);
    }

    @Override
    public Page<TaskResponse> getTasksByAssignee(Long assigneeId, Pageable pageable) {
        return taskRepository.findByAssigneeId(assigneeId, pageable)
                .map(taskMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        Task task = getTaskOrThrow(id);
        taskRepository.delete(task);
    }
}