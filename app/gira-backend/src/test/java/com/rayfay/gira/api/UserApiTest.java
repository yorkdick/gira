package com.rayfay.gira.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.rayfay.gira.api.dto.LoginRequest;
import com.rayfay.gira.api.dto.UserRequest;
import org.junit.jupiter.api.*;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@Order(2)
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // 控制测试方法的执行顺序
class UserApiTest extends AuthenticatedApiTest {

    private Long createdUserId;

    @Test
    @Order(1)
    void testCreateUser() {
        // TC-004-1: 创建用户
        UserRequest request = new UserRequest();
        request.setUsername("newuser" + System.currentTimeMillis());
        request.setEmail("newuser" + System.currentTimeMillis() + "@gira.com");
        request.setPassword("Password123!");
        request.setFullName("New User");
        request.setRole("USER");
        request.setEnabled(true);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL + "/auth/register",
                createJsonAuthEntity(request),
                String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("id"));

        // 保存创建的用户ID
        createdUserId = extractIdFromResponse(response.getBody());
    }

    @Test
    @Order(2)
    void testCreateUserWithExistingUsername() {
        // TC-004-2: 创建用户 - 用户名已存在
        UserRequest request = new UserRequest();
        request.setUsername("admin");
        request.setEmail("newuser" + System.currentTimeMillis() + "@gira.com");
        request.setPassword("Password123!");
        request.setFullName("New User");
        request.setRole("USER");
        request.setEnabled(true);

        try {
            restTemplate.postForEntity(
                    BASE_URL + "/auth/register",
                    createJsonAuthEntity(request),
                    String.class);
            fail("Expected HttpClientErrorException.BadRequest");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("Username is already taken"));
        }
    }

    @Test
    @Order(3)
    void testGetUserDetails() {
        // TC-004-3: 获取用户详情
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/users/1",
                HttpMethod.GET,
                createAuthEntity(),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("id"));
        assertTrue(response.getBody().contains("username"));
        assertTrue(response.getBody().contains("email"));
    }

    @Test
    @Order(4)
    void testUpdateUser() {
        // TC-004-4: 更新用户
        UserRequest request = new UserRequest();
        request.setFullName("Updated User");
        request.setAvatar("https://example.com/avatar.jpg");

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/users/" + createdUserId,
                HttpMethod.PUT,
                createJsonAuthEntity(request),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Updated User"));
    }

    @Test
    @Order(5)
    void testDeleteUser() {
        // TC-004-5: 删除用户
        // 先创建一个新用户
        UserRequest createRequest = new UserRequest();
        createRequest.setUsername("userToDelete" + System.currentTimeMillis());
        createRequest.setEmail("userToDelete" + System.currentTimeMillis() + "@gira.com");
        createRequest.setPassword("Password123!");
        createRequest.setFullName("User To Delete");
        createRequest.setRole("USER");
        createRequest.setEnabled(true);

        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                BASE_URL + "/auth/register",
                createJsonAuthEntity(createRequest),
                String.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertTrue(createResponse.getBody().contains("id"));

        // 从响应中提取用户ID
        String responseBody = createResponse.getBody();
        int idStart = responseBody.indexOf("\"id\":") + 5;
        int idEnd = responseBody.indexOf(",", idStart);
        String userId = responseBody.substring(idStart, idEnd);

        // 删除创建的用户
        ResponseEntity<String> deleteResponse = restTemplate.exchange(
                BASE_URL + "/users/" + userId,
                HttpMethod.DELETE,
                createAuthEntity(),
                String.class);

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
    }

    @Test
    @Order(6)
    void testGetUserList() {
        // TC-004-6: 获取用户列表
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/users",
                HttpMethod.GET,
                createAuthEntity(),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("content"));
        assertTrue(response.getBody().contains("totalElements"));
    }

    @Test
    @Order(7)
    void testChangeUserPassword() {
        // TC-004-7: 修改用户密码
        String oldPassword = "NewPassword123!";
        String newPassword = "1qaz@WSX";

        try {
            // 1. 修改密码
            Map<String, String> changeRequest = new HashMap<>();
            changeRequest.put("oldPassword", oldPassword);
            changeRequest.put("newPassword", newPassword);

            ResponseEntity<String> changeResponse = restTemplate.exchange(
                    BASE_URL + "/users/1/password",
                    HttpMethod.PUT,
                    createJsonAuthEntity(changeRequest),
                    String.class);

            assertEquals(HttpStatus.OK, changeResponse.getStatusCode());

            // 2. 使用新密码登录验证
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUsername("admin");
            loginRequest.setPassword(newPassword);

            ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                    BASE_URL + "/auth/login",
                    loginRequest,
                    String.class);

            assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
            assertTrue(loginResponse.getBody().contains("token"));

        } finally {
            // 3. 恢复原密码
            Map<String, String> restoreRequest = new HashMap<>();
            restoreRequest.put("oldPassword", newPassword);
            restoreRequest.put("newPassword", oldPassword);

            ResponseEntity<String> restoreResponse = restTemplate.exchange(
                    BASE_URL + "/users/1/password",
                    HttpMethod.PUT,
                    createJsonAuthEntity(restoreRequest),
                    String.class);

            assertEquals(HttpStatus.OK, restoreResponse.getStatusCode());
        }
    }

    @Test
    @Order(8)
    void testDisableUser() {
        // TC-004-8: 禁用用户
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/users/" + createdUserId + "/disable",
                HttpMethod.PUT,
                createAuthEntity(),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"status\":0"));
    }

    @Test
    @Order(9)
    void testSearchUsers() {
        // TC-004-9: 搜索用户
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/users/search?keyword=admin",
                HttpMethod.GET,
                createAuthEntity(),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("content"));
        assertTrue(response.getBody().contains("totalElements"));
        assertTrue(response.getBody().contains("admin"));
    }

    private Long extractIdFromResponse(String response) {
        // 从响应中提取用户ID
        int idStart = response.indexOf("\"id\":") + 5;
        int idEnd = response.indexOf(",", idStart);
        if (idEnd == -1) {
            idEnd = response.indexOf("}", idStart);
        }
        return Long.parseLong(response.substring(idStart, idEnd).trim());
    }

    @AfterAll
    void cleanup() {
        // 清理创建的测试用户
        if (createdUserId != null) {
            try {
                restTemplate.exchange(
                        BASE_URL + "/users/" + createdUserId,
                        HttpMethod.DELETE,
                        createAuthEntity(),
                        String.class);
            } catch (Exception e) {
                // 记录但不抛出异常，因为这只是清理操作
                System.err.println("Failed to cleanup test user: " + e.getMessage());
            }
        }
    }
}