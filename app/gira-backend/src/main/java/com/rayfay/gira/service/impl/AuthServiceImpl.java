package com.rayfay.gira.service.impl;

import com.rayfay.gira.dto.auth.AuthResponse;
import com.rayfay.gira.dto.auth.LoginRequest;
import com.rayfay.gira.dto.auth.RegisterRequest;
import com.rayfay.gira.entity.User;
import com.rayfay.gira.repository.UserRepository;
import com.rayfay.gira.security.JwtService;
import com.rayfay.gira.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public void logout(String token) {
        // Token blacklisting could be implemented here if needed
    }

    @Override
    public boolean validateToken(String token) {
        String username = jwtService.extractUsername(token);
        if (username == null) {
            return false;
        }
        var user = userRepository.findByUsername(username)
                .orElse(null);
        return user != null && jwtService.isTokenValid(token, user);
    }
}