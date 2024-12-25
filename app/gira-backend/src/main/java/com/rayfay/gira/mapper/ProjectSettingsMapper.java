package com.rayfay.gira.mapper;

import com.rayfay.gira.dto.ProjectSettingsDto;
import com.rayfay.gira.entity.ProjectSettings;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProjectSettingsMapper {

    ProjectSettingsDto toDto(ProjectSettings settings);

    ProjectSettings toEntity(ProjectSettingsDto settingsDto);

    @Mapping(target = "id", ignore = true)
    void updateEntity(ProjectSettingsDto settingsDto, @MappingTarget ProjectSettings settings);
}