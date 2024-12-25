package com.rayfay.gira.mapper;

import com.rayfay.gira.dto.IssueDto;
import com.rayfay.gira.entity.Issue;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IssueMapper {

    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "sprintId", source = "sprint.id")
    IssueDto toDto(Issue issue);

    @Mapping(target = "assignee.id", source = "assigneeId")
    @Mapping(target = "project.id", source = "projectId")
    @Mapping(target = "sprint.id", source = "sprintId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Issue toEntity(IssueDto issueDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "assignee.id", source = "assigneeId")
    @Mapping(target = "project.id", source = "projectId")
    @Mapping(target = "sprint.id", source = "sprintId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(IssueDto issueDto, @MappingTarget Issue issue);
}