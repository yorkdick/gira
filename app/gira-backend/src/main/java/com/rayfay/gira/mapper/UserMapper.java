package com.rayfay.gira.mapper;

import com.rayfay.gira.dto.UserDto;
import com.rayfay.gira.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper extends BaseMapper<User, UserDto> {

    @Override
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "avatar", source = "avatar")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "status", source = "status")
    UserDto toDto(User user);

    @Override
    @Mapping(target = "avatar", source = "avatar")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "status", source = "status")
    User toEntity(UserDto dto);

    @Override
    @Mapping(target = "avatar", source = "avatar")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "status", source = "status")
    void updateEntity(UserDto dto, @MappingTarget User user);
}