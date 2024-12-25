package com.rayfay.gira.mapper;

import com.rayfay.gira.dto.UserDto;
import com.rayfay.gira.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { RoleMapper.class })
public interface UserMapper {
    UserDto toDto(User user);

    User toEntity(UserDto dto);

    void updateEntity(UserDto dto, @MappingTarget User user);
}