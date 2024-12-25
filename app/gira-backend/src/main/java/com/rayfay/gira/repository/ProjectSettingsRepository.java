package com.rayfay.gira.repository;

import com.rayfay.gira.entity.ProjectSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectSettingsRepository extends JpaRepository<ProjectSettings, Long> {
    ProjectSettings findByProjectKey(String projectKey);
}