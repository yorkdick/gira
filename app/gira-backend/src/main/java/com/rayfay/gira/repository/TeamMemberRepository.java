package com.rayfay.gira.repository;

import com.rayfay.gira.entity.TeamMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    Page<TeamMember> findByTeamId(Long teamId, Pageable pageable);

    void deleteByTeamIdAndUserId(Long teamId, Long userId);

    boolean existsByTeamIdAndUserId(Long teamId, Long userId);

    boolean existsByTeamIdAndUserIdAndRole(Long teamId, Long userId, String role);
}