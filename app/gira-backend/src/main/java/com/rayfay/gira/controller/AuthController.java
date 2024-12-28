package com.rayfay.gira.controller;

import com.rayfay.gira.dto.auth.AuthResponse;
import com.rayfay.gira.dto.auth.LoginRequest;
import com.rayfay.gira.dto.auth.RegisterRequest;
import com.rayfay.gira.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        // try {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
        // } catch (IllegalArgumentException e) {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        // }
    }

    @Operation(summary = "Login user")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // try {
        return ResponseEntity.ok(authService.login(request));
        // } catch (UsernameNotFoundException e) {
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        // } catch (BadCredentialsException e) {
        // throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username
        // or password");
        // } catch (LockedException e) {
        // throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is locked");
        // }
    }

    @Operation(summary = "Logout user")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token.substring(7)); // Remove "Bearer " prefix
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Validate token")
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestParam String token) {
        return ResponseEntity.ok(authService.validateToken(token));
    }

    @Operation(summary = "Refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(authService.refresh(token.substring(7))); // Remove "Bearer " prefix
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}