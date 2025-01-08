package com.rayfay.gira.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.*;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Order(7)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SprintApiTest extends AuthenticatedApiTest {

    private Long projectId;
    private Long sprintId;
    private static final String TEST_PROJECT_NAME = "Test Project " + UUID.randomUUID().toString();
    private static final String TEST_PROJECT_KEY = "TEST" + UUID.randomUUID().toString().substring(0, 5);

    @BeforeAll
    void init() {
        // 先清理可能存在的测试数据
        cleanup();

        // 创建测试项目
        Map<String, Object> projectRequest = new HashMap<>();
        projectRequest.put("name", TEST_PROJECT_NAME);
        projectRequest.put("key", TEST_PROJECT_KEY);
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

    @AfterAll
    void cleanup() {
        try {
            // 获取项目列表并查找测试项目
            ResponseEntity<String> response = restTemplate.exchange(
                    BASE_URL + "/projects?page=0&size=100",
                    HttpMethod.GET,
                    createAuthEntity(),
                    String.class);

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            JsonNode content = jsonNode.get("content");
            if (content.isArray()) {
                for (JsonNode project : content) {
                    if (project.get("name").asText().startsWith("Test Project ")) {
                        Long pid = project.get("id").asLong();

                        // 1. 先获取项目的所有sprint
                        ResponseEntity<String> sprintsResponse = restTemplate.exchange(
                                BASE_URL + "/projects/" + pid + "/sprints",
                                HttpMethod.GET,
                                createAuthEntity(),
                                String.class);

                        // 2. 删除所有sprint
                        JsonNode sprintsJson = objectMapper.readTree(sprintsResponse.getBody());
                        JsonNode sprintsContent = sprintsJson.get("content");
                        if (sprintsContent.isArray()) {
                            for (JsonNode sprint : sprintsContent) {
                                Long sprintId = sprint.get("id").asLong();
                                try {
                                    restTemplate.exchange(
                                            BASE_URL + "/sprints/" + sprintId,
                                            HttpMethod.DELETE,
                                            createAuthEntity(),
                                            String.class);
                                    log.info("Deleted sprint with id: {}", sprintId);
                                } catch (Exception e) {
                                    log.error("Error deleting sprint {}: {}", sprintId, e.getMessage());
                                }
                            }
                        }

                        // 3. 删除项目
                        try {
                            restTemplate.exchange(
                                    BASE_URL + "/projects/" + pid,
                                    HttpMethod.DELETE,
                                    createAuthEntity(),
                                    String.class);
                            log.info("Deleted test project with id: {}", pid);
                        } catch (Exception e) {
                            log.error("Error deleting project {}: {}", pid, e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 如果清理失败，记录日志但不影响测试
            log.error("Error during cleanup: {}", e.getMessage());
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