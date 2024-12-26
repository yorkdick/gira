package com.rayfay.gira.api;

import com.rayfay.gira.api.dto.BacklogItemRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.*;

class BacklogApiTest extends BaseApiTest {

    @Test
    void testCreateBacklogItem() {
        // TC-006-1: 创建 Backlog 项
        BacklogItemRequest request = new BacklogItemRequest();
        request.setTitle("实现用户登录功能");
        request.setDescription("实现用户的登录和认证功能");
        request.setPriority("HIGH");
        request.setType("STORY");
        request.setProjectId(1L);
        request.setAssigneeId(2L);
        request.setReporterId(1L);
        request.setEstimatedHours(8.0);
        request.setStatus("TODO");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<BacklogItemRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL + "/backlog-items",
                entity,
                String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("id"));
        assertTrue(response.getBody().contains("title"));
    }

    @Test
    void testGetBacklogItemDetails() {
        // TC-006-2: 获取 Backlog 项详情
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/backlog-items/1",
                HttpMethod.GET,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("id"));
        assertTrue(response.getBody().contains("title"));
        assertTrue(response.getBody().contains("description"));
    }

    @Test
    void testUpdateBacklogItem() {
        // TC-006-3: 更新 Backlog 项
        BacklogItemRequest request = new BacklogItemRequest();
        request.setTitle("更新后的标题");
        request.setDescription("更新后的描述");
        request.setPriority("MEDIUM");
        request.setEstimatedHours(16.0);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<BacklogItemRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/backlog-items/1",
                HttpMethod.PUT,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("更新后的标题"));
    }

    @Test
    void testDeleteBacklogItem() {
        // TC-006-4: 删除 Backlog 项
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/backlog-items/2",
                HttpMethod.DELETE,
                entity,
                String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testGetProjectBacklogItems() {
        // TC-006-5: 获取项目的 Backlog 列表
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/projects/1/backlog-items",
                HttpMethod.GET,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("content"));
        assertTrue(response.getBody().contains("totalElements"));
    }

    @Test
    void testAssignBacklogItem() {
        // TC-006-6: 分配 Backlog 项
        BacklogItemRequest request = new BacklogItemRequest();
        request.setAssigneeId(3L);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<BacklogItemRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/backlog-items/1/assign",
                HttpMethod.PUT,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"assigneeId\":3"));
    }

    @Test
    void testMoveToSprint() {
        // TC-006-7: 移动到 Sprint
        BacklogItemRequest request = new BacklogItemRequest();
        request.setSprintId(1L);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<BacklogItemRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/backlog-items/1/move-to-sprint",
                HttpMethod.PUT,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"sprintId\":1"));
    }

    @Test
    void testUpdatePriority() {
        // TC-006-8: 更新优先级
        BacklogItemRequest request = new BacklogItemRequest();
        request.setPriority("HIGH");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<BacklogItemRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/backlog-items/1/priority",
                HttpMethod.PUT,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"priority\":\"HIGH\""));
    }

    @Test
    void testUpdateStatus() {
        // TC-006-9: 更新状态
        BacklogItemRequest request = new BacklogItemRequest();
        request.setStatus("IN_PROGRESS");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<BacklogItemRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/backlog-items/1/status",
                HttpMethod.PUT,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"status\":\"IN_PROGRESS\""));
    }

    @Test
    void testSearchBacklogItems() {
        // TC-006-10: 搜索 Backlog 项
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/projects/1/backlog-items/search?keyword=登录&type=STORY&priority=HIGH",
                HttpMethod.GET,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("content"));
        assertTrue(response.getBody().contains("totalElements"));
    }

    @Test
    void testCreateBacklogItemWithNonexistentProject() {
        // TC-006-11: 创建 Backlog 项 - 项目不存在
        BacklogItemRequest request = new BacklogItemRequest();
        request.setTitle("实现用户登录功能");
        request.setDescription("实现用户的登录和认证功能");
        request.setPriority("HIGH");
        request.setType("STORY");
        request.setProjectId(999L); // 不存在的项目ID
        request.setAssigneeId(2L);
        request.setReporterId(1L);
        request.setEstimatedHours(8.0);
        request.setStatus("TODO");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<BacklogItemRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.postForEntity(
                    BASE_URL + "/backlog-items",
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.NotFound");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("项目不存在"));
        }
    }

    @Test
    void testMoveToCompletedSprint() {
        // TC-006-12: 移动到 Sprint - Sprint 已完成
        BacklogItemRequest request = new BacklogItemRequest();
        request.setSprintId(2L); // 假设 Sprint 2 已完成

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<BacklogItemRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.exchange(
                    BASE_URL + "/backlog-items/1/move-to-sprint",
                    HttpMethod.PUT,
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.BadRequest");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("无法移动到已完成的 Sprint"));
        }
    }

    @Test
    void testUpdateStatusWithInvalidTransition() {
        // TC-006-13: 更新状态 - 状态转换无效
        BacklogItemRequest request = new BacklogItemRequest();
        request.setStatus("DONE"); // 直接从 TODO 到 DONE，跳过了 IN_PROGRESS

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<BacklogItemRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.exchange(
                    BASE_URL + "/backlog-items/1/status",
                    HttpMethod.PUT,
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.BadRequest");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("无效的状态转换"));
        }
    }

    @Test
    void testAssignBacklogItemToNonexistentUser() {
        // TC-006-14: 分配 Backlog 项 - 用户不存在
        BacklogItemRequest request = new BacklogItemRequest();
        request.setAssigneeId(999L); // 不存在的用户ID

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<BacklogItemRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.exchange(
                    BASE_URL + "/backlog-items/1/assign",
                    HttpMethod.PUT,
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.NotFound");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("用户不存在"));
        }
    }

    @Test
    void testUpdateNonexistentBacklogItem() {
        // TC-006-15: 更新 Backlog 项 - Backlog 项不存在
        BacklogItemRequest request = new BacklogItemRequest();
        request.setTitle("更新后的标题");
        request.setDescription("更新后的描述");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<BacklogItemRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.exchange(
                    BASE_URL + "/backlog-items/999",
                    HttpMethod.PUT,
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.NotFound");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("Backlog 项不存在"));
        }
    }

    @Test
    void testCreateBacklogItemWithNegativeEstimatedHours() {
        // TC-006-16: 创建 Backlog 项 - 估算时间为负数
        BacklogItemRequest request = new BacklogItemRequest();
        request.setTitle("实现用户登录功能");
        request.setDescription("实现用户的登录和认证功能");
        request.setPriority("HIGH");
        request.setType("STORY");
        request.setProjectId(1L);
        request.setAssigneeId(2L);
        request.setReporterId(1L);
        request.setEstimatedHours(-8.0); // 负数的估算时间
        request.setStatus("TODO");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<BacklogItemRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.postForEntity(
                    BASE_URL + "/backlog-items",
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.BadRequest");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("估算时间不能为负数"));
        }
    }
}