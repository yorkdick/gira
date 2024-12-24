package com.rayfay.gira.service.impl;

import com.rayfay.gira.dto.CommentDto;
import com.rayfay.gira.entity.Comment;
import com.rayfay.gira.entity.Task;
import com.rayfay.gira.entity.User;
import com.rayfay.gira.repository.CommentRepository;
import com.rayfay.gira.repository.TaskRepository;
import com.rayfay.gira.repository.UserRepository;
import com.rayfay.gira.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto) {
        Task task = taskRepository.findById(commentDto.getTaskId())
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        User user = userRepository.findById(commentDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setTask(task);
        comment.setAuthor(user);

        comment = commentRepository.save(comment);
        return mapToDto(comment);
    }

    @Override
    public CommentDto getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        return mapToDto(comment);
    }

    @Override
    public List<CommentDto> getCommentsByTaskId(Long taskId) {
        return commentRepository.findByTaskId(taskId).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long id, CommentDto commentDto) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        if (commentDto.getContent() != null) {
            comment.setContent(commentDto.getContent());
        }

        comment = commentRepository.save(comment);
        return mapToDto(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new EntityNotFoundException("Comment not found");
        }
        commentRepository.deleteById(id);
    }

    private CommentDto mapToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setTaskId(comment.getTask().getId());
        dto.setUserId(comment.getAuthor().getId());
        return dto;
    }
}