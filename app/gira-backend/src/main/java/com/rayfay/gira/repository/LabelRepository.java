package com.rayfay.gira.repository;

import com.rayfay.gira.entity.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {
    List<Label> findAllByProjectId(Long projectId);
}