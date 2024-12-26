package com.rayfay.gira.api.dto;

import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String avatar;
    private String role;
    private Boolean enabled;
}