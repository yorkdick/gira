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

@Order(4)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BoardApiTest extends AuthenticatedApiTest {

    @Test
    @Order(1)
    void testCreateBoard() {
        Map<String, Object> request = new HashMap<>();
        request.put("name", "开发看板");
        request.put("projectId", 1L);
        request.put("type", "SCRUM");

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/boards",
                HttpMethod.POST,
                createJsonAuthEntity(request),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("id"));
    }

    @Test
    @Order(2)
    void testGetBoardList() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/projects/1/boards",
                HttpMethod.GET,
                createAuthEntity(),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("content"));
    }

    @Test
    @Order(3)
    void testUpdateBoardSettings() {
        Map<String, Object> request = new HashMap<>();
        request.put("defaultAssigneeId", 1L);
        request.put("defaultPriority", "MEDIUM");
        request.put("defaultType", "TASK");

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/boards/1/settings",
                HttpMethod.PUT,
                createJsonAuthEntity(request),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(4)
    void testDeleteBoard() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/boards/2",
                HttpMethod.DELETE,
                createAuthEntity(),
                String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}