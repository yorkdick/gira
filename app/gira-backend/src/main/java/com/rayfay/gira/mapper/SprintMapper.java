package com.rayfay.gira.mapper;

import com.rayfay.gira.dto.response.SprintResponse;
import com.rayfay.gira.entity.Sprint;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { UserMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SprintMapper {
    SprintResponse toResponse(Sprint sprint);
}