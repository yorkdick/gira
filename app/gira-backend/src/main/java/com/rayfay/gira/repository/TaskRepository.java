package com.rayfay.gira.repository;

import com.rayfay.gira.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByBoardColumnId(Long columnId);

    List<Task> findByBoardColumnIdOrderByPosition(Long columnId);

    List<Task> findByAssigneeId(Long assigneeId);

    List<Task> findByReporterId(Long reporterId);

    List<Task> findByTitleContainingOrDescriptionContaining(String title, String description);

    @Query("SELECT t FROM Task t WHERE " +
            "(:title IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:priority IS NULL OR t.priority = :priority) AND " +
            "(:assigneeId IS NULL OR t.assignee.id = :assigneeId) AND " +
            "(:creatorId IS NULL OR t.creator.id = :creatorId) AND " +
            "(:projectId IS NULL OR t.project.id = :projectId)")
    List<Task> advancedSearch(
            @Param("title") String title,
            @Param("status") String status,
            @Param("priority") String priority,
            @Param("assigneeId") String assigneeId,
            @Param("creatorId") String creatorId,
            @Param("projectId") String projectId);
}