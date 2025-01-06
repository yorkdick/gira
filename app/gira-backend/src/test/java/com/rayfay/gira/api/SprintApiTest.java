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

@Order(7)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SprintApiTest extends AuthenticatedApiTest {

    @Test
    @Order(1)
    void testCreateSprint() {
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Sprint 1");
        request.put("goal", "完成用户认证功能");
        request.put("startDate", "2024-01-01");
        request.put("endDate", "2024-01-14");
        request.put("projectId", 1L);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/sprints",
                HttpMethod.POST,
                createJsonAuthEntity(request),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("id"));
    }

    @Test
    @Order(2)
    void testGetSprintList() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/projects/1/sprints",
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
        request.put("endDate", "2024-01-21");

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/sprints/1",
                HttpMethod.PUT,
                createJsonAuthEntity(request),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(4)
    void testStartSprint() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/sprints/1/start",
                HttpMethod.POST,
                createAuthEntity(),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(5)
    void testCompleteSprint() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/sprints/1/complete",
                HttpMethod.POST,
                createAuthEntity(),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}