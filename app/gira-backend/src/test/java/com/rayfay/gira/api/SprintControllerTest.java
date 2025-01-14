package com.rayfay.gira.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rayfay.gira.dto.request.CreateSprintRequest;
import com.rayfay.gira.dto.request.UpdateSprintRequest;
import com.rayfay.gira.repository.BoardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = { "/sql/sprint/init_sprint_test.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = { "/sql/sprint/cleanup_sprint_test.sql" }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class SprintControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BoardRepository boardRepository;

    private static Long sprintId;
    private static Long sprintId2;
    private static Long boardId;

    @BeforeEach
    void setUp() {
        if (boardId == null) {
            boardId = boardRepository.findByName("测试看板")
                    .orElseThrow(() -> new RuntimeException("测试看板未找到")).getId();
        }
    }

    @Test
    @Order(1)
    @WithMockUser(username = "manager", roles = { "ADMIN" })
    void createSprint_WithValidRequest_ShouldReturnCreatedSprint() throws Exception {
        CreateSprintRequest request = new CreateSprintRequest();
        request.setName("Sprint 1");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusWeeks(2));

        MvcResult result = mockMvc.perform(post("/api/sprints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sprint 1"))
                .andExpect(jsonPath("$.status").value("PLANNING"))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        sprintId = objectMapper.readTree(response).get("id").asLong();

        CreateSprintRequest request2 = new CreateSprintRequest();
        request2.setName("Another Sprint");
        request2.setStartDate(LocalDate.now());
        request2.setEndDate(LocalDate.now().plusWeeks(2));

        MvcResult result2 = mockMvc.perform(post("/api/sprints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk())
                .andReturn();
        String response2 = result2.getResponse().getContentAsString();
        sprintId2 = objectMapper.readTree(response2).get("id").asLong();
    }

    @Test
    @Order(2)
    @WithMockUser(username = "developer", roles = "DEVELOPER")
    void getSprint_WithValidId_ShouldReturnSprint() throws Exception {

        mockMvc.perform(get("/api/sprints/{id}", sprintId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sprintId))
                .andExpect(jsonPath("$.name").value("Sprint 1"))
                .andExpect(jsonPath("$.status").value("PLANNING"));
    }

    @Test
    @Order(3)
    @WithMockUser(username = "developer", roles = "DEVELOPER")
    void getSprint_WithInvalidId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/sprints/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Sprint不存在"));
    }

    @Test
    @Order(4)
    @WithMockUser(username = "developer", roles = "DEVELOPER")
    void getAllSprintsByBoard_ShouldReturnPageOfSprints() throws Exception {
        mockMvc.perform(get("/api/sprints/boards/{boardId}/sprints", boardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").exists())
                .andExpect(jsonPath("$.content[0].status").exists());
    }

    @Test
    @Order(5)
    @WithMockUser(username = "manager", roles = { "ADMIN" })
    void updateSprint_WithValidRequest_ShouldReturnUpdatedSprint() throws Exception {
        UpdateSprintRequest request = new UpdateSprintRequest();
        request.setName("Updated Sprint");
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusWeeks(3));

        mockMvc.perform(put("/api/sprints/{id}", sprintId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Sprint"));
    }

    @Test
    @Order(6)
    @WithMockUser(username = "developer", roles = "DEVELOPER")
    void updateSprint_WithoutAdminRole_ShouldReturnForbidden() throws Exception {
        UpdateSprintRequest request = new UpdateSprintRequest();
        request.setName("Updated Sprint");
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusWeeks(3));

        mockMvc.perform(put("/api/sprints/{id}", sprintId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(7)
    @WithMockUser(username = "manager", roles = { "ADMIN" })
    void startSprint_WithValidId_ShouldReturnOk() throws Exception {
        mockMvc.perform(put("/api/sprints/{id}/start", sprintId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @Order(8)
    @WithMockUser(username = "manager", roles = { "ADMIN" })
    void startSprint_WhenAlreadyActive_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(put("/api/sprints/{id}/start", sprintId2))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("已存在活动中的Sprint"))
                .andDo(print());
    }

    @Test
    @Order(9)
    @WithMockUser(username = "manager", roles = { "ADMIN" })
    void completeSprint_WithValidId_ShouldReturnOk() throws Exception {
        mockMvc.perform(put("/api/sprints/{id}/complete", sprintId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @Order(10)
    @WithMockUser(username = "developer", roles = "DEVELOPER")
    void completeSprint_WithoutAdminRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(put("/api/sprints/{id}/complete", sprintId))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(11)
    @WithMockUser(username = "manager", roles = { "ADMIN" })
    void completeSprint_WhenNotActive_ShouldReturnBadRequest() throws Exception {
        CreateSprintRequest request = new CreateSprintRequest();
        request.setName("Planning Sprint");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusWeeks(2));

        MvcResult result = mockMvc.perform(post("/api/sprints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Long newId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(put("/api/sprints/{id}/complete", newId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("只能完成活动中的Sprint"));
    }

    @Test
    @Order(12)
    @WithMockUser(username = "developer", roles = "DEVELOPER")
    void startSprint_WithoutAdminRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(put("/api/sprints/{id}/start", sprintId))
                .andExpect(status().isForbidden());
    }

    // 新增异常处理测试用例
    @Test
    @Order(13)
    @WithMockUser(username = "manager", roles = { "ADMIN" })
    void createSprint_WithInvalidDateRange_ShouldReturnBadRequest() throws Exception {
        CreateSprintRequest request = new CreateSprintRequest();
        request.setName("Invalid Sprint");
        request.setStartDate(LocalDate.now().plusWeeks(2));
        request.setEndDate(LocalDate.now().plusWeeks(1));

        mockMvc.perform(post("/api/sprints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("开始日期不能晚于结束日期"));
    }

    @Test
    @Order(14)
    @WithMockUser(username = "manager", roles = { "ADMIN" })
    void createSprint_WithPastStartDate_ShouldReturnBadRequest() throws Exception {
        CreateSprintRequest request = new CreateSprintRequest();
        request.setName("Past Sprint");
        request.setStartDate(LocalDate.now().minusDays(1));
        request.setEndDate(LocalDate.now().plusWeeks(2));

        mockMvc.perform(post("/api/sprints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("开始日期不能早于今天"));
    }
}