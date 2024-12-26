package com.rayfay.gira.api;

import com.rayfay.gira.api.dto.CommentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.*;

class CommentApiTest extends BaseApiTest {

    @Test
    void testCreateComment() {
        // TC-008-1: 创建评论
        CommentRequest request = new CommentRequest();
        request.setContent("这是一个测试评论");
        request.setTaskId(1L);
        request.setAuthorId(1L);
        request.setType("COMMENT");
        request.setStatus("ACTIVE");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<CommentRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL + "/comments",
                entity,
                String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("id"));
        assertTrue(response.getBody().contains("content"));
    }

    @Test
    void testGetCommentDetails() {
        // TC-008-2: 获取评论详情
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/comments/1",
                HttpMethod.GET,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("id"));
        assertTrue(response.getBody().contains("content"));
        assertTrue(response.getBody().contains("author"));
    }

    @Test
    void testUpdateComment() {
        // TC-008-3: 更新评论
        CommentRequest request = new CommentRequest();
        request.setContent("更新后的评论内容");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<CommentRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/comments/1",
                HttpMethod.PUT,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("更新后的评论内容"));
    }

    @Test
    void testDeleteComment() {
        // TC-008-4: 删除评论
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/comments/2",
                HttpMethod.DELETE,
                entity,
                String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testGetTaskComments() {
        // TC-008-5: 获取任务的评论列表
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/tasks/1/comments",
                HttpMethod.GET,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("content"));
        assertTrue(response.getBody().contains("totalElements"));
    }

    @Test
    void testReplyToComment() {
        // TC-008-6: 回复评论
        CommentRequest request = new CommentRequest();
        request.setContent("这是一个回复评论");
        request.setTaskId(1L);
        request.setAuthorId(1L);
        request.setParentId(1L);
        request.setType("REPLY");
        request.setStatus("ACTIVE");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<CommentRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL + "/comments",
                entity,
                String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("parentId"));
        assertTrue(response.getBody().contains("type\":\"REPLY\""));
    }

    @Test
    void testLikeComment() {
        // TC-008-7: 点赞评论
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/comments/1/like",
                HttpMethod.POST,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("likeCount"));
    }

    @Test
    void testUnlikeComment() {
        // TC-008-8: 取消点赞
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/comments/1/unlike",
                HttpMethod.POST,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("likeCount"));
    }

    @Test
    void testSearchComments() {
        // TC-008-9: 搜索评论
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/tasks/1/comments/search?keyword=测试&type=COMMENT",
                HttpMethod.GET,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("content"));
        assertTrue(response.getBody().contains("totalElements"));
    }

    @Test
    void testReplyToNonexistentComment() {
        // TC-008-10: 回复评论 - 评论不存在
        CommentRequest request = new CommentRequest();
        request.setContent("这是一个回复评论");
        request.setTaskId(1L);
        request.setAuthorId(1L);
        request.setParentId(999L); // 不存在的评论ID
        request.setType("REPLY");
        request.setStatus("ACTIVE");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<CommentRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.postForEntity(
                    BASE_URL + "/comments",
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.NotFound");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("父评论不存在"));
        }
    }

    @Test
    void testUpdateDeletedComment() {
        // TC-008-11: 更新评论 - 评论已删除
        CommentRequest request = new CommentRequest();
        request.setContent("更新已删除的评论");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<CommentRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.exchange(
                    BASE_URL + "/comments/3", // 假设评论3已被删除
                    HttpMethod.PUT,
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.BadRequest");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("评论已被删除"));
        }
    }

    @Test
    void testLikeOwnComment() {
        // TC-008-12: 点赞评论 - 点赞自己的评论
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(
                    BASE_URL + "/comments/4/like", // 假设评论4是当前用户创建的
                    HttpMethod.POST,
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.BadRequest");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("不能点赞自己的评论"));
        }
    }

    @Test
    void testCreateCommentWithNonexistentTask() {
        // TC-008-13: 创建评论 - 任务不存在
        CommentRequest request = new CommentRequest();
        request.setContent("这是一个测试评论");
        request.setTaskId(999L); // 不存在的任务ID
        request.setAuthorId(1L);
        request.setType("COMMENT");
        request.setStatus("ACTIVE");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<CommentRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.postForEntity(
                    BASE_URL + "/comments",
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.NotFound");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("任务不存在"));
        }
    }

    @Test
    void testReplyToDeletedComment() {
        // TC-008-14: 回复评论 - 回复已删除的评论
        CommentRequest request = new CommentRequest();
        request.setContent("这是一个回复评论");
        request.setTaskId(1L);
        request.setAuthorId(1L);
        request.setParentId(5L); // 假设评论5已被删除
        request.setType("REPLY");
        request.setStatus("ACTIVE");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<CommentRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.postForEntity(
                    BASE_URL + "/comments",
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.BadRequest");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("不能回复已删除的评论"));
        }
    }

    @Test
    void testDuplicateLikeComment() {
        // TC-008-15: 点赞评论 - 重复点赞
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        // 第一次点赞
        restTemplate.exchange(
                BASE_URL + "/comments/1/like",
                HttpMethod.POST,
                entity,
                String.class);

        // 第二次点赞
        try {
            restTemplate.exchange(
                    BASE_URL + "/comments/1/like",
                    HttpMethod.POST,
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.BadRequest");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("已经点赞过该评论"));
        }
    }
}