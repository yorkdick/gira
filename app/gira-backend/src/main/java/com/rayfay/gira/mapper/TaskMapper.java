package com.rayfay.gira.mapper;

import com.rayfay.gira.dto.response.TaskResponse;
import com.rayfay.gira.entity.Task;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { UserMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    @Mapping(target = "sprintId", source = "sprint.id")
    @Mapping(target = "sprintName", source = "sprint.name")
    @Mapping(target = "columnId", source = "column.id")
    @Mapping(target = "columnName", source = "column.name")
    TaskResponse toResponse(Task task);
}