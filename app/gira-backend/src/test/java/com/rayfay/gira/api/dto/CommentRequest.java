package com.rayfay.gira.api.dto;

import lombok.Data;

@Data
public class CommentRequest {
    private String content;
    private Long taskId;
    private Long authorId;
    private Long parentId;
    private String type;
    private String status;
}