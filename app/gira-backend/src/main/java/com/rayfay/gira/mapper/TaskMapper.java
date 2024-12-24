package com.rayfay.gira.mapper;

import com.rayfay.gira.dto.TaskDto;
import com.rayfay.gira.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper extends BaseMapper<Task, TaskDto> {
}