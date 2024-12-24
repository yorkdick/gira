package com.rayfay.gira.mapper;

import com.rayfay.gira.dto.LabelDto;
import com.rayfay.gira.entity.Label;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LabelMapper extends BaseMapper<Label, LabelDto> {
}