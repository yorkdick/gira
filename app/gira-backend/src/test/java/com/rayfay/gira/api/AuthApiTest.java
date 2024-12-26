package com.rayfay.gira.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rayfay.gira.api.dto.LoginRequest;
import com.rayfay.gira.api.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class AuthApiTest extends BaseApiTest {

    @Test
    void testSuccessfulLogin() {
        // TC-001-1: 成功登录
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("1qaz@WSX");
        request.setRememberMe(false);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL + "/auth/login",
                request,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("token"));
        // assertTrue(response.getBody().contains("admin"));
    }

    @Test
    void testLoginWithNonexistentUsername() {
        // TC-001-2: 用户名不存在
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistent");
        request.setPassword("password123");
        request.setRememberMe(false);

        try {
            restTemplate.postForEntity(
                    BASE_URL + "/auth/login",
                    request,
                    String.class);
            fail("Expected HttpClientErrorException.NotFound");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("用户不存在"));
        }
    }

    @Test
    void testSuccessfulRegistration() {
        // TC-002-1: 成功注册
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser_" + System.currentTimeMillis()); // 避免用户名冲突
        request.setPassword("Password123!");
        request.setEmail("newuser_" + System.currentTimeMillis() + "@gira.com");

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL + "/auth/register",
                request,
                String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("注册成功"));
    }

    @Test
    void testValidTokenAccess() {
        // TC-003-1: 有效Token访问
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/user/profile",
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
                    BASE_URL + "/user/profile",
                    HttpMethod.GET,
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.Unauthorized");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("无效的Token"));
        }
    }

    @Test
    void testTokenRefresh() {
        // TC-003-5: Token刷新
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/auth/refresh",
                HttpMethod.POST,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("token"));
    }

    @Test
    void testLoginWithLockedAccount() {
        // TC-001-4: 账户被锁定
        LoginRequest request = new LoginRequest();
        request.setUsername("locked_user");
        request.setPassword("password123");
        request.setRememberMe(false);

        try {
            restTemplate.postForEntity(
                    BASE_URL + "/auth/login",
                    request,
                    String.class);
            fail("Expected HttpClientErrorException.Forbidden");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("账户已被锁定"));
        }
    }

    @Test
    void testLoginWithRememberMe() {
        // TC-001-5: 记住密码
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("1qaz@WSX");
        request.setRememberMe(true);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL + "/auth/login",
                request,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("token"));
        // 验证token有效期为7天
        assertTrue(response.getHeaders().containsKey("Set-Cookie"));
        assertTrue(response.getHeaders().getFirst("Set-Cookie").contains("Max-Age=604800"));
    }

    @Test
    void testLoginWithInvalidFormat() {
        // TC-001-6: 无效的请求格式
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
            assertTrue(e.getResponseBodyAsString().contains("用户名和密码不能为空"));
        }
    }

    @Test
    void testLoginFailureLock() {
        // TC-001-7: 连续登录失败锁定
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("wrongpassword");
        request.setRememberMe(false);

        // 连续尝试5次错误密码
        for (int i = 0; i < 5; i++) {
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

        // 第6次尝试应该返回账户锁定
        try {
            restTemplate.postForEntity(
                    BASE_URL + "/auth/login",
                    request,
                    String.class);
            fail("Expected HttpClientErrorException.Forbidden");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("账户已被锁定，请15分钟后重试"));
        }
    }

    @Test
    void testRegisterWithWeakPassword() {
        // TC-002-4: 密码强度不足
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("123");
        request.setEmail("newuser@gira.com");

        try {
            restTemplate.postForEntity(
                    BASE_URL + "/auth/register",
                    request,
                    String.class);
            fail("Expected HttpClientErrorException.BadRequest");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("密码必须包含至少8个字符，至少一个字母和一个数字"));
        }
    }

    @Test
    void testRegisterWithExistingEmail() {
        // TC-002-3: 邮箱已存在
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("Password123!");
        request.setEmail("admin@gira.com");

        try {
            restTemplate.postForEntity(
                    BASE_URL + "/auth/register",
                    request,
                    String.class);
            fail("Expected HttpClientErrorException.BadRequest");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("邮箱已被使用"));
        }
    }

    @Test
    void testLoginWithWrongPassword() {
        // TC-001-3: 密码错误
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("wrongpassword");
        request.setRememberMe(false);

        try {
            restTemplate.postForEntity(
                    BASE_URL + "/auth/login",
                    request,
                    String.class);
            fail("Expected HttpClientErrorException.Unauthorized");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("密码错误"));
        }
    }

    @Test
    void testRegisterWithExistingUsername() {
        // TC-002-2: 用户名已存在
        RegisterRequest request = new RegisterRequest();
        request.setUsername("admin");
        request.setPassword("Password123!");
        request.setEmail("newuser@gira.com");

        try {
            restTemplate.postForEntity(
                    BASE_URL + "/auth/register",
                    request,
                    String.class);
            fail("Expected HttpClientErrorException.BadRequest");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("用户名已存在"));
        }
    }

    @Test
    void testExpiredToken() {
        // TC-003-2: Token过期
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSIsImV4cCI6MTUxNjIzOTAyMn0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(
                    BASE_URL + "/user/profile",
                    HttpMethod.GET,
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.Unauthorized");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("Token已过期"));
        }
    }

    @Test
    void testNoToken() {
        // TC-003-4: 无Token访问
        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());

        try {
            restTemplate.exchange(
                    BASE_URL + "/user/profile",
                    HttpMethod.GET,
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.Unauthorized");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("未提供Token"));
        }
    }
}