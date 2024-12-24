package com.rayfay.gira.repository;

import com.rayfay.gira.entity.Board;
import com.rayfay.gira.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findByProject(Project project, Pageable pageable);

    Page<Board> findByProjectAndArchivedFalse(Project project, Pageable pageable);
}