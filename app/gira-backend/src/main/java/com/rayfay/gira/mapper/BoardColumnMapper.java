package com.rayfay.gira.mapper;

import com.rayfay.gira.dto.ColumnDto;
import com.rayfay.gira.entity.BoardColumn;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BoardColumnMapper extends BaseMapper<BoardColumn, ColumnDto> {
}