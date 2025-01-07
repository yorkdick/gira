package com.rayfay.gira.service.impl;

import com.rayfay.gira.dto.UserDto;
import com.rayfay.gira.entity.User;
import com.rayfay.gira.exception.ResourceNotFoundException;
import com.rayfay.gira.mapper.UserMapper;
import com.rayfay.gira.repository.UserRepository;
import com.rayfay.gira.security.SecurityUtils;
import com.rayfay.gira.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.rayfay.gira.security.UserPrincipal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(UserPrincipal::create)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public UserDto getCurrentUser() {
        String username = SecurityUtils.getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return clearPassword(userMapper.toDto(user));
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return clearPassword(userMapper.toDto(user));
    }

    @Override
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return clearPasswords(userRepository.findAll(pageable).map(userMapper::toDto));
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (userDto.getUsername() != null) {
            user.setUsername(userDto.getUsername());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getFullName() != null) {
            user.setFullName(userDto.getFullName());
        }
        if (userDto.getAvatarUrl() != null) {
            user.setAvatarUrl(userDto.getAvatarUrl());
        }
        if (userDto.getStatus() > 0) {
            user.setStatus(userDto.getStatus());
        }
        // if (userDto.getPassword() != null) {
        // user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        // }

        user = userRepository.save(user);
        return clearPassword(userMapper.toDto(user));
    }

    @Override
    @Transactional
    public UserDto updatePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid old password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user = userRepository.save(user);
        return clearPassword(userMapper.toDto(user));
    }

    @Override
    @Transactional
    public UserDto updateAvatar(Long id, String avatarUrl) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setAvatarUrl(avatarUrl);
        user = userRepository.save(user);
        return clearPassword(userMapper.toDto(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UserDto enableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setStatus(1);
        user = userRepository.save(user);
        return clearPassword(userMapper.toDto(user));
    }

    @Override
    @Transactional
    public UserDto disableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setStatus(0);
        user = userRepository.save(user);
        return clearPassword(userMapper.toDto(user));
    }

    @Override
    @Transactional
    public void changePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid old password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public boolean isCurrentUser(Long id) {
        String username = SecurityUtils.getCurrentUsername();
        return userRepository.findById(id)
                .map(user -> user.getUsername().equals(username))
                .orElse(false);
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        user.setStatus(1); // Set default status
        if (userDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        user = userRepository.save(user);
        return clearPassword(userMapper.toDto(user));
    }

    @Override
    public Page<UserDto> searchUsers(String keyword, Pageable pageable) {
        return userRepository
                .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(
                        keyword, pageable)
                .map(userMapper::toDto)
                .map(this::clearPassword);
    }

    private UserDto clearPassword(UserDto userDto) {
        if (userDto != null) {
            userDto.setPassword(null);
        }
        return userDto;
    }

    private Page<UserDto> clearPasswords(Page<UserDto> page) {
        if (page != null) {
            page.getContent().forEach(this::clearPassword);
        }
        return page;
    }
}