package com.rayfay.gira.api;

import com.rayfay.gira.api.dto.UserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.*;

class UserApiTest extends BaseApiTest {

    @Test
    void testCreateUser() {
        // TC-004-1: 创建用户
        UserRequest request = new UserRequest();
        request.setUsername("newuser");
        request.setEmail("newuser@gira.com");
        request.setPassword("Password123!");
        request.setFullName("New User");
        request.setRole("USER");
        request.setEnabled(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<UserRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL + "/users",
                entity,
                String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("id"));
        assertTrue(response.getBody().contains("username"));
    }

    @Test
    void testCreateUserWithExistingUsername() {
        // TC-004-2: 创建用户 - 用户名已存在
        UserRequest request = new UserRequest();
        request.setUsername("user1");
        request.setEmail("newuser@gira.com");
        request.setPassword("Password123!");
        request.setFullName("New User");
        request.setRole("USER");
        request.setEnabled(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<UserRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.postForEntity(
                    BASE_URL + "/users",
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.BadRequest");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("用户名已存在"));
        }
    }

    @Test
    void testGetUserDetails() {
        // TC-004-3: 获取用户详情
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/users/1",
                HttpMethod.GET,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("id"));
        assertTrue(response.getBody().contains("username"));
        assertTrue(response.getBody().contains("email"));
    }

    @Test
    void testUpdateUser() {
        // TC-004-4: 更新用户
        UserRequest request = new UserRequest();
        request.setFullName("Updated User");
        request.setAvatar("https://example.com/avatar.jpg");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<UserRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/users/2",
                HttpMethod.PUT,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Updated User"));
    }

    @Test
    void testDeleteUser() {
        // TC-004-5: 删除用户
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/users/3",
                HttpMethod.DELETE,
                entity,
                String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testGetUserList() {
        // TC-004-6: 获取用户列表
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/users",
                HttpMethod.GET,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("content"));
        assertTrue(response.getBody().contains("totalElements"));
    }

    @Test
    void testChangeUserPassword() {
        // TC-004-7: 修改用户密码
        UserRequest request = new UserRequest();
        request.setPassword("NewPassword123!");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<UserRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/users/2/password",
                HttpMethod.PUT,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDisableUser() {
        // TC-004-8: 禁用用户
        UserRequest request = new UserRequest();
        request.setEnabled(false);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<UserRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/users/2/status",
                HttpMethod.PUT,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"enabled\":false"));
    }

    @Test
    void testSearchUsers() {
        // TC-004-9: 搜索用户
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/users/search?keyword=user&role=USER",
                HttpMethod.GET,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("content"));
        assertTrue(response.getBody().contains("totalElements"));
    }
}