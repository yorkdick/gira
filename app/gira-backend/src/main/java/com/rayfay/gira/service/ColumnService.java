package com.rayfay.gira.service;

import com.rayfay.gira.dto.ColumnDto;

import java.util.List;

public interface ColumnService {
    ColumnDto createColumn(Long boardId, ColumnDto columnDto);

    ColumnDto getColumnById(Long id);

    List<ColumnDto> getColumnsByBoard(Long boardId);

    List<ColumnDto> getColumnsByBoardOrderByPosition(Long boardId);

    ColumnDto updateColumn(Long id, ColumnDto columnDto);

    void deleteColumn(Long id);

    List<ColumnDto> updateColumnPositions(Long boardId, List<Long> columnIds);

    boolean hasAccess(Long columnId);

    ColumnDto updateTasksOrder(Long id, List<Long> taskIds);
}