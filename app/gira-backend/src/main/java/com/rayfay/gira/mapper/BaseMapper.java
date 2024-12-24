package com.rayfay.gira.mapper;

import org.mapstruct.MappingTarget;

import java.util.List;

public interface BaseMapper<E, D> {
    D toDto(E entity);

    E toEntity(D dto);

    List<D> toDto(List<E> entities);

    List<E> toEntity(List<D> dtos);

    void updateEntity(D dto, @MappingTarget E entity);
}