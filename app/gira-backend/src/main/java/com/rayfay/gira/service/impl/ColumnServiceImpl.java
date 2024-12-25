package com.rayfay.gira.service.impl;

import com.rayfay.gira.dto.ColumnDto;
import com.rayfay.gira.dto.TaskDto;
import com.rayfay.gira.entity.Board;
import com.rayfay.gira.entity.BoardColumn;
import com.rayfay.gira.entity.Task;
import com.rayfay.gira.repository.BoardRepository;
import com.rayfay.gira.repository.BoardColumnRepository;
import com.rayfay.gira.repository.TaskRepository;
import com.rayfay.gira.service.ColumnService;
import com.rayfay.gira.service.BoardService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ColumnServiceImpl implements ColumnService {

    private final BoardColumnRepository boardColumnRepository;
    private final BoardRepository boardRepository;
    private final TaskRepository taskRepository;
    private final BoardService boardService;

    @Override
    @Transactional
    public ColumnDto createColumn(Long boardId, ColumnDto columnDto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));

        BoardColumn column = new BoardColumn();
        column.setName(columnDto.getName());
        column.setDescription(columnDto.getDescription());
        column.setBoard(board);
        column.setPosition(boardColumnRepository.countByBoard(board));
        column.setTasks(new ArrayList<>());

        column = boardColumnRepository.save(column);
        return mapToDto(column);
    }

    @Override
    public ColumnDto getColumnById(Long id) {
        BoardColumn column = boardColumnRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Column not found"));
        return mapToDto(column);
    }

    @Override
    public List<ColumnDto> getColumnsByBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));
        return boardColumnRepository.findByBoard(board).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ColumnDto> getColumnsByBoardOrderByPosition(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));
        return boardColumnRepository.findByBoardOrderByPosition(board).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ColumnDto updateColumn(Long id, ColumnDto columnDto) {
        BoardColumn column = boardColumnRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Column not found"));

        if (columnDto.getName() != null) {
            column.setName(columnDto.getName());
        }
        if (columnDto.getDescription() != null) {
            column.setDescription(columnDto.getDescription());
        }

        column = boardColumnRepository.save(column);
        return mapToDto(column);
    }

    @Override
    @Transactional
    public void deleteColumn(Long id) {
        BoardColumn column = boardColumnRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Column not found"));

        // Update positions of remaining columns
        List<BoardColumn> columnsToUpdate = boardColumnRepository.findByBoardOrderByPosition(column.getBoard()).stream()
                .filter(c -> c.getPosition() > column.getPosition())
                .collect(Collectors.toList());

        columnsToUpdate.forEach(c -> c.setPosition(c.getPosition() - 1));
        boardColumnRepository.saveAll(columnsToUpdate);
        boardColumnRepository.delete(column);
    }

    @Override
    @Transactional
    public List<ColumnDto> updateColumnPositions(Long boardId, List<Long> columnIds) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));

        Map<Long, BoardColumn> columnMap = boardColumnRepository.findByBoard(board).stream()
                .collect(Collectors.toMap(BoardColumn::getId, column -> column));

        List<BoardColumn> updatedColumns = IntStream.range(0, columnIds.size())
                .mapToObj(index -> {
                    Long columnId = columnIds.get(index);
                    BoardColumn column = columnMap.get(columnId);
                    if (column == null) {
                        throw new EntityNotFoundException("Column not found with id: " + columnId);
                    }
                    column.setPosition(index);
                    return column;
                })
                .collect(Collectors.toList());

        boardColumnRepository.saveAll(updatedColumns);
        return updatedColumns.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ColumnDto updateTasksOrder(Long id, List<Long> taskIds) {
        BoardColumn column = boardColumnRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Column not found"));

        List<Task> tasks = taskRepository.findAllById(taskIds);
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            task.setColumn(column);
            task.setPosition(i);
            taskRepository.save(task);
        }

        return mapToDto(column);
    }

    @Override
    public boolean hasAccess(Long columnId) {
        BoardColumn column = boardColumnRepository.findById(columnId)
                .orElseThrow(() -> new EntityNotFoundException("Column not found"));
        return boardService.hasAccess(column.getBoard().getId());
    }

    private ColumnDto mapToDto(BoardColumn column) {
        ColumnDto dto = new ColumnDto();
        dto.setId(column.getId());
        dto.setName(column.getName());
        dto.setDescription(column.getDescription());
        dto.setBoardId(column.getBoard().getId());
        dto.setPosition(column.getPosition());
        dto.setTasks(column.getTasks().stream()
                .map(this::mapTaskToDto)
                .collect(Collectors.toList()));
        dto.setCreatedAt(column.getCreatedAt());
        dto.setUpdatedAt(column.getUpdatedAt());
        dto.setCreatedBy(column.getCreatedBy());
        dto.setUpdatedBy(column.getUpdatedBy());
        return dto;
    }

    private TaskDto mapTaskToDto(com.rayfay.gira.entity.Task task) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setColumnId(task.getColumn().getId());
        dto.setPosition(task.getPosition());
        dto.setPriority(task.getPriority().name());
        dto.setStatus(task.getStatus().name());
        dto.setType(task.getType().name());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setCreatedBy(task.getCreatedBy());
        dto.setUpdatedBy(task.getUpdatedBy());
        return dto;
    }
}