package com.rayfay.gira.service;

import com.rayfay.gira.dto.TaskDto;
import java.util.List;

public interface TaskService {
    TaskDto createTask(TaskDto taskDto);

    TaskDto getTaskById(Long id);

    List<TaskDto> getAllTasks();

    List<TaskDto> getTasksByColumnId(Long columnId);

    TaskDto updateTask(Long id, TaskDto taskDto);

    void deleteTask(Long id);

    TaskDto moveTask(Long id, Long targetColumnId, Integer position);

    TaskDto assignTask(Long id, Long userId);

    TaskDto updateTaskLabels(Long id, List<Long> labelIds);
}