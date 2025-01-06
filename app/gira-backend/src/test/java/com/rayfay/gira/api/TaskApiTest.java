package com.rayfay.gira.api;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Order(6)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskApiTest extends AuthenticatedApiTest {

    @Test
    @Order(1)
    void testCreateTask() {
        Map<String, Object> request = new HashMap<>();
        request.put("title", "实现登录功能");
        request.put("description", "实现用户登录和认证功能");
        request.put("priority", "HIGH");
        request.put("projectId", 1L);
        request.put("assigneeId", 1L);
        request.put("type", "TASK");
        request.put("status", "TODO");

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/tasks",
                HttpMethod.POST,
                createJsonAuthEntity(request),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("id"));
    }

    @Test
    @Order(2)
    void testGetTaskList() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/projects/1/tasks",
                HttpMethod.GET,
                createAuthEntity(),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("content"));
    }

    @Test
    @Order(3)
    void testUpdateTask() {
        Map<String, Object> request = new HashMap<>();
        request.put("title", "更新后的标题");
        request.put("description", "更新后的描述");
        request.put("priority", "MEDIUM");
        request.put("status", "IN_PROGRESS");

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/tasks/1",
                HttpMethod.PUT,
                createJsonAuthEntity(request),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(4)
    void testDeleteTask() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/tasks/2",
                HttpMethod.DELETE,
                createAuthEntity(),
                String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}