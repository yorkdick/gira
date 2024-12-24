package com.rayfay.gira.repository;

import com.rayfay.gira.entity.Project;
import com.rayfay.gira.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Page<Project> findByOwnerId(Long ownerId, Pageable pageable);

    Page<Project> findByMembersId(Long memberId, Pageable pageable);

    Optional<Project> findByKey(String key);

    Page<Project> findByMembersContaining(User user, Pageable pageable);
}