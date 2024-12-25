package com.rayfay.gira.board;

import com.rayfay.gira.dto.BoardDto;
import com.rayfay.gira.dto.ColumnDto;
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

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BoardControllerTest {

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
    void testCreateBoard() throws Exception {
        // TC-004-1: 创建看板
        BoardDto request = new BoardDto();
        request.setName("开发看板");
        request.setDescription("描述");
        request.setProjectId(1L);
        request.setColumns(Arrays.asList(
                createColumn("待处理", 0),
                createColumn("进行中", 1),
                createColumn("已完成", 2)));

        mockMvc.perform(post("/api/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("开发看板"))
                .andExpect(jsonPath("$.columns.length()").value(3));
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testUpdateBoard() throws Exception {
        // TC-004-2: 更新看板
        BoardDto request = new BoardDto();
        request.setName("新名称");
        request.setDescription("新描述");

        mockMvc.perform(put("/api/boards/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("新名称"))
                .andExpect(jsonPath("$.description").value("新描述"));
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testMoveTask() throws Exception {
        // TC-004-3: 移动任务
        TaskDto request = new TaskDto();
        request.setId(1L);
        request.setColumnId(2L);
        request.setPosition(0);

        mockMvc.perform(put("/api/tasks/1/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.columnId").value(2));
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testCreateBoardWithoutColumns() throws Exception {
        // 额外测试：创建没有列的看板
        BoardDto request = new BoardDto();
        request.setName("空看板");
        request.setDescription("描述");
        request.setProjectId(1L);

        mockMvc.perform(post("/api/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("看板必须至少包含一列"));
    }

    @Test
    @WithMockUser(username = "unauthorized_user")
    void testUpdateBoardWithoutPermission() throws Exception {
        // 额外测试：无权限更新看板
        BoardDto request = new BoardDto();
        request.setName("新名称");
        request.setDescription("新描述");

        mockMvc.perform(put("/api/boards/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("无权限修改看板"));
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testDeleteBoard() throws Exception {
        // TC-004-4: 删除看板
        mockMvc.perform(delete("/api/boards/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testDeleteColumn() throws Exception {
        // TC-004-5: 删除看板列
        mockMvc.perform(delete("/api/boards/1/columns/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "unauthorized_user")
    void testDeleteBoardWithoutPermission() throws Exception {
        // TC-004-6: 无权限删除看板
        mockMvc.perform(delete("/api/boards/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("无权限删除看板"));
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testDeleteNonExistentBoard() throws Exception {
        // TC-004-7: 删除不存在的看板
        mockMvc.perform(delete("/api/boards/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("看板不存在"));
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testDeleteLastColumn() throws Exception {
        // TC-004-8: 删除最后一列
        mockMvc.perform(delete("/api/boards/1/columns/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("看板必须至少保留一列"));
    }

    private ColumnDto createColumn(String name, int position) {
        ColumnDto column = new ColumnDto();
        column.setName(name);
        column.setPosition(position);
        return column;
    }
}