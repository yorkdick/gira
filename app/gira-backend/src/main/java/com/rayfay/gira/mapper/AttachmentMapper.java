package com.rayfay.gira.mapper;

import com.rayfay.gira.dto.AttachmentDto;
import com.rayfay.gira.entity.Attachment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AttachmentMapper extends BaseMapper<Attachment, AttachmentDto> {
}