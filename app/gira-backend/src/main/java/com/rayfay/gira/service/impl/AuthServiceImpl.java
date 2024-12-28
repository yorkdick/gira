package com.rayfay.gira.service.impl;

import com.rayfay.gira.dto.auth.AuthResponse;
import com.rayfay.gira.dto.auth.LoginRequest;
import com.rayfay.gira.dto.auth.RegisterRequest;
import com.rayfay.gira.entity.Role;
import com.rayfay.gira.entity.User;
import com.rayfay.gira.repository.RoleRepository;
import com.rayfay.gira.repository.UserRepository;
import com.rayfay.gira.security.JwtTokenProvider;
import com.rayfay.gira.security.UserPrincipal;
import com.rayfay.gira.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.rayfay.gira.dto.UserDto;
import com.rayfay.gira.dto.RoleDto;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;

    @Override
    public AuthResponse register(RegisterRequest request) {
        // /try {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already taken");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(1) // 1 = enabled
                .build();

        // Assign default role
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole != null) {
            user.getRoles().add(userRole);
        }

        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String token = tokenProvider.generateToken(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        return AuthResponse.builder()
                .token(token)
                .user(convertToUserDto(userPrincipal))
                .build();
        // } catch (IllegalArgumentException e) {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        // }
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // try {
        // 先检查用户是否存在
        if (!userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameNotFoundException("User not found");
        }

        // 检查用户是否被锁定
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user.getStatus() == 0) {
            throw new LockedException("Account is locked");
        }

        // 尝试认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String token = tokenProvider.generateToken(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        return AuthResponse.builder()
                .token(token)
                .user(convertToUserDto(userPrincipal))
                .build();
        // } catch (UsernameNotFoundException e) {
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        // } catch (BadCredentialsException e) {
        // throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username
        // or password");
        // } catch (LockedException e) {
        // throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is locked");
        // }
    }

    private UserDto convertToUserDto(UserPrincipal userPrincipal) {
        UserDto userDto = new UserDto();
        userDto.setId(userPrincipal.getId());
        userDto.setUsername(userPrincipal.getUsername());
        userDto.setEmail(userPrincipal.getEmail());
        userDto.setStatus(1); // 已登录用户必定是启用状态
        userDto.setRoles(userPrincipal.getAuthorities().stream()
                .map(authority -> {
                    RoleDto roleDto = new RoleDto();
                    roleDto.setName(authority.getAuthority());
                    return roleDto;
                })
                .collect(Collectors.toSet()));
        return userDto;
    }

    @Override
    public void logout(String token) {
        // Token blacklisting could be implemented here if needed
    }

    @Override
    public boolean validateToken(String token) {
        return tokenProvider.validateToken(token);
    }

    @Override
    public AuthResponse refresh(String token) {
        if (!tokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid token");
        }

        String username = tokenProvider.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                UserPrincipal.create(user),
                null,
                UserPrincipal.create(user).getAuthorities());

        String newToken = tokenProvider.generateToken(authentication);

        return AuthResponse.builder()
                .token(newToken)
                .build();
    }
}