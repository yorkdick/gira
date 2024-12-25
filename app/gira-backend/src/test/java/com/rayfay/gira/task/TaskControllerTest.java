package com.rayfay.gira.task;

import com.rayfay.gira.dto.TaskDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testCreateTask() throws Exception {
        // TC-005-1: 创建任务
        TaskDto request = new TaskDto();
        request.setTitle("测试任务");
        request.setDescription("任务描述");
        request.setType("STORY");
        request.setPriority("HIGH");
        request.setDueDate(LocalDateTime.now().plusDays(7));
        request.setColumnId(1L);
        request.setAssigneeId(1L);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("测试任务"))
                .andExpect(jsonPath("$.type").value("STORY"));
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testUpdateTaskStatus() throws Exception {
        // TC-005-2: 更新任务状态
        TaskDto request = new TaskDto();
        request.setStatus("IN_PROGRESS");

        mockMvc.perform(put("/api/tasks/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testAssignTask() throws Exception {
        // TC-005-3: 分配任务
        mockMvc.perform(put("/api/tasks/1/assign/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assigneeId").value(2));
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testCreateTaskWithInvalidDueDate() throws Exception {
        // 额外测试：截止日期在过去
        TaskDto request = new TaskDto();
        request.setTitle("测试任务");
        request.setDescription("任务描述");
        request.setType("STORY");
        request.setPriority("HIGH");
        request.setDueDate(LocalDateTime.now().minusDays(1));
        request.setColumnId(1L);
        request.setAssigneeId(1L);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("截止日期不能在过去"));
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testUpdateTaskStatusWithInvalidTransition() throws Exception {
        // 额外测试：无效的状态转换
        TaskDto request = new TaskDto();
        request.setStatus("DONE");

        mockMvc.perform(put("/api/tasks/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("无效的状态转换"));
    }

    @Test
    @WithMockUser(username = "unauthorized_user")
    void testAssignTaskWithoutPermission() throws Exception {
        // 额外测试：无权限分配任务
        mockMvc.perform(put("/api/tasks/1/assign/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("无权限分配任务"));
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testDeleteTask() throws Exception {
        // TC-005-4: 删除任务
        mockMvc.perform(delete("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "unauthorized_user")
    void testDeleteTaskWithoutPermission() throws Exception {
        // TC-005-5: 无权限删除任务
        mockMvc.perform(delete("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("无权限删除任务"));
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testDeleteNonExistentTask() throws Exception {
        // TC-005-6: 删除不存在的任务
        mockMvc.perform(delete("/api/tasks/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("任务不存在"));
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testDeleteTaskWithComments() throws Exception {
        // TC-005-7: 删除有评论的任务
        mockMvc.perform(delete("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testDeleteTaskWithAttachments() throws Exception {
        // TC-005-8: 删除有附件的任务
        mockMvc.perform(delete("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}