package com.rayfay.gira.api;

import com.rayfay.gira.api.dto.ProjectRequest;
import com.rayfay.gira.api.dto.ProjectSettingsRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.*;

class ProjectApiTest extends BaseApiTest {

    @Test
    void testCreateProject() {
        // TC-003-1: 创建项目
        ProjectRequest request = new ProjectRequest();
        request.setName("Test Project");
        request.setKey("TEST");
        request.setDescription("This is a test project");
        request.setLeaderId(1L);
        request.setType("SOFTWARE");
        request.setTemplate("SCRUM");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<ProjectRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL + "/projects",
                entity,
                String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("id"));
        assertTrue(response.getBody().contains("name"));
    }

    @Test
    void testCreateProjectWithDuplicateKey() {
        // TC-003-2: 创建项目 - 项目键重复
        ProjectRequest request = new ProjectRequest();
        request.setName("Test Project");
        request.setKey("TEST");
        request.setDescription("This is a test project");
        request.setLeaderId(1L);
        request.setType("SOFTWARE");
        request.setTemplate("SCRUM");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<ProjectRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.postForEntity(
                    BASE_URL + "/projects",
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.BadRequest");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("项目键已存在"));
        }
    }

    @Test
    void testGetProjectDetails() {
        // TC-003-3: 获取项目详情
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/projects/1",
                HttpMethod.GET,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("id"));
        assertTrue(response.getBody().contains("name"));
        assertTrue(response.getBody().contains("key"));
    }

    @Test
    void testUpdateProject() {
        // TC-003-4: 更新项目
        ProjectRequest request = new ProjectRequest();
        request.setName("Updated Project");
        request.setDescription("This is an updated project");
        request.setLeaderId(2L);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<ProjectRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/projects/1",
                HttpMethod.PUT,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Updated Project"));
    }

    @Test
    void testDeleteProject() {
        // TC-003-5: 删除项目
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/projects/2",
                HttpMethod.DELETE,
                entity,
                String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testGetProjectList() {
        // TC-003-6: 获取项目列表
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/projects",
                HttpMethod.GET,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("content"));
        assertTrue(response.getBody().contains("totalElements"));
    }

    @Test
    void testGetProjectMembers() {
        // TC-003-7: 获取项目成员列表
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/projects/1/members",
                HttpMethod.GET,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("id"));
        assertTrue(response.getBody().contains("username"));
        assertTrue(response.getBody().contains("email"));
    }

    @Test
    void testAddProjectMember() {
        // TC-003-8: 添加项目成员
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/projects/1/members/3",
                HttpMethod.POST,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testRemoveProjectMember() {
        // TC-003-9: 移除项目成员
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/projects/1/members/3",
                HttpMethod.DELETE,
                entity,
                String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testCreateProjectWithInvalidKey() {
        // TC-003-10: 创建项目 - 项目键格式无效
        ProjectRequest request = new ProjectRequest();
        request.setName("Test Project");
        request.setKey("invalid key"); // 包含空格，不符合规范
        request.setDescription("This is a test project");
        request.setLeaderId(1L);
        request.setType("SOFTWARE");
        request.setTemplate("SCRUM");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<ProjectRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.postForEntity(
                    BASE_URL + "/projects",
                    entity,
                    String.class);
            fail("Expected HttpClientErrorException.BadRequest");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("项目键不符合规范"));
        }
    }

    @Test
    void testUpdateProjectSettings() {
        // TC-003-11: 更新项目设置
        ProjectSettingsRequest request = new ProjectSettingsRequest();
        request.setAllowGuestAccess(true);
        request.setRequireApprovalForJoin(true);
        request.setDefaultRole("MEMBER");
        request.setWorkflowType("SCRUM");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<ProjectSettingsRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/projects/1/settings",
                HttpMethod.PUT,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"allowGuestAccess\":true"));
        assertTrue(response.getBody().contains("\"requireApprovalForJoin\":true"));
        assertTrue(response.getBody().contains("\"defaultRole\":\"MEMBER\""));
        assertTrue(response.getBody().contains("\"workflowType\":\"SCRUM\""));
    }

    @Test
    void testSearchProjects() {
        // TC-003-12: 搜索项目
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/projects/search?keyword=test&status=ACTIVE",
                HttpMethod.GET,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("content"));
        assertTrue(response.getBody().contains("totalElements"));
    }

    @Test
    void testCheckProjectPermission() {
        // TC-003-13: 检查项目权限
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/projects/1/permissions/MANAGE_MEMBERS",
                HttpMethod.GET,
                entity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("hasPermission"));
    }
}