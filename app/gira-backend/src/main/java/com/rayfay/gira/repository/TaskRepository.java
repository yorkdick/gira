package com.rayfay.gira.repository;

import com.rayfay.gira.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByBoardColumnId(Long columnId);

    List<Task> findByBoardColumnIdOrderByPosition(Long columnId);

    List<Task> findByAssigneeId(Long assigneeId);

    List<Task> findByReporterId(Long reporterId);
}