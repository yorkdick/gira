package com.rayfay.gira.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.*;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Order(7)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SprintApiTest extends AuthenticatedApiTest {

    private Long projectId;
    private Long sprintId;

    @BeforeEach
    void init() {
        // 创建测试项目
        Map<String, Object> projectRequest = new HashMap<>();
        projectRequest.put("name", "Test Project");
        projectRequest.put("key", "TEST");
        projectRequest.put("description", "Test Project Description");
        Map<String, Object> owner = new HashMap<>();
        owner.put("id", 1L);
        projectRequest.put("owner", owner);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/projects",
                HttpMethod.POST,
                createJsonAuthEntity(projectRequest),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        projectId = extractIdFromResponse(response.getBody());
    }

    private Long extractIdFromResponse(String responseBody) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("id").asLong();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract ID from response", e);
        }
    }

    @AfterEach
    void cleanup() {
        // 删除测试项目
        if (projectId != null) {
            restTemplate.exchange(
                    BASE_URL + "/projects/" + projectId,
                    HttpMethod.DELETE,
                    createAuthEntity(),
                    String.class);
        }
    }

    @Test
    @Order(1)
    void testCreateSprint() {
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Sprint 1");
        request.put("goal", "完成用户认证功能");
        request.put("startDate", OffsetDateTime.now().toString());
        request.put("endDate", OffsetDateTime.now().plusDays(14).toString());
        request.put("projectId", projectId);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/sprints",
                HttpMethod.POST,
                createJsonAuthEntity(request),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("id"));
        sprintId = extractIdFromResponse(response.getBody());
    }

    @Test
    @Order(2)
    void testGetSprintList() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/projects/" + projectId + "/sprints",
                HttpMethod.GET,
                createAuthEntity(),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("content"));
    }

    @Test
    @Order(3)
    void testUpdateSprint() {
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Sprint 1 - 更新");
        request.put("goal", "完成用户认证和授权功能");
        request.put("endDate", OffsetDateTime.now().plusDays(21).toString());

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/sprints/" + sprintId,
                HttpMethod.PUT,
                createJsonAuthEntity(request),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(4)
    void testStartSprint() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/sprints/" + sprintId + "/start",
                HttpMethod.POST,
                createAuthEntity(),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(5)
    void testCompleteSprint() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/sprints/" + sprintId + "/complete",
                HttpMethod.POST,
                createAuthEntity(),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}