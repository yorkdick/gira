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
        request.setDescription("项目开发任务看板");
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
    void testGetBoardDetails() throws Exception {
        // TC-004-2: 获取看板详情
        mockMvc.perform(get("/api/boards/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.columns").isArray());
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testUpdateBoard() throws Exception {
        // TC-004-3: 更新看板
        BoardDto request = new BoardDto();
        request.setName("新看板名称");
        request.setDescription("更新后的描述");

        mockMvc.perform(put("/api/boards/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("新看板名称"))
                .andExpect(jsonPath("$.description").value("更新后的描述"));
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testAddColumn() throws Exception {
        // TC-004-4: 添加看板列
        ColumnDto request = createColumn("测试列", 3);

        mockMvc.perform(post("/api/boards/1/columns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("测试列"))
                .andExpect(jsonPath("$.position").value(3));
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testUpdateColumnOrder() throws Exception {
        // TC-004-5: 更新列顺序
        Long[] columnIds = { 2L, 1L, 3L };

        mockMvc.perform(put("/api/boards/1/columns/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(columnIds)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testMoveTask() throws Exception {
        // TC-004-6: 移动任务
        TaskDto request = new TaskDto();
        request.setId(1L);
        request.setColumnId(2L);
        request.setPosition(0);

        mockMvc.perform(put("/api/boards/tasks/1/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.columnId").value(2))
                .andExpect(jsonPath("$.position").value(0));
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testDeleteColumn() throws Exception {
        // TC-004-7: 删除看板列
        mockMvc.perform(delete("/api/boards/1/columns/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testArchiveBoard() throws Exception {
        // TC-004-8: 归档看板
        mockMvc.perform(put("/api/boards/1/archive")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.archived").value(true));
    }

    @Test
    @WithMockUser(username = "unauthorized_user")
    void testUpdateBoardWithoutPermission() throws Exception {
        // TC-004-9: 无权限更新看板
        BoardDto request = new BoardDto();
        request.setName("新名称");

        mockMvc.perform(put("/api/boards/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("无权限修改看板"));
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testDeleteBoard() throws Exception {
        // TC-004-10: 删除看板
        mockMvc.perform(delete("/api/boards/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testCreateBoardWithInvalidData() throws Exception {
        // TC-004-11: 创建看板 - 无效数据
        BoardDto request = new BoardDto();
        request.setName(""); // 空名称

        mockMvc.perform(post("/api/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("看板名称不能为空"));
    }

    @Test
    @WithMockUser(username = "user1", roles = { "USER" })
    void testDeleteLastColumn() throws Exception {
        // TC-004-12: 删除最后一列
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