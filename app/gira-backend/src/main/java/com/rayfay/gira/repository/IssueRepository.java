package com.rayfay.gira.repository;

import com.rayfay.gira.entity.Issue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    Page<Issue> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String title, String description, Pageable pageable);

    Page<Issue> findByProjectId(Long projectId, Pageable pageable);

    Page<Issue> findBySprintId(Long sprintId, Pageable pageable);

    Page<Issue> findByAssigneeId(Long assigneeId, Pageable pageable);
}