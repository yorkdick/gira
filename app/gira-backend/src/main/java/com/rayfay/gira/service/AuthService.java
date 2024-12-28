package com.rayfay.gira.service;

import com.rayfay.gira.dto.auth.AuthResponse;
import com.rayfay.gira.dto.auth.LoginRequest;
import com.rayfay.gira.dto.auth.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    void logout(String token);

    boolean validateToken(String token);

    AuthResponse refresh(String token);
}