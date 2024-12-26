package com.rayfay.gira.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rayfay.gira.api.dto.BoardColumnRequest;
import com.rayfay.gira.api.dto.BoardRequest;
import com.rayfay.gira.api.dto.LoginRequest;
import com.rayfay.gira.api.dto.ProjectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class BoardApiTest {

    private final String BASE_URL = "http://localhost:8080/api/v1";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String validToken;
    private Long projectId;
    private Long boardId;

    @BeforeEach
    void setUp() throws Exception {
        // 获取有效token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("1qaz@WSX");
        loginRequest.setRememberMe(false);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL + "/auth/login",
                loginRequest,
                String.class);

        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        validToken = jsonNode.get("token").asText();

        // 创建测试项目
        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setName("测试项目_" + System.currentTimeMillis());
        projectRequest.setKey("TEST" + System.currentTimeMillis());
        projectRequest.setDescription("这是一个测试项目");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(validToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ProjectRequest> entity = new HttpEntity<>(projectRequest, headers);

        response = restTemplate.exchange(
                BASE_URL + "/projects",
                HttpMethod.POST,
                entity,
                String.class);

        jsonNode = objectMapper.readTree(response.getBody());
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
        headers.setBearerAuth(validToken);
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
        headers.setBearerAuth(validToken);
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
        headers.setBearerAuth(validToken);
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
        headers.setBearerAuth(validToken);
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
        headers.setBearerAuth(validToken);
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
        headers.setBearerAuth(validToken);
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
        headers.setBearerAuth(validToken);
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
        headers.setBearerAuth(validToken);
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