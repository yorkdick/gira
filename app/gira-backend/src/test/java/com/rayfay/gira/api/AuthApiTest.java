package com.rayfay.gira.api;

import com.rayfay.gira.api.dto.LoginRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.*;

@Order(1)
public class AuthApiTest extends BaseApiTest {

    @Test
    void testLoginWithNonexistentUsername() {
        // TC-001-2: 用户名不存在
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistent");
        request.setPassword("password123");

        try {
            restTemplate.postForEntity(
                    BASE_URL + "/auth/login",
                    request,
                    String.class);
            fail("Expected HttpClientErrorException.NotFound");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        }
    }

    @Test
    void testLoginWithWrongPassword() {
        // TC-001-3: 密码错误
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("wrongpassword");

        try {
            restTemplate.postForEntity(
                    BASE_URL + "/auth/login",
                    request,
                    String.class);
            fail("Expected HttpClientErrorException.Unauthorized");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }

    @Test
    void testLoginWithLockedAccount() {
        // TC-001-4: 账户被锁定
        LoginRequest request = new LoginRequest();
        request.setUsername("locked_user");
        request.setPassword("password123");

        try {
            restTemplate.postForEntity(
                    BASE_URL + "/auth/login",
                    request,
                    String.class);
            fail("Expected HttpClientErrorException.NotFound");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        }
    }

    @Test
    void testLoginWithInvalidFormat() {
        // TC-001-5: 无效的请求格式
        LoginRequest request = new LoginRequest();
        request.setUsername("");
        request.setPassword("");

        try {
            restTemplate.postForEntity(
                    BASE_URL + "/auth/login",
                    request,
                    String.class);
            fail("Expected HttpClientErrorException.BadRequest");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        }
    }

    @Test
    void testValidTokenAccess() {
        // TC-003-1: 有效Token访问
        HttpEntity<?> entity = new HttpEntity<>(getAuthHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/users/me",
                HttpMethod.GET,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("admin"));
    }

    @Test
    void testInvalidToken() {
        // TC-003-3: 无效Token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("invalid_token");
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(
                    BASE_URL + "/users/me",
                    HttpMethod.GET,
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.Unauthorized");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }

    @Test
    void testTokenRefresh() {
        // TC-003-5: Token刷新
        HttpEntity<?> entity = new HttpEntity<>(getAuthHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/auth/refresh",
                HttpMethod.POST,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("token"));
    }

    protected HttpHeaders getAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }
}