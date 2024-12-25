package com.rayfay.gira.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String avatarUrl;
    private int status;
    private Set<RoleDto> roles = new HashSet<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}