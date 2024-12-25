package com.rayfay.gira.mapper;

import com.rayfay.gira.dto.TaskDto;
import com.rayfay.gira.entity.Task;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring", uses = { LabelMapper.class, CommentMapper.class, AttachmentMapper.class })
public interface TaskMapper {

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectName", source = "project.name")
    @Mapping(target = "columnId", source = "column.id")
    @Mapping(target = "columnName", source = "column.name")
    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "assigneeName", source = "assignee.username")
    @Mapping(target = "reporterId", source = "reporter.id")
    @Mapping(target = "reporterName", source = "reporter.username")
    @Mapping(target = "priority", expression = "java(task.getPriority().name())")
    @Mapping(target = "status", expression = "java(task.getStatus().name())")
    @Mapping(target = "type", expression = "java(task.getType().name())")
    TaskDto toDto(Task task);

    List<TaskDto> toDto(List<Task> tasks);

    @InheritInverseConfiguration
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "column", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "reporter", ignore = true)
    @Mapping(target = "priority", expression = "java(TaskPriority.valueOf(dto.getPriority()))")
    @Mapping(target = "status", expression = "java(TaskStatus.valueOf(dto.getStatus()))")
    @Mapping(target = "type", expression = "java(TaskType.valueOf(dto.getType()))")
    Task toEntity(TaskDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "column", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "reporter", ignore = true)
    @Mapping(target = "priority", expression = "java(dto.getPriority() != null ? TaskPriority.valueOf(dto.getPriority()) : null)")
    @Mapping(target = "status", expression = "java(dto.getStatus() != null ? TaskStatus.valueOf(dto.getStatus()) : null)")
    @Mapping(target = "type", expression = "java(dto.getType() != null ? TaskType.valueOf(dto.getType()) : null)")
    void updateEntity(TaskDto dto, @MappingTarget Task task);
}