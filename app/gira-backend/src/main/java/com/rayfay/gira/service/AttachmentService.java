package com.rayfay.gira.service;

import com.rayfay.gira.dto.AttachmentDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.util.List;

public interface AttachmentService {
    AttachmentDto uploadAttachment(Long taskId, MultipartFile file) throws IOException;

    AttachmentDto getAttachmentById(Long id);

    List<AttachmentDto> getAttachmentsByTaskId(Long taskId);

    Resource loadAttachmentAsResource(Long id);

    void deleteAttachment(Long id);
}