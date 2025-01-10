package com.rayfay.gira.dto.response;

import com.rayfay.gira.entity.BoardStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BoardResponse {
    private Long id;
    private String name;
    private String description;
    private BoardStatus status;
    private List<BoardColumnResponse> columns;
    private UserResponse createdBy;
    private LocalDateTime createdAt;

    @Data
    public static class BoardColumnResponse {
        private Long id;
        private String name;
        private Integer orderIndex;
        private Integer wipLimit;
        private Integer taskCount;
    }
}