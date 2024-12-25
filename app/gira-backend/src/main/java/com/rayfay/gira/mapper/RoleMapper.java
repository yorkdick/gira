package com.rayfay.gira.mapper;

import com.rayfay.gira.dto.RoleDto;
import com.rayfay.gira.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { PermissionMapper.class })
public interface RoleMapper {
    RoleDto toDto(Role role);

    Role toEntity(RoleDto roleDto);

    void updateEntity(RoleDto roleDto, @MappingTarget Role role);
}