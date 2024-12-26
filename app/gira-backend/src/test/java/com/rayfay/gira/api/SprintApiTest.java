package com.rayfay.gira.api;

import com.rayfay.gira.api.dto.SprintRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SprintApiTest extends BaseApiTest {

    @Test
    void testCreateSprint() {
        // TC-005-1: 创建 Sprint
        SprintRequest request = new SprintRequest();
        request.setName("Sprint 1");
        request.setGoal("完成用户管理模块");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusWeeks(2));
        request.setStatus("PLANNING");
        request.setProjectId(1L);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<SprintRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL + "/sprints",
                entity,
                String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("id"));
        assertTrue(response.getBody().contains("name"));
    }

    @Test
    void testGetSprintDetails() {
        // TC-005-2: 获取 Sprint 详情
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/sprints/1",
                HttpMethod.GET,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("id"));
        assertTrue(response.getBody().contains("name"));
        assertTrue(response.getBody().contains("goal"));
    }

    @Test
    void testUpdateSprint() {
        // TC-005-3: 更新 Sprint
        SprintRequest request = new SprintRequest();
        request.setName("Updated Sprint");
        request.setGoal("更新后的目标");
        request.setEndDate(LocalDate.now().plusWeeks(3));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<SprintRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/sprints/1",
                HttpMethod.PUT,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Updated Sprint"));
    }

    @Test
    void testDeleteSprint() {
        // TC-005-4: 删除 Sprint
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/sprints/2",
                HttpMethod.DELETE,
                entity,
                String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testStartSprint() {
        // TC-005-5: 开始 Sprint
        SprintRequest request = new SprintRequest();
        request.setStatus("ACTIVE");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<SprintRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/sprints/1/start",
                HttpMethod.POST,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"status\":\"ACTIVE\""));
    }

    @Test
    void testCompleteSprint() {
        // TC-005-6: 完成 Sprint
        SprintRequest request = new SprintRequest();
        request.setStatus("COMPLETED");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<SprintRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/sprints/1/complete",
                HttpMethod.POST,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"status\":\"COMPLETED\""));
    }

    @Test
    void testGetProjectSprints() {
        // TC-005-7: 获取项目的 Sprint 列表
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/projects/1/sprints",
                HttpMethod.GET,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("content"));
        assertTrue(response.getBody().contains("totalElements"));
    }

    @Test
    void testGetActiveSprint() {
        // TC-005-8: 获取当前活动的 Sprint
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/projects/1/sprints/active",
                HttpMethod.GET,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"status\":\"ACTIVE\""));
    }

    @Test
    void testMoveBacklogItemsToSprint() {
        // TC-005-9: 将 Backlog 项移动到 Sprint
        SprintRequest request = new SprintRequest();
        request.setBacklogItemIds(new Long[] { 1L, 2L, 3L });

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<SprintRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/sprints/1/backlog-items",
                HttpMethod.POST,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testRemoveBacklogItemFromSprint() {
        // TC-005-10: 从 Sprint 移除 Backlog 项
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/sprints/1/backlog-items/1",
                HttpMethod.DELETE,
                entity,
                String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testCreateSprintWithInvalidDates() {
        // TC-005-11: 创建 Sprint - 日期无效
        SprintRequest request = new SprintRequest();
        request.setName("Sprint 1");
        request.setGoal("完成用户管理模块");
        request.setStartDate(LocalDate.now().plusDays(7));
        request.setEndDate(LocalDate.now()); // 结束日期早于开始日期
        request.setStatus("PLANNING");
        request.setProjectId(1L);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<SprintRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.postForEntity(
                    BASE_URL + "/sprints",
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.BadRequest");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("结束日期不能早于开始日期"));
        }
    }

    @Test
    void testCompleteSprintWithUnfinishedTasks() {
        // TC-005-12: 完成 Sprint - 存在未完成任务
        SprintRequest request = new SprintRequest();
        request.setStatus("COMPLETED");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<SprintRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.exchange(
                    BASE_URL + "/sprints/1/complete",
                    HttpMethod.POST,
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.BadRequest");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("Sprint 中还有未完成的任务"));
        }
    }

    @Test
    void testDeleteActiveSprint() {
        // TC-005-13: 删除活动中的 Sprint
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(
                    BASE_URL + "/sprints/1",
                    HttpMethod.DELETE,
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.BadRequest");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("无法删除活动中的 Sprint"));
        }
    }

    @Test
    void testCreateSprintWithNonexistentProject() {
        // TC-005-14: 创建 Sprint - 项目不存在
        SprintRequest request = new SprintRequest();
        request.setName("Sprint 1");
        request.setGoal("完成用户管理模块");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusWeeks(2));
        request.setStatus("PLANNING");
        request.setProjectId(999L); // 不存在的项目ID

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<SprintRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.postForEntity(
                    BASE_URL + "/sprints",
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.NotFound");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("项目不存在"));
        }
    }

    @Test
    void testStartSprintWithNoBacklogItems() {
        // TC-005-15: 开始 Sprint - 没有任何 Backlog 项
        SprintRequest request = new SprintRequest();
        request.setStatus("ACTIVE");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<SprintRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.exchange(
                    BASE_URL + "/sprints/2/start",
                    HttpMethod.POST,
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.BadRequest");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("Sprint 中没有任何 Backlog 项"));
        }
    }

    @Test
    void testGetNonexistentSprint() {
        // TC-005-16: 获取不存在的 Sprint
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(
                    BASE_URL + "/sprints/999",
                    HttpMethod.GET,
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.NotFound");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("Sprint 不存在"));
        }
    }
}