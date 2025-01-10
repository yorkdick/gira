package com.rayfay.gira.mapper;

import com.rayfay.gira.dto.response.SprintResponse;
import com.rayfay.gira.entity.Sprint;
import com.rayfay.gira.entity.TaskStatus;
import com.rayfay.gira.repository.TaskRepository;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { UserMapper.class }, imports = {
        TaskStatus.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SprintMapper {

    @Mapping(target = "totalTasks", expression = "java(taskRepository.countBySprintId(sprint.getId()))")
    @Mapping(target = "completedTasks", expression = "java(taskRepository.countBySprintIdAndStatus(sprint.getId(), TaskStatus.DONE))")
    SprintResponse toResponse(Sprint sprint, @Context TaskRepository taskRepository);
}