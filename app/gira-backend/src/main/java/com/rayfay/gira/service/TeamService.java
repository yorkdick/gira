package com.rayfay.gira.service;

import com.rayfay.gira.dto.TeamDto;
import com.rayfay.gira.dto.TeamMemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TeamService {
    TeamDto createTeam(TeamDto teamDto);

    TeamDto getTeamById(Long teamId);

    Page<TeamDto> getTeams(Pageable pageable);

    TeamDto updateTeam(Long teamId, TeamDto teamDto);

    void deleteTeam(Long teamId);

    TeamDto addTeamMember(Long teamId, TeamMemberDto memberDto);

    TeamDto removeTeamMember(Long teamId, Long userId);

    Page<TeamMemberDto> getTeamMembers(Long teamId, Pageable pageable);

    boolean isTeamAdmin(Long teamId);

    boolean hasAccess(Long teamId);
}