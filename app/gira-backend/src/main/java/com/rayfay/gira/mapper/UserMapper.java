package com.rayfay.gira.mapper;

import com.rayfay.gira.dto.response.UserResponse;
import com.rayfay.gira.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserResponse toResponse(User user);
}