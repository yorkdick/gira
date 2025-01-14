package com.rayfay.gira.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rayfay.gira.dto.request.CreateTaskRequest;
import com.rayfay.gira.dto.request.UpdateTaskRequest;
import com.rayfay.gira.entity.TaskPriority;
import com.rayfay.gira.entity.TaskStatus;
import com.rayfay.gira.repository.BoardColumnRepository;
import com.rayfay.gira.repository.BoardRepository;
import com.rayfay.gira.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = { "/sql/task/init_task_test.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = { "/sql/task/cleanup_task_test.sql" }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class TaskControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private BoardRepository boardRepository;

        @Autowired
        private BoardColumnRepository boardColumnRepository;

        @Autowired
        private UserRepository userRepository;

        private static Long boardId;
        private static Long columnId;
        private static Long taskId;
        private static Long userId;

        @BeforeEach
        void setUp() {
                if (boardId == null) {
                        boardId = boardRepository.findByName("测试看板")
                                        .orElseThrow(() -> new RuntimeException("测试看板未找到")).getId();
                        columnId = boardColumnRepository.findByBoardIdAndName(boardId, "待办")
                                        .orElseThrow(() -> new RuntimeException("看板列未找到")).getId();
                        userId = userRepository.findByUsername("developer")
                                        .orElseThrow(() -> new RuntimeException("测试用户未找到")).getId();
                }
        }

        @Test
        @Order(1)
        @WithMockUser(username = "developer", roles = "DEVELOPER")
        void createTask_WithValidRequest_ShouldReturnCreatedTask() throws Exception {
                CreateTaskRequest request = new CreateTaskRequest();
                request.setTitle("Test Task");
                request.setDescription("Test Description");
                request.setPriority(TaskPriority.MEDIUM);
                request.setBoardId(boardId);
                request.setColumnId(columnId);

                MvcResult result = mockMvc.perform(post("/api/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.title").value("Test Task"))
                                .andExpect(jsonPath("$.description").value("Test Description"))
                                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                                .andExpect(jsonPath("$.status").value("TODO"))
                                .andReturn();

                String response = result.getResponse().getContentAsString();
                taskId = objectMapper.readTree(response).get("id").asLong();
        }

        @Test
        @Order(2)
        @WithMockUser(username = "developer", roles = "DEVELOPER")
        void createTask_WithoutTitle_ShouldReturnBadRequest() throws Exception {
                CreateTaskRequest request = new CreateTaskRequest();
                request.setDescription("Test Description");
                request.setPriority(TaskPriority.MEDIUM);
                request.setBoardId(boardId);
                request.setColumnId(columnId);

                mockMvc.perform(post("/api/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("任务标题不能为空"));
        }

        @Test
        @Order(3)
        @WithMockUser(username = "developer", roles = "DEVELOPER")
        void createTask_WithInvalidBoard_ShouldReturnBadRequest() throws Exception {
                CreateTaskRequest request = new CreateTaskRequest();
                request.setTitle("Test Task");
                request.setDescription("Test Description");
                request.setPriority(TaskPriority.MEDIUM);
                request.setBoardId(999L);
                request.setColumnId(columnId);

                mockMvc.perform(post("/api/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("看板不存在"));
        }

        @Test
        @Order(4)
        @WithMockUser(username = "developer", roles = "DEVELOPER")
        void createTask_WithAssignee_ShouldReturnCreatedTask() throws Exception {
                CreateTaskRequest request = new CreateTaskRequest();
                request.setTitle("Assigned Task");
                request.setDescription("Task with Assignee");
                request.setPriority(TaskPriority.HIGH);
                request.setBoardId(boardId);
                request.setColumnId(columnId);
                request.setAssigneeId(userId);

                mockMvc.perform(post("/api/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.title").value("Assigned Task"))
                                .andExpect(jsonPath("$.assignee.id").value(userId));
        }

        @Test
        @Order(5)
        @WithMockUser(username = "developer", roles = "DEVELOPER")
        void getTask_WithValidId_ShouldReturnTask() throws Exception {
                mockMvc.perform(get("/api/tasks/{id}", taskId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(taskId))
                                .andExpect(jsonPath("$.title").value("Test Task"));
        }

        @Test
        @Order(6)
        @WithMockUser(username = "developer", roles = "DEVELOPER")
        void getTask_WithInvalidId_ShouldReturnNotFound() throws Exception {
                mockMvc.perform(get("/api/tasks/{id}", 999L))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(404))
                                .andExpect(jsonPath("$.message").value("任务不存在"));
        }

        @Test
        @Order(7)
        @WithMockUser(username = "developer", roles = "DEVELOPER")
        void getAllTasksByBoard_ShouldReturnPageOfTasks() throws Exception {
                mockMvc.perform(get("/api/tasks/boards/{boardId}", boardId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray())
                                .andExpect(jsonPath("$.content[0].title").exists());
        }

        @Test
        @Order(8)
        @WithMockUser(username = "developer", roles = "DEVELOPER")
        void getAllTasksByAssignee_ShouldReturnPageOfTasks() throws Exception {
                mockMvc.perform(get("/api/tasks/assignee/{assigneeId}", userId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @Order(9)
        @WithMockUser(username = "developer", roles = "DEVELOPER")
        void getAllTasksByStatus_ShouldReturnPageOfTasks() throws Exception {
                mockMvc.perform(get("/api/tasks")
                                .param("status", "TODO")
                                .param("priority", "MEDIUM"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray())
                                .andExpect(jsonPath("$.content[0].status").value("TODO"))
                                .andExpect(jsonPath("$.content[0].priority").value("MEDIUM"));
        }

        @Test
        @Order(10)
        @WithMockUser(username = "developer", roles = "DEVELOPER")
        void updateTask_ToInProgress_ShouldReturnUpdatedTask() throws Exception {
                UpdateTaskRequest request = new UpdateTaskRequest();
                request.setStatus(TaskStatus.IN_PROGRESS);

                mockMvc.perform(put("/api/tasks/{id}", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
        }

        @Test
        @Order(11)
        @WithMockUser(username = "developer", roles = "DEVELOPER")
        void updateTask_ToDone_ShouldReturnUpdatedTask() throws Exception {
                UpdateTaskRequest request = new UpdateTaskRequest();
                request.setStatus(TaskStatus.DONE);

                mockMvc.perform(put("/api/tasks/{id}", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("DONE"));
        }

        @Test
        @Order(12)
        @WithMockUser(username = "developer", roles = "DEVELOPER")
        void updateTask_InvalidStatusTransition_ShouldReturnBadRequest() throws Exception {
                // 创建新任务用于测试
                CreateTaskRequest createRequest = new CreateTaskRequest();
                createRequest.setTitle("Status Test Task");
                createRequest.setDescription("For testing invalid status transition");
                createRequest.setPriority(TaskPriority.MEDIUM);
                createRequest.setBoardId(boardId);
                createRequest.setColumnId(columnId);

                MvcResult result = mockMvc.perform(post("/api/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createRequest)))
                                .andExpect(status().isOk())
                                .andReturn();

                Long newTaskId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

                // 尝试从TODO直接到DONE
                UpdateTaskRequest updateRequest = new UpdateTaskRequest();
                updateRequest.setStatus(TaskStatus.DONE);

                mockMvc.perform(put("/api/tasks/{id}", newTaskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("无效的状态变更"));
        }

        @Test
        @Order(13)
        @WithMockUser(username = "developer", roles = "DEVELOPER")
        void deleteTask_WithoutAdminRole_ShouldReturnForbidden() throws Exception {
                mockMvc.perform(delete("/api/tasks/{id}", taskId))
                                .andExpect(status().isForbidden());
        }

        @Test
        @Order(14)
        @WithMockUser(username = "manager", roles = "ADMIN")
        void deleteTask_WithAdminRole_ShouldReturnOk() throws Exception {
                mockMvc.perform(delete("/api/tasks/{id}", taskId))
                                .andExpect(status().isOk());
        }
}