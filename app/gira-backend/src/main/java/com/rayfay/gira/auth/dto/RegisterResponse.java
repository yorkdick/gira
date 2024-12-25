package com.rayfay.gira.auth.dto;

import com.rayfay.gira.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private UserDto user;
    private String message;
}