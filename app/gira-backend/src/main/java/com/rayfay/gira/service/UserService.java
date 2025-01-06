package com.rayfay.gira.service;

import com.rayfay.gira.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto getCurrentUser();

    UserDto getUserById(Long id);

    Page<UserDto> getAllUsers(Pageable pageable);

    UserDto updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);

    UserDto updatePassword(Long id, String oldPassword, String newPassword);

    UserDto updateAvatar(Long id, String avatarUrl);

    boolean isCurrentUser(Long id);

    UserDto enableUser(Long id);

    UserDto disableUser(Long id);

    void changePassword(Long id, String oldPassword, String newPassword);

    UserDto createUser(UserDto userDto);

    Page<UserDto> searchUsers(String keyword, Pageable pageable);
}