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

@Order(3)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProjectApiTest extends AuthenticatedApiTest {

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
    }

    @Test
    @Order(2)
    void testCreateProjectWithDuplicateName() {
        Map<String, String> request = new HashMap<>();
        request.put("name", "已存在的项目");
        request.put("description", "描述");

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
}