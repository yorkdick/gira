package com.rayfay.gira.backlog;

import com.rayfay.gira.dto.BacklogItemDto;
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
public class BacklogControllerTest {

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
    void testCreateBacklogItem() throws Exception {
        // TC-006-1: 创建Backlog项
        BacklogItemDto request = new BacklogItemDto();
        request.setTitle("新功能开发");
        request.setDescription("实现新的功能模块");
        request.setPriority("HIGH");
        request.setType("FEATURE");
        request.setProjectId(1L);
        request.setStoryPoints(5);
        request.setDueDate(LocalDateTime.now().plusDays(14));

        mockMvc.perform(post("/api/backlog/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("新功能开发"))
                .andExpect(jsonPath("$.type").value("FEATURE"));
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testGetBacklogItems() throws Exception {
        // TC-006-2: 获取Backlog列表（分页）
        mockMvc.perform(get("/api/backlog/items")
                .param("projectId", "1")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "priority,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalPages").exists());
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testFilterBacklogItems() throws Exception {
        // TC-006-3: 过滤Backlog项
        mockMvc.perform(get("/api/backlog/items")
                .param("projectId", "1")
                .param("type", "FEATURE")
                .param("priority", "HIGH")
                .param("status", "TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].type").value("FEATURE"))
                .andExpect(jsonPath("$.content[*].priority").value("HIGH"));
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testUpdateBacklogItem() throws Exception {
        // TC-006-4: 更新Backlog项
        BacklogItemDto request = new BacklogItemDto();
        request.setTitle("更新的标题");
        request.setPriority("MEDIUM");
        request.setStoryPoints(8);

        mockMvc.perform(put("/api/backlog/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("更新的标题"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"));
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testAssignToSprint() throws Exception {
        // TC-006-5: 分配到Sprint
        mockMvc.perform(put("/api/backlog/items/1/sprint/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sprintId").value(2));
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testRemoveFromSprint() throws Exception {
        // TC-006-6: 从Sprint中移除
        mockMvc.perform(delete("/api/backlog/items/1/sprint")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sprintId").isEmpty());
    }

    @Test
    @WithMockUser(username = "unauthorized_user")
    void testUpdateBacklogItemWithoutPermission() throws Exception {
        // TC-006-7: 无权限更新
        BacklogItemDto request = new BacklogItemDto();
        request.setTitle("更新的标题");

        mockMvc.perform(put("/api/backlog/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("无权限修改Backlog项"));
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testDeleteBacklogItem() throws Exception {
        // TC-006-8: 删除Backlog项
        mockMvc.perform(delete("/api/backlog/items/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testBulkUpdatePriority() throws Exception {
        // TC-006-9: 批量更新优先级
        Long[] itemIds = { 1L, 2L, 3L };
        mockMvc.perform(put("/api/backlog/items/bulk/priority/HIGH")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemIds)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testSearchBacklogItems() throws Exception {
        // TC-006-10: 搜索Backlog项
        mockMvc.perform(get("/api/backlog/items/search")
                .param("projectId", "1")
                .param("keyword", "功能")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}