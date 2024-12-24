package com.rayfay.gira.mapper;

import com.rayfay.gira.dto.CommentDto;
import com.rayfay.gira.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper extends BaseMapper<Comment, CommentDto> {
}