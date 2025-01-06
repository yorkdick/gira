package com.rayfay.gira.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rayfay.gira.api.dto.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class BaseApiTest {
    protected static final String BASE_URL = "http://localhost:8080/api/v1";
    protected static final RestTemplate restTemplate = new RestTemplate();
    protected static final ObjectMapper objectMapper = new ObjectMapper();
    protected String token;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        try {
            // 登录获取token
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUsername("admin");
            loginRequest.setPassword("NewPassword123!");

            log.info("Attempting to login with credentials: {}", loginRequest);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    BASE_URL + "/auth/login",
                    loginRequest,
                    String.class);

            log.info("Login response: {}", response.getBody());

            assertTrue(response.getBody().contains("token"));
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            token = jsonNode.get("token").asText();
            log.info("Successfully obtained token");
        } catch (Exception e) {
            log.error("Error during setup: ", e);
            throw e;
        }
    }
}