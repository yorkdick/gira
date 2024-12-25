package com.rayfay.gira.auth;

import com.rayfay.gira.auth.dto.LoginRequest;
import com.rayfay.gira.auth.dto.RegisterRequest;
import com.rayfay.gira.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    void testSuccessfulLogin() throws Exception {
        // TC-001-1: 成功登录
        LoginRequest request = new LoginRequest(
                "user1",
                "password123",
                false);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.username").value("user1"))
                .andExpect(jsonPath("$.user.status").value(1))
                .andExpect(jsonPath("$.user.roles[0]").value("ROLE_USER"));
    }

    @Test
    void testLoginWithNonexistentUsername() throws Exception {
        // TC-001-2: 用户名不存在
        LoginRequest request = new LoginRequest(
                "nonexistent",
                "password123",
                false);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }

    @Test
    void testLoginWithWrongPassword() throws Exception {
        // TC-001-3: 密码错误
        LoginRequest request = new LoginRequest(
                "user1",
                "wrongpassword",
                false);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("密码错误"));
    }

    @Test
    void testLoginWithLockedAccount() throws Exception {
        // TC-001-4: 账户被锁定
        LoginRequest request = new LoginRequest(
                "locked_user",
                "password123",
                false);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("账户已被锁定"));
    }

    @Test
    void testLoginWithRememberMe() throws Exception {
        // TC-001-5: 记住密码
        LoginRequest request = new LoginRequest(
                "user1",
                "password123",
                true);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andDo(result -> {
                    String token = objectMapper.readTree(result.getResponse().getContentAsString())
                            .get("token").asText();
                    assertTrue(jwtTokenProvider.getExpirationTime(token) > System.currentTimeMillis()
                            + (7 * 24 * 60 * 60 * 1000)); // 7天
                });
    }

    @Test
    void testLoginWithInvalidFormat() throws Exception {
        // TC-001-6: 无效的请求格式
        LoginRequest request = new LoginRequest("", "", false);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("用户名和密码不能为空"));
    }

    @Test
    void testLoginWithConsecutiveFailures() throws Exception {
        // TC-001-7: 连续登录失败锁定
        LoginRequest request = new LoginRequest(
                "user1",
                "wrongpassword",
                false);

        // 前5次尝试
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }

        // 第6次尝试
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("账户已被锁定，请15分钟后重试"));
    }

    @Test
    void testSuccessfulRegistration() throws Exception {
        // TC-002-1: 成功注册
        RegisterRequest request = new RegisterRequest(
                "newuser",
                "Password123!",
                "newuser@gira.com");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.username").value("newuser"))
                .andExpect(jsonPath("$.user.email").value("newuser@gira.com"))
                .andExpect(jsonPath("$.user.status").value(1))
                .andExpect(jsonPath("$.message").value("注册成功"));
    }

    @Test
    void testRegisterWithExistingUsername() throws Exception {
        // TC-002-2: 用户名已存在
        RegisterRequest request = new RegisterRequest(
                "user1",
                "Password123!",
                "different@gira.com");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("用户名已存在"));
    }

    @Test
    void testRegisterWithExistingEmail() throws Exception {
        // TC-002-3: 邮箱已存在
        RegisterRequest request = new RegisterRequest(
                "newuser",
                "Password123!",
                "user1@gira.com");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("邮箱已被使用"));
    }

    @Test
    void testRegisterWithWeakPassword() throws Exception {
        // TC-002-4: 密码强度不足
        RegisterRequest request = new RegisterRequest(
                "newuser",
                "123",
                "newuser@gira.com");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("密码必须包含至少8个字符，至少一个字母和一个数字"));
    }

    @Test
    void testRegisterWithInvalidEmail() throws Exception {
        // TC-002-5: 无效的邮箱格式
        RegisterRequest request = new RegisterRequest(
                "newuser",
                "Password123!",
                "invalid-email");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("邮箱格式不正确"));
    }

    @Test
    void testRegisterWithInvalidUsername() throws Exception {
        // TC-002-6: 用户名格式无效
        RegisterRequest request = new RegisterRequest(
                "u",
                "Password123!",
                "newuser@gira.com");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("用户名长度必须在3-50个字符之间"));
    }

    @Test
    void testValidTokenAccess() throws Exception {
        // TC-003-1: 有效Token访问
        String token = getValidToken();

        mockMvc.perform(get("/api/user/profile")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").exists());
    }

    @Test
    void testExpiredTokenAccess() throws Exception {
        // TC-003-2: Token过期
        String expiredToken = getExpiredToken();

        mockMvc.perform(get("/api/user/profile")
                .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Token已过期"));
    }

    @Test
    void testInvalidTokenAccess() throws Exception {
        // TC-003-3: 无效Token
        mockMvc.perform(get("/api/user/profile")
                .header("Authorization", "Bearer invalid_token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("无效的Token"));
    }

    @Test
    void testMissingTokenAccess() throws Exception {
        // TC-003-4: 缺少Token
        mockMvc.perform(get("/api/user/profile"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("未提供Token"));
    }

    @Test
    void testTokenRefresh() throws Exception {
        // TC-003-5: Token刷新
        String token = getValidToken();

        mockMvc.perform(post("/api/auth/refresh")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    private String getValidToken() throws Exception {
        LoginRequest request = new LoginRequest("user1", "password123", false);
        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }

    private String getExpiredToken() {
        // 创建一个已过期的token，具体实现依��于JwtTokenProvider
        return jwtTokenProvider.createExpiredToken("user1");
    }
}