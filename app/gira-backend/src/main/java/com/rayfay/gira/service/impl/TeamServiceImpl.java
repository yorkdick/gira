package com.rayfay.gira.service.impl;

import com.rayfay.gira.dto.TeamDto;
import com.rayfay.gira.dto.TeamMemberDto;
import com.rayfay.gira.entity.Team;
import com.rayfay.gira.entity.TeamMember;
import com.rayfay.gira.entity.User;
import com.rayfay.gira.exception.ResourceNotFoundException;
import com.rayfay.gira.mapper.TeamMapper;
import com.rayfay.gira.mapper.TeamMemberMapper;
import com.rayfay.gira.repository.TeamMemberRepository;
import com.rayfay.gira.repository.TeamRepository;
import com.rayfay.gira.repository.UserRepository;
import com.rayfay.gira.security.SecurityUtils;
import com.rayfay.gira.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final TeamMapper teamMapper;
    private final TeamMemberMapper teamMemberMapper;

    @Override
    @Transactional
    public TeamDto createTeam(TeamDto teamDto) {
        Team team = teamMapper.toEntity(teamDto);
        team.setCreatedBy(SecurityUtils.getCurrentUserId().toString());
        team = teamRepository.save(team);

        // Add creator as team admin
        TeamMember teamMember = new TeamMember();
        teamMember.setTeam(team);
        teamMember.setUser(userRepository.getReferenceById(SecurityUtils.getCurrentUserId()));
        teamMember.setRole("ADMIN");
        teamMemberRepository.save(teamMember);

        return teamMapper.toDto(team);
    }

    @Override
    public TeamDto getTeamById(Long teamId) {
        return teamRepository.findById(teamId)
                .map(teamMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
    }

    @Override
    public Page<TeamDto> getTeams(Pageable pageable) {
        return teamRepository.findAll(pageable)
                .map(teamMapper::toDto);
    }

    @Override
    @Transactional
    public TeamDto updateTeam(Long teamId, TeamDto teamDto) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

        teamMapper.updateEntity(teamDto, team);
        team = teamRepository.save(team);
        return teamMapper.toDto(team);
    }

    @Override
    @Transactional
    public void deleteTeam(Long teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new ResourceNotFoundException("Team not found with id: " + teamId);
        }
        teamRepository.deleteById(teamId);
    }

    @Override
    @Transactional
    public TeamDto addTeamMember(Long teamId, TeamMemberDto memberDto) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

        User user = userRepository.findById(memberDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + memberDto.getUserId()));

        TeamMember teamMember = new TeamMember();
        teamMember.setTeam(team);
        teamMember.setUser(user);
        teamMember.setRole(memberDto.getRole());
        teamMemberRepository.save(teamMember);

        return teamMapper.toDto(team);
    }

    @Override
    @Transactional
    public TeamDto removeTeamMember(Long teamId, Long userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

        teamMemberRepository.deleteByTeamIdAndUserId(teamId, userId);
        return teamMapper.toDto(team);
    }

    @Override
    public Page<TeamMemberDto> getTeamMembers(Long teamId, Pageable pageable) {
        return teamMemberRepository.findByTeamId(teamId, pageable)
                .map(teamMemberMapper::toDto);
    }

    @Override
    public boolean isTeamAdmin(Long teamId) {
        return teamMemberRepository.existsByTeamIdAndUserIdAndRole(
                teamId, SecurityUtils.getCurrentUserId(), "ADMIN");
    }

    @Override
    public boolean hasAccess(Long teamId) {
        return teamMemberRepository.existsByTeamIdAndUserId(
                teamId, SecurityUtils.getCurrentUserId());
    }
}