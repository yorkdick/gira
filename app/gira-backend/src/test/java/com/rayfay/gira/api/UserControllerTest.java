package com.rayfay.gira.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rayfay.gira.dto.request.CreateUserRequest;
import com.rayfay.gira.dto.request.UpdatePasswordRequest;
import com.rayfay.gira.dto.request.UpdateUserRequest;
import com.rayfay.gira.entity.User;
import com.rayfay.gira.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = { "/sql/user/init_user_test.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = { "/sql/user/cleanup_user_test.sql" }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private UserRepository userRepository;

        private Long testUserId;

        @BeforeEach
        void setUp() {
                User testUser = userRepository.findByUsername("testuser")
                                .orElseThrow(() -> new RuntimeException("Test user not found"));
                testUserId = testUser.getId();
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void createUser_WithValidRequest_ShouldReturnCreatedUser() throws Exception {
                // Arrange
                CreateUserRequest request = new CreateUserRequest();
                request.setUsername("newuser");
                request.setPassword("password123");
                request.setFullName("New User");
                request.setEmail("newuser@example.com");

                // Act & Assert
                mockMvc.perform(post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value("newuser"))
                                .andExpect(jsonPath("$.fullName").value("New User"))
                                .andExpect(jsonPath("$.email").value("newuser@example.com"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void createUser_WithDuplicateUsername_ShouldReturnConflict() throws Exception {
                // Arrange
                CreateUserRequest request = new CreateUserRequest();
                request.setUsername("testuser"); // 使用已存在的用户名
                request.setPassword("password123");
                request.setFullName("Another User");
                request.setEmail("another@example.com");

                // Act & Assert
                mockMvc.perform(post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.message").value("用户名已存在"));
        }

        @Test
        @WithMockUser(username = "testuser")
        void updateUser_WithValidRequest_ShouldReturnUpdatedUser() throws Exception {
                // Arrange
                UpdateUserRequest request = new UpdateUserRequest();
                request.setFullName("Updated User");
                request.setEmail("updated@example.com");

                // Act & Assert
                mockMvc.perform(put("/api/users/" + testUserId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.fullName").value("Updated User"))
                                .andExpect(jsonPath("$.email").value("updated@example.com"));
        }

        @Test
        @WithMockUser(username = "testuser")
        void updateUser_UpdateOtherUser_ShouldReturnForbidden() throws Exception {
                // Arrange
                CreateUserRequest createRequest = new CreateUserRequest();
                createRequest.setUsername("otheruser");
                createRequest.setPassword("password123");
                createRequest.setFullName("Other User");
                createRequest.setEmail("other@example.com");

                // 使用管理员创建另一个用户
                mockMvc.perform(post("/api/users")
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createRequest)))
                                .andExpect(status().isOk());

                User otherUser = userRepository.findByUsername("otheruser")
                                .orElseThrow(() -> new RuntimeException("Other user not found"));

                UpdateUserRequest request = new UpdateUserRequest();
                request.setFullName("Updated User");
                request.setEmail("updated@example.com");

                // 使用普通用户尝试更新其他用户信息
                mockMvc.perform(put("/api/users/" + otherUser.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isForbidden())
                                .andExpect(jsonPath("$.message").value("无权限修改其他用户信息"));
        }

        @Test
        @WithMockUser(username = "testuser")
        void updatePassword_WithValidRequest_ShouldReturnOk() throws Exception {
                // Arrange
                UpdatePasswordRequest request = new UpdatePasswordRequest();
                request.setOldPassword("password123");
                request.setNewPassword("newpassword123");

                // Act & Assert
                mockMvc.perform(put("/api/users/" + testUserId + "/password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "testuser")
        void updatePassword_WithWrongOldPassword_ShouldReturnBadRequest() throws Exception {
                // Arrange
                UpdatePasswordRequest request = new UpdatePasswordRequest();
                request.setOldPassword("wrongpassword");
                request.setNewPassword("newpassword123");

                // Act & Assert
                mockMvc.perform(put("/api/users/" + testUserId + "/password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("原密码错误"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_ShouldReturnPageOfUsers() throws Exception {
                // Act & Assert
                mockMvc.perform(get("/api/users"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray())
                                .andExpect(jsonPath("$.content[0].username").exists())
                                .andExpect(jsonPath("$.content[0].email").exists());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void deleteUser_WithValidId_ShouldReturnOk() throws Exception {
                // Act & Assert
                mockMvc.perform(delete("/api/users/" + testUserId))
                                .andExpect(status().isOk());
        }
}