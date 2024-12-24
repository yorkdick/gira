package com.rayfay.gira.mapper;

import com.rayfay.gira.dto.BoardDto;
import com.rayfay.gira.entity.Board;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BoardMapper extends BaseMapper<Board, BoardDto> {
}