package com.rayfay.gira.service.impl;

import com.rayfay.gira.dto.AttachmentDto;
import com.rayfay.gira.dto.UserDto;
import com.rayfay.gira.entity.Attachment;
import com.rayfay.gira.entity.Task;
import com.rayfay.gira.entity.User;
import com.rayfay.gira.repository.AttachmentRepository;
import com.rayfay.gira.repository.TaskRepository;
import com.rayfay.gira.repository.UserRepository;
import com.rayfay.gira.service.AttachmentService;
import com.rayfay.gira.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Value("${app.upload.location}")
    private String uploadLocation;

    @Override
    @Transactional
    public AttachmentDto uploadAttachment(Long taskId, MultipartFile file) throws IOException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        UserDto currentUser = userService.getCurrentUser();
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;
        Path uploadPath = Paths.get(uploadLocation);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Attachment attachment = new Attachment();
        attachment.setFilename(filename);
        attachment.setContentType(file.getContentType());
        attachment.setSize(file.getSize());
        attachment.setPath(uniqueFilename);
        attachment.setTask(task);
        attachment.setUser(user);

        attachment = attachmentRepository.save(attachment);
        return mapToDto(attachment);
    }

    @Override
    public AttachmentDto getAttachmentById(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attachment not found"));
        return mapToDto(attachment);
    }

    @Override
    public List<AttachmentDto> getAttachmentsByTaskId(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        return attachmentRepository.findByTask(task).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteAttachment(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attachment not found"));

        UserDto currentUser = userService.getCurrentUser();
        if (!attachment.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You can only delete your own attachments");
        }

        try {
            Path filePath = Paths.get(uploadLocation).resolve(attachment.getPath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error deleting file: " + e.getMessage());
        }
        attachmentRepository.delete(attachment);
    }

    @Override
    public Resource loadAttachmentAsResource(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attachment not found"));

        try {
            Path filePath = Paths.get(uploadLocation).resolve(attachment.getPath());
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    private AttachmentDto mapToDto(Attachment attachment) {
        AttachmentDto dto = new AttachmentDto();
        dto.setId(attachment.getId());
        dto.setFilename(attachment.getFilename());
        dto.setContentType(attachment.getContentType());
        dto.setSize(attachment.getSize());
        dto.setPath(attachment.getPath());
        dto.setTaskId(attachment.getTask().getId());
        dto.setUser(mapUserToDto(attachment.getUser()));
        dto.setCreatedAt(attachment.getCreatedAt());
        dto.setUpdatedAt(attachment.getUpdatedAt());
        dto.setCreatedBy(attachment.getCreatedBy());
        dto.setUpdatedBy(attachment.getUpdatedBy());
        return dto;
    }

    private UserDto mapUserToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setAvatar(user.getAvatar());
        dto.setRoles(user.getRoles());
        dto.setEnabled(user.isEnabled());
        return dto;
    }
}