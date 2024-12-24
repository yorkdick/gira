package com.rayfay.gira.service;

import com.rayfay.gira.dto.CommentDto;
import java.util.List;

public interface CommentService {
    CommentDto createComment(CommentDto commentDto);

    CommentDto getCommentById(Long id);

    List<CommentDto> getCommentsByTaskId(Long taskId);

    CommentDto updateComment(Long id, CommentDto commentDto);

    void deleteComment(Long id);
}