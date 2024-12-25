package com.rayfay.gira.mapper;

import com.rayfay.gira.dto.TeamDto;
import com.rayfay.gira.entity.Team;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { TeamMemberMapper.class })
public interface TeamMapper {

    TeamDto toDto(Team team);

    @Mapping(target = "members", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Team toEntity(TeamDto teamDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(TeamDto teamDto, @MappingTarget Team team);
}