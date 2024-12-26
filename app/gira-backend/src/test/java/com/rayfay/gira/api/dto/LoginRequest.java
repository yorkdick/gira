package com.rayfay.gira.api.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
    private Boolean rememberMe;
}