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

@Order(8)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CommentApiTest extends AuthenticatedApiTest {

    @Test
    @Order(1)
    void testCreateComment() {
        Map<String, Object> request = new HashMap<>();
        request.put("content", "这是一条测试评论");
        request.put("taskId", 1L);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/comments",
                HttpMethod.POST,
                createJsonAuthEntity(request),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("id"));
    }

    @Test
    @Order(2)
    void testGetCommentList() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/tasks/1/comments",
                HttpMethod.GET,
                createAuthEntity(),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("content"));
    }

    @Test
    @Order(3)
    void testUpdateComment() {
        Map<String, Object> request = new HashMap<>();
        request.put("content", "更新后的评论内容");

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/comments/1",
                HttpMethod.PUT,
                createJsonAuthEntity(request),
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(4)
    void testDeleteComment() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/comments/2",
                HttpMethod.DELETE,
                createAuthEntity(),
                String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}