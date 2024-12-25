package com.rayfay.gira.project;

import com.rayfay.gira.dto.TeamDto;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerTest {

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
    void testCreateProject() throws Exception {
        // TC-003-1: 创建项目
        TeamDto request = new TeamDto();
        request.setName("新项目");
        request.setDescription("新项目描述");

        mockMvc.perform(post("/api/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("新项目"))
                .andExpect(jsonPath("$.description").value("新项目描述"));
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testCreateProjectWithDuplicateName() throws Exception {
        // TC-003-2: 项目名称重复
        TeamDto request = new TeamDto();
        request.setName("已存在的项目");
        request.setDescription("描述");

        mockMvc.perform(post("/api/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("项目名称已存在"));
    }

    @Test
    @WithMockUser(username = "unauthorized_user")
    void testCreateProjectWithoutPermission() throws Exception {
        // TC-003-3: 无权限创建
        TeamDto request = new TeamDto();
        request.setName("新项目");
        request.setDescription("描述");

        mockMvc.perform(post("/api/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("无权限创建项目"));
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testCreateProjectWithEmptyName() throws Exception {
        // 额外测试：项目名称为空
        TeamDto request = new TeamDto();
        request.setName("");
        request.setDescription("描述");

        mockMvc.perform(post("/api/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("项目名称不能为空"));
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testDeleteProject() throws Exception {
        // TC-003-4: 删除项目
        mockMvc.perform(delete("/api/teams/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "unauthorized_user")
    void testDeleteProjectWithoutPermission() throws Exception {
        // TC-003-5: 无权限删除项目
        mockMvc.perform(delete("/api/teams/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("无权限删除项目"));
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testDeleteNonExistentProject() throws Exception {
        // TC-003-6: 删除不存在的项目
        mockMvc.perform(delete("/api/teams/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("项目不存在"));
    }
}