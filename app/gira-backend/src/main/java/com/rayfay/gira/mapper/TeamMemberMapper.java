package com.rayfay.gira.mapper;

import com.rayfay.gira.dto.TeamMemberDto;
import com.rayfay.gira.entity.TeamMember;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface TeamMemberMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "teamId", source = "team.id")
    TeamMemberDto toDto(TeamMember teamMember);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "team", ignore = true)
    @Mapping(target = "joinedAt", ignore = true)
    TeamMember toEntity(TeamMemberDto teamMemberDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "team", ignore = true)
    @Mapping(target = "joinedAt", ignore = true)
    void updateEntity(TeamMemberDto teamMemberDto, @MappingTarget TeamMember teamMember);
}