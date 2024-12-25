package com.rayfay.gira.repository;

import com.rayfay.gira.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Permission findByCode(String code);

    boolean existsByCode(String code);
}