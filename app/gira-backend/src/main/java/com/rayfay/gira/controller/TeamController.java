package com.rayfay.gira.controller;

import com.rayfay.gira.dto.TeamDto;
import com.rayfay.gira.dto.TeamMemberDto;
import com.rayfay.gira.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
@Tag(name = "Teams", description = "Team management APIs")
public class TeamController {

    private final TeamService teamService;

    @Operation(summary = "Create team")
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TeamDto> createTeam(@Valid @RequestBody TeamDto teamDto) {
        return ResponseEntity.ok(teamService.createTeam(teamDto));
    }

    @Operation(summary = "Get team by ID")
    @GetMapping("/{teamId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TeamDto> getTeamById(@PathVariable Long teamId) {
        return ResponseEntity.ok(teamService.getTeamById(teamId));
    }

    @Operation(summary = "Get teams")
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<TeamDto>> getTeams(Pageable pageable) {
        return ResponseEntity.ok(teamService.getTeams(pageable));
    }

    @Operation(summary = "Update team")
    @PutMapping("/{teamId}")
    @PreAuthorize("@teamService.isTeamAdmin(#teamId)")
    public ResponseEntity<TeamDto> updateTeam(
            @PathVariable Long teamId,
            @Valid @RequestBody TeamDto teamDto) {
        return ResponseEntity.ok(teamService.updateTeam(teamId, teamDto));
    }

    @Operation(summary = "Delete team")
    @DeleteMapping("/{teamId}")
    @PreAuthorize("@teamService.isTeamAdmin(#teamId)")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long teamId) {
        teamService.deleteTeam(teamId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Add team member")
    @PostMapping("/{teamId}/members")
    @PreAuthorize("@teamService.isTeamAdmin(#teamId)")
    public ResponseEntity<TeamDto> addTeamMember(
            @PathVariable Long teamId,
            @Valid @RequestBody TeamMemberDto memberDto) {
        return ResponseEntity.ok(teamService.addTeamMember(teamId, memberDto));
    }

    @Operation(summary = "Remove team member")
    @DeleteMapping("/{teamId}/members/{userId}")
    @PreAuthorize("@teamService.isTeamAdmin(#teamId)")
    public ResponseEntity<TeamDto> removeTeamMember(
            @PathVariable Long teamId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(teamService.removeTeamMember(teamId, userId));
    }

    @Operation(summary = "Get team members")
    @GetMapping("/{teamId}/members")
    @PreAuthorize("@teamService.hasAccess(#teamId)")
    public ResponseEntity<Page<TeamMemberDto>> getTeamMembers(
            @PathVariable Long teamId,
            Pageable pageable) {
        return ResponseEntity.ok(teamService.getTeamMembers(teamId, pageable));
    }
}