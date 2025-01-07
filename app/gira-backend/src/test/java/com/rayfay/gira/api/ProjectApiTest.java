package com.rayfay.gira.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.rayfay.gira.api.dto.LoginRequest;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Order(3)
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProjectApiTest extends AuthenticatedApiTest {

    private Long createdProjectId;

    @BeforeAll
    void init() {
        try {
            // 清理同名项目
            ResponseEntity<String> response = restTemplate.exchange(
                    BASE_URL + "/projects",
                    HttpMethod.GET,
                    createAuthEntity(),
                    String.class);

            log.info("response: {}", response.getBody());

            if (response.getBody() != null && response.getBody().contains("content")) {
                String body = response.getBody();
                if (body.contains("\"name\":\"新项目\"")) {
                    // 找到项目ID
                    int idStart = body.lastIndexOf("\"id\":", body.indexOf("\"name\":\"新项目\"")) + 5;
                    int idEnd = body.indexOf(",", idStart);
                    if (idEnd == -1) {
                        idEnd = body.indexOf("}", idStart);
                    }
                    Long projectId = Long.parseLong(body.substring(idStart, idEnd).trim());
                    log.info("Delete projectId: {}", projectId);
                    // 删除项目
                    ResponseEntity<String> response2 = restTemplate.exchange(
                            BASE_URL + "/projects/" + projectId,
                            HttpMethod.DELETE,
                            createAuthEntity(),
                            String.class);

                    log.info("Delete response: {}", response2.getBody());

                    // 更新剩余的响应体
                    // body = body.substring(body.indexOf("\"name\":\"新项目\"") + 10);
                }
            }
        } catch (Exception e) {
            // 记录但不抛出异常，因为这只是清理操作
            System.err.println("Failed to cleanup existing projects: " + e.getMessage());
        }
    }

    @AfterAll
    void cleanup() {
        // 只清理测试创建的项目
        if (createdProjectId != null) {
            try {
                restTemplate.exchange(
                        BASE_URL + "/projects/" + createdProjectId,
                        HttpMethod.DELETE,
                        createAuthEntity(),
                        String.class);
            } catch (Exception e) {
                // 记录但不抛出异常，因为这只是清理操作
                System.err.println("Failed to cleanup test project: " + e.getMessage());
            }
        }
    }

    @Test
    @Order(1)
    void testCreateProject() {
        Map<String, String> request = new HashMap<>();
        request.put("name", "新项目");
        request.put("description", "新项目描述");

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/projects",
                HttpMethod.POST,
                createJsonAuthEntity(request),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("id"));

        // 保存创建的项目ID以便后续清理
        createdProjectId = extractIdFromResponse(response.getBody());
    }

    @Test
    @Order(2)
    void testCreateProjectWithDuplicateName() {
        // 尝试创建与第一个测试用例相同名称的项目
        Map<String, String> request = new HashMap<>();
        request.put("name", "新项目");
        request.put("description", "新项目描述");

        try {
            restTemplate.exchange(
                    BASE_URL + "/projects",
                    HttpMethod.POST,
                    createJsonAuthEntity(request),
                    String.class);
            fail("Expected HttpClientErrorException.BadRequest");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("项目名称已存在"));
        }
    }

    @Test
    @Order(3)
    void testGetProjectList() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/projects",
                HttpMethod.GET,
                createAuthEntity(),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("content"));
    }

    private Long extractIdFromResponse(String response) {
        // 简单的方式从响应中提取ID
        int start = response.indexOf("\"id\":") + 5;
        int end = response.indexOf(",", start);
        if (end == -1) {
            end = response.indexOf("}", start);
        }
        return Long.parseLong(response.substring(start, end).trim());
    }
}