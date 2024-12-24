package com.rayfay.gira.mapper;

import com.rayfay.gira.dto.ProjectDto;
import com.rayfay.gira.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = { UserMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper extends BaseMapper<Project, ProjectDto> {

    @Override
    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "members", source = "members")
    @Mapping(target = "avatar", source = "avatar")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "deletedAt", source = "deletedAt")
    ProjectDto toDto(Project project);

    @Override
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "avatar", source = "avatar")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "deletedAt", source = "deletedAt")
    Project toEntity(ProjectDto dto);

    @Override
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "avatar", source = "avatar")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "deletedAt", source = "deletedAt")
    void updateEntity(ProjectDto dto, @MappingTarget Project project);
}