package com.rayfay.gira.api;

import com.rayfay.gira.api.dto.TaskRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TaskApiTest extends BaseApiTest {
    @Test
    void testCreateTask() {
        // TC-007-1: 创建任务
        TaskRequest request = new TaskRequest();
        request.setTitle("实现用户注册功能");
        request.setDescription("实现新用户的注册流程");
        request.setPriority("HIGH");
        request.setStatus("TODO");
        request.setType("TASK");
        request.setProjectId(1L);
        request.setColumnId(1L);
        request.setAssigneeId(2L);
        request.setReporterId(1L);
        request.setEstimatedHours(4.0);
        request.setDueDate(LocalDate.now().plusDays(7));
        request.setPosition(1);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<TaskRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL + "/tasks",
                entity,
                String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("id"));
        assertTrue(response.getBody().contains("title"));
    }

    @Test
    void testGetTaskDetails() {
        // TC-007-2: 获取任务详情
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/tasks/1",
                HttpMethod.GET,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("id"));
        assertTrue(response.getBody().contains("title"));
        assertTrue(response.getBody().contains("description"));
    }

    @Test
    void testUpdateTask() {
        // TC-007-3: 更新任务
        TaskRequest request = new TaskRequest();
        request.setTitle("更新后的任务标题");
        request.setDescription("更新后的任务描述");
        request.setPriority("MEDIUM");
        request.setEstimatedHours(6.0);
        request.setDueDate(LocalDate.now().plusDays(14));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<TaskRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/tasks/1",
                HttpMethod.PUT,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("更新后的任务标题"));
    }

    @Test
    void testDeleteTask() {
        // TC-007-4: 删除任务
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/tasks/2",
                HttpMethod.DELETE,
                entity,
                String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testMoveTask() {
        // TC-007-5: 移动任务
        TaskRequest request = new TaskRequest();
        request.setColumnId(2L);
        request.setPosition(1);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<TaskRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/tasks/1/move",
                HttpMethod.PUT,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"columnId\":2"));
    }

    @Test
    void testAssignTask() {
        // TC-007-6: 分配任务
        TaskRequest request = new TaskRequest();
        request.setAssigneeId(3L);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<TaskRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/tasks/1/assign",
                HttpMethod.PUT,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"assigneeId\":3"));
    }

    @Test
    void testUpdateTaskStatus() {
        // TC-007-7: 更改任务状态
        TaskRequest request = new TaskRequest();
        request.setStatus("IN_PROGRESS");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<TaskRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/tasks/1/status",
                HttpMethod.PUT,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"status\":\"IN_PROGRESS\""));
    }

    @Test
    void testGetBoardTasks() {
        // TC-007-8: 获取看板任务列表
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/projects/1/board/tasks",
                HttpMethod.GET,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("columns"));
        assertTrue(response.getBody().contains("tasks"));
    }

    @Test
    void testUpdateTaskPriority() {
        // TC-007-9: 更新任务优先级
        TaskRequest request = new TaskRequest();
        request.setPriority("HIGH");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<TaskRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/tasks/1/priority",
                HttpMethod.PUT,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"priority\":\"HIGH\""));
    }

    @Test
    void testSearchTasks() {
        // TC-007-10: 搜索任务
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/projects/1/tasks/search?keyword=注册&type=TASK&priority=HIGH",
                HttpMethod.GET,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("content"));
        assertTrue(response.getBody().contains("totalElements"));
    }

    @Test
    void testCreateTaskWithNonexistentColumn() {
        // TC-007-11: 创建任务 - 列不存在
        TaskRequest request = new TaskRequest();
        request.setTitle("实现用户注册功能");
        request.setDescription("实现新用户的注册流程");
        request.setPriority("HIGH");
        request.setStatus("TODO");
        request.setType("TASK");
        request.setProjectId(1L);
        request.setColumnId(999L); // 不存在的列ID
        request.setAssigneeId(2L);
        request.setReporterId(1L);
        request.setEstimatedHours(4.0);
        request.setDueDate(LocalDate.now().plusDays(7));
        request.setPosition(1);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<TaskRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.postForEntity(
                    BASE_URL + "/tasks",
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.NotFound");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("列不存在"));
        }
    }

    @Test
    void testMoveTaskToFullColumn() {
        // TC-007-12: 移动任务 - 列已满
        TaskRequest request = new TaskRequest();
        request.setColumnId(3L); // 假设列3已达到任务数量限制
        request.setPosition(1);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<TaskRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.exchange(
                    BASE_URL + "/tasks/1/move",
                    HttpMethod.PUT,
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.BadRequest");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("列已达到任务数量限制"));
        }
    }

    @Test
    void testAssignTaskToNonexistentUser() {
        // TC-007-13: 分配任务 - 用户不存在
        TaskRequest request = new TaskRequest();
        request.setAssigneeId(999L); // 不存在的用户ID

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<TaskRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.exchange(
                    BASE_URL + "/tasks/1/assign",
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
    void testCreateTaskWithPastDueDate() {
        // TC-007-14: 创建任务 - 截止日期早于当前日期
        TaskRequest request = new TaskRequest();
        request.setTitle("实现用户注册功能");
        request.setDescription("实现新用户的注册流程");
        request.setPriority("HIGH");
        request.setStatus("TODO");
        request.setType("TASK");
        request.setProjectId(1L);
        request.setColumnId(1L);
        request.setAssigneeId(2L);
        request.setReporterId(1L);
        request.setEstimatedHours(4.0);
        request.setDueDate(LocalDate.now().minusDays(1)); // 过去的日期
        request.setPosition(1);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<TaskRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.postForEntity(
                    BASE_URL + "/tasks",
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.BadRequest");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("截止日期不能早于当前日期"));
        }
    }

    @Test
    void testUpdateTaskWithZeroEstimatedHours() {
        // TC-007-15: 更新任务 - 估算时间为零
        TaskRequest request = new TaskRequest();
        request.setTitle("更新后的任务标题");
        request.setDescription("更新后的任务描述");
        request.setEstimatedHours(0.0); // 零估算时间

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<TaskRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.exchange(
                    BASE_URL + "/tasks/1",
                    HttpMethod.PUT,
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.BadRequest");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("估算时间必须大于零"));
        }
    }

    @Test
    void testMoveTaskToDifferentProjectColumn() {
        // TC-007-16: 移动任务 - 移动到不同项目的列
        TaskRequest request = new TaskRequest();
        request.setColumnId(5L); // 假设列5属于不同的项目
        request.setPosition(1);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<TaskRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.exchange(
                    BASE_URL + "/tasks/1/move",
                    HttpMethod.PUT,
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.BadRequest");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("不能移动任务到不同项目的列"));
        }
    }
}