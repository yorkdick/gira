package com.rayfay.gira.controller;

import com.rayfay.gira.dto.AttachmentDto;
import com.rayfay.gira.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/attachments")
@RequiredArgsConstructor
@Tag(name = "Attachment", description = "Attachment management APIs")
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a new attachment")
    public ResponseEntity<AttachmentDto> uploadAttachment(
            @RequestParam("file") MultipartFile file,
            @RequestParam("taskId") Long taskId) throws IOException {
        return ResponseEntity.ok(attachmentService.uploadAttachment(taskId, file));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get attachment metadata by ID")
    public ResponseEntity<AttachmentDto> getAttachment(@PathVariable Long id) {
        return ResponseEntity.ok(attachmentService.getAttachmentById(id));
    }

    @GetMapping("/task/{taskId}")
    @Operation(summary = "Get attachments by task ID")
    public ResponseEntity<List<AttachmentDto>> getAttachmentsByTaskId(@PathVariable Long taskId) {
        return ResponseEntity.ok(attachmentService.getAttachmentsByTaskId(taskId));
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download an attachment")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long id) {
        AttachmentDto attachment = attachmentService.getAttachmentById(id);
        Resource resource = attachmentService.loadAttachmentAsResource(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an attachment")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long id) {
        attachmentService.deleteAttachment(id);
        return ResponseEntity.ok().build();
    }
}