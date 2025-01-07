package com.rayfay.gira.repository;

import com.rayfay.gira.entity.Project;
import com.rayfay.gira.entity.Sprint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {
    Page<Sprint> findByProject(Project project, Pageable pageable);
}