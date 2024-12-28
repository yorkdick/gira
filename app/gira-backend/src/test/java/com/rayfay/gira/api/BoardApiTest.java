package com.rayfay.gira.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rayfay.gira.api.dto.BoardColumnRequest;
import com.rayfay.gira.api.dto.BoardRequest;
import com.rayfay.gira.api.dto.LoginRequest;
import com.rayfay.gira.dto.ProjectDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class BoardApiTest extends BaseApiTest {

    private Long projectId;
    private Long boardId;

    @BeforeEach
    void setUp() throws Exception {
        // 创建测试项目
        ProjectDto projectDto = new ProjectDto();
        projectDto.setName("测试项目_" + System.currentTimeMillis());
        projectDto.setKey("TEST" + System.currentTimeMillis());
        projectDto.setDescription("这是一个测试项目");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ProjectDto> entity = new HttpEntity<>(projectDto, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/projects",
                HttpMethod.POST,
                entity,
                String.class);

        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        projectId = jsonNode.get("id").asLong();
    }

    @Test
    void testCreateBoard() {
        // 创建看板
        BoardRequest request = new BoardRequest();
        request.setName("测试看板");
        request.setDescription("这是一个测试看板");
        request.setProjectId(projectId);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BoardRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/boards",
                HttpMethod.POST,
                entity,
                String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        JsonNode jsonNode = assertDoesNotThrow(() -> objectMapper.readTree(response.getBody()));
        boardId = jsonNode.get("id").asLong();
        assertTrue(response.getBody().contains(request.getName()));
    }

    @Test
    void testGetBoard() {
        // 先创建看板
        testCreateBoard();

        // 获取看板详情
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/boards/" + boardId,
                HttpMethod.GET,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("测试看板"));
    }

    @Test
    void testUpdateBoard() {
        // 先创建看板
        testCreateBoard();

        // 更新看板
        BoardRequest request = new BoardRequest();
        request.setName("更新的看板名称");
        request.setDescription("更新的看板描述");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BoardRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/boards/" + boardId,
                HttpMethod.PUT,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("更新的看板名称"));
    }

    @Test
    void testCreateBoardColumn() {
        // 先创建看板
        testCreateBoard();

        // 创建看板列
        BoardColumnRequest request = new BoardColumnRequest();
        request.setName("待处理");
        request.setPosition(0);
        request.setBoardId(boardId);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BoardColumnRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/boards/" + boardId + "/columns",
                HttpMethod.POST,
                entity,
                String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("待处理"));
    }

    @Test
    void testGetBoardColumns() {
        // 先创建看板和列
        testCreateBoard();
        testCreateBoardColumn();

        // 获取看板列列表
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/boards/" + boardId + "/columns",
                HttpMethod.GET,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JsonNode jsonNode = assertDoesNotThrow(() -> objectMapper.readTree(response.getBody()));
        assertTrue(jsonNode.isArray());
        assertTrue(response.getBody().contains("待处理"));
    }

    @Test
    void testUpdateBoardColumn() {
        // 先创建看板和列
        testCreateBoard();
        testCreateBoardColumn();

        // 更新看板列
        BoardColumnRequest request = new BoardColumnRequest();
        request.setName("进行中");
        request.setPosition(1);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BoardColumnRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/boards/" + boardId + "/columns/1", // 假设列ID为1
                HttpMethod.PUT,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("进行中"));
    }

    @Test
    void testDeleteBoardColumn() {
        // 先创建看板和列
        testCreateBoard();
        testCreateBoardColumn();

        // 删除看板列
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/boards/" + boardId + "/columns/1", // 假设列ID为1
                HttpMethod.DELETE,
                entity,
                String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testArchiveBoard() {
        // 先创建看板
        testCreateBoard();

        // 归档看板
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/boards/" + boardId + "/archive",
                HttpMethod.POST,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JsonNode jsonNode = assertDoesNotThrow(() -> objectMapper.readTree(response.getBody()));
        assertTrue(jsonNode.get("archived").asBoolean());
    }
}