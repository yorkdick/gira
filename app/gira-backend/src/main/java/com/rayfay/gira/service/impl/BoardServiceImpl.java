package com.rayfay.gira.service.impl;

import com.rayfay.gira.dto.BoardDto;
import com.rayfay.gira.dto.ColumnDto;
import com.rayfay.gira.entity.Board;
import com.rayfay.gira.entity.BoardColumn;
import com.rayfay.gira.entity.Project;
import com.rayfay.gira.repository.BoardRepository;
import com.rayfay.gira.repository.ProjectRepository;
import com.rayfay.gira.service.BoardService;
import com.rayfay.gira.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;

    @Override
    @Transactional
    public BoardDto createBoard(Long projectId, BoardDto boardDto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        Board board = new Board();
        board.setName(boardDto.getName());
        board.setDescription(boardDto.getDescription());
        board.setProject(project);
        board.setColumns(new ArrayList<>());

        board = boardRepository.save(board);
        return mapToDto(board);
    }

    @Override
    public BoardDto getBoardById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));
        return mapToDto(board);
    }

    @Override
    public Page<BoardDto> getBoardsByProject(Long projectId, Pageable pageable) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        return boardRepository.findByProject(project, pageable).map(this::mapToDto);
    }

    @Override
    public Page<BoardDto> getActiveBoards(Long projectId, Pageable pageable) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        return boardRepository.findByProjectAndArchivedFalse(project, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional
    public BoardDto updateBoard(Long id, BoardDto boardDto) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));

        if (boardDto.getName() != null) {
            board.setName(boardDto.getName());
        }
        if (boardDto.getDescription() != null) {
            board.setDescription(boardDto.getDescription());
        }

        board = boardRepository.save(board);
        return mapToDto(board);
    }

    @Override
    @Transactional
    public void deleteBoard(Long id) {
        if (!boardRepository.existsById(id)) {
            throw new EntityNotFoundException("Board not found");
        }
        boardRepository.deleteById(id);
    }

    @Override
    @Transactional
    public BoardDto archiveBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));
        board.setArchived(true);
        board = boardRepository.save(board);
        return mapToDto(board);
    }

    @Override
    @Transactional
    public BoardDto unarchiveBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));
        board.setArchived(false);
        board = boardRepository.save(board);
        return mapToDto(board);
    }

    @Override
    public boolean hasAccess(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));
        return projectService.hasAccess(board.getProject().getId());
    }

    @Override
    @Transactional
    public BoardDto updateBoardColumnsOrder(Long id, List<Long> columnIds) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));

        List<BoardColumn> columns = board.getColumns();
        IntStream.range(0, columnIds.size()).forEach(i -> {
            Long columnId = columnIds.get(i);
            columns.stream()
                    .filter(col -> col.getId().equals(columnId))
                    .findFirst()
                    .ifPresent(col -> col.setPosition(i));
        });

        board = boardRepository.save(board);
        return mapToDto(board);
    }

    private BoardDto mapToDto(Board board) {
        BoardDto dto = new BoardDto();
        dto.setId(board.getId());
        dto.setName(board.getName());
        dto.setDescription(board.getDescription());
        dto.setProjectId(board.getProject().getId());
        dto.setColumns(board.getColumns().stream()
                .map(this::mapColumnToDto)
                .collect(Collectors.toList()));
        dto.setArchived(board.isArchived());
        dto.setCreatedAt(board.getCreatedAt());
        dto.setUpdatedAt(board.getUpdatedAt());
        dto.setCreatedBy(board.getCreatedBy());
        dto.setUpdatedBy(board.getUpdatedBy());
        return dto;
    }

    private ColumnDto mapColumnToDto(BoardColumn column) {
        ColumnDto dto = new ColumnDto();
        dto.setId(column.getId());
        dto.setName(column.getName());
        dto.setDescription(column.getDescription());
        dto.setBoardId(column.getBoard().getId());
        dto.setPosition(column.getPosition());
        dto.setCreatedAt(column.getCreatedAt());
        dto.setUpdatedAt(column.getUpdatedAt());
        dto.setCreatedBy(column.getCreatedBy());
        dto.setUpdatedBy(column.getUpdatedBy());
        return dto;
    }
}