package com.rayfay.gira.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rayfay.gira.dto.request.CreateBoardRequest;
import com.rayfay.gira.dto.request.UpdateBoardRequest;
import com.rayfay.gira.entity.Board;
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

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = { "/sql/board/init_board_test.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = { "/sql/board/cleanup_board_test.sql" }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static Long boardId;

    @Test
    @Order(1)
    @WithMockUser(username = "manager", roles = { "ADMIN", "MANAGER" })
    void createBoard_WithValidRequest_ShouldReturnCreatedBoard() throws Exception {
        // Arrange
        CreateBoardRequest request = new CreateBoardRequest();
        request.setName("New Test Board");
        request.setDescription("New Test Description");

        // 创建看板列
        CreateBoardRequest.BoardColumnRequest todoColumn = new CreateBoardRequest.BoardColumnRequest();
        todoColumn.setName("待办");
        todoColumn.setOrderIndex(0);

        CreateBoardRequest.BoardColumnRequest inProgressColumn = new CreateBoardRequest.BoardColumnRequest();
        inProgressColumn.setName("进行中");
        inProgressColumn.setOrderIndex(1);

        CreateBoardRequest.BoardColumnRequest doneColumn = new CreateBoardRequest.BoardColumnRequest();
        doneColumn.setName("已完成");
        doneColumn.setOrderIndex(2);

        request.setColumns(Arrays.asList(todoColumn, inProgressColumn, doneColumn));

        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Test Board"))
                .andExpect(jsonPath("$.description").value("New Test Description"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.columns").isArray())
                .andExpect(jsonPath("$.columns.length()").value(3))
                .andExpect(jsonPath("$.columns[0].name").value("待办"))
                .andExpect(jsonPath("$.columns[1].name").value("进行中"))
                .andExpect(jsonPath("$.columns[2].name").value("已完成"))
                .andReturn();

        // 获取创建的看板ID，供其他测试使用
        String response = result.getResponse().getContentAsString();
        Board createdBoard = objectMapper.readValue(response, Board.class);
        boardId = createdBoard.getId();
    }

    @Test
    @Order(2)
    @WithMockUser(username = "developer", roles = "DEVELOPER")
    void getBoard_WithValidId_ShouldReturnBoard() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/boards/{id}", boardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(boardId))
                .andExpect(jsonPath("$.name").value("New Test Board"))
                .andExpect(jsonPath("$.description").value("New Test Description"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.columns").isArray())
                .andExpect(jsonPath("$.columns.length()").value(3));
    }

    @Test
    @Order(3)
    @WithMockUser(username = "developer", roles = "DEVELOPER")
    void getBoard_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/boards/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("看板不存在"));
    }

    @Test
    @Order(4)
    @WithMockUser(username = "developer", roles = "DEVELOPER")
    void getAllBoards_ShouldReturnPageOfBoards() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/boards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").exists())
                .andExpect(jsonPath("$.content[0].description").exists());
    }

    @Test
    @Order(5)
    @WithMockUser(username = "manager", roles = { "ADMIN", "MANAGER" })
    void updateBoard_WithValidRequest_ShouldReturnUpdatedBoard() throws Exception {
        // Arrange
        UpdateBoardRequest request = new UpdateBoardRequest();
        request.setName("Updated Board");
        request.setDescription("Updated Description");

        // Act & Assert
        mockMvc.perform(put("/api/boards/{id}", boardId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Board"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    @Order(6)
    @WithMockUser(username = "developer", roles = "DEVELOPER")
    void updateBoard_WithoutAdminRole_ShouldReturnForbidden() throws Exception {
        // Arrange
        UpdateBoardRequest request = new UpdateBoardRequest();
        request.setName("Updated Board");
        request.setDescription("Updated Description");

        // Act & Assert
        mockMvc.perform(put("/api/boards/{id}", boardId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(7)
    @WithMockUser(username = "manager", roles = { "ADMIN", "MANAGER" })
    void archiveBoard_WithValidId_ShouldReturnOk() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/boards/{id}/archive", boardId))
                .andExpect(status().isOk());
    }

    @Test
    @Order(8)
    @WithMockUser(username = "developer", roles = "DEVELOPER")
    void archiveBoard_WithoutAdminRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(put("/api/boards/{id}/archive", boardId))
                .andExpect(status().isForbidden());
    }
}