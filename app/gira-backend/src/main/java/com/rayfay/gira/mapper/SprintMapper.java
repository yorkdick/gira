package com.rayfay.gira.mapper;

import com.rayfay.gira.dto.SprintRequest;
import com.rayfay.gira.dto.SprintResponse;
import com.rayfay.gira.entity.Sprint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SprintMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Sprint toEntity(SprintRequest request);

    @Mapping(source = "project.id", target = "projectId")
    SprintResponse toResponse(Sprint sprint);
}