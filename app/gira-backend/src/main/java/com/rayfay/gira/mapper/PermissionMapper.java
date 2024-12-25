package com.rayfay.gira.mapper;

import com.rayfay.gira.dto.PermissionDto;
import com.rayfay.gira.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PermissionMapper extends BaseMapper<Permission, PermissionDto> {
    PermissionDto toDto(Permission permission);

    Permission toEntity(PermissionDto permissionDto);
}