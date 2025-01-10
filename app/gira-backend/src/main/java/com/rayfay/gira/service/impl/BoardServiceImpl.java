package com.rayfay.gira.service.impl;

import com.rayfay.gira.dto.request.CreateBoardRequest;
import com.rayfay.gira.dto.request.UpdateBoardColumnsRequest;
import com.rayfay.gira.dto.request.UpdateBoardRequest;
import com.rayfay.gira.dto.response.BoardResponse;
import com.rayfay.gira.dto.response.TaskResponse;
import com.rayfay.gira.entity.Board;
import com.rayfay.gira.entity.BoardColumn;
import com.rayfay.gira.entity.BoardStatus;
import com.rayfay.gira.entity.User;
import com.rayfay.gira.exception.ResourceNotFoundException;
import com.rayfay.gira.mapper.BoardMapper;
import com.rayfay.gira.mapper.TaskMapper;
import com.rayfay.gira.repository.BoardColumnRepository;
import com.rayfay.gira.repository.BoardRepository;
import com.rayfay.gira.repository.TaskRepository;
import com.rayfay.gira.repository.UserRepository;
import com.rayfay.gira.service.interfaces.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final BoardColumnRepository boardColumnRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final BoardMapper boardMapper;
    private final TaskMapper taskMapper;

    @Override
    @Transactional
    public BoardResponse createBoard(CreateBoardRequest request) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        final Board board = Board.builder()
                .name(request.getName())
                .description(request.getDescription())
                .status(BoardStatus.ACTIVE)
                .createdBy(currentUser)
                .build();

        final Board savedBoard = boardRepository.save(board);

        List<BoardColumn> columns = request.getColumns().stream()
                .map(column -> BoardColumn.builder()
                        .board(savedBoard)
                        .name(column.getName())
                        .orderIndex(column.getOrderIndex())
                        .wipLimit(column.getWipLimit())
                        .build())
                .collect(Collectors.toList());

        boardColumnRepository.saveAll(columns);
        savedBoard.setColumns(columns);

        return boardMapper.toResponse(savedBoard, taskRepository);
    }

    @Override
    @Transactional
    public BoardResponse updateBoardColumns(Long id, UpdateBoardColumnsRequest request) {
        Board board = getBoardOrThrow(id);

        // 获取现有列的映射
        Map<Long, BoardColumn> existingColumns = board.getColumns().stream()
                .collect(Collectors.toMap(BoardColumn::getId, column -> column));

        List<BoardColumn> updatedColumns = new ArrayList<>();
        for (UpdateBoardColumnsRequest.BoardColumnRequest columnRequest : request.getColumns()) {
            BoardColumn column;
            if (columnRequest.getId() != null) {
                column = existingColumns.get(columnRequest.getId());
                if (column == null) {
                    throw new ResourceNotFoundException("看板列不存在");
                }
                column.setName(columnRequest.getName());
                column.setOrderIndex(columnRequest.getOrderIndex());
                column.setWipLimit(columnRequest.getWipLimit());
            } else {
                column = BoardColumn.builder()
                        .board(board)
                        .name(columnRequest.getName())
                        .orderIndex(columnRequest.getOrderIndex())
                        .wipLimit(columnRequest.getWipLimit())
                        .build();
            }
            updatedColumns.add(column);
        }

        // 删除不再使用的列
        List<Long> updatedColumnIds = request.getColumns().stream()
                .map(UpdateBoardColumnsRequest.BoardColumnRequest::getId)
                .filter(columnId -> columnId != null)
                .collect(Collectors.toList());

        board.getColumns().stream()
                .filter(column -> !updatedColumnIds.contains(column.getId()))
                .forEach(column -> {
                    if (!taskRepository.findByColumnId(column.getId()).isEmpty()) {
                        throw new IllegalStateException("无法删除包含任务的列");
                    }
                });

        board.setColumns(boardColumnRepository.saveAll(updatedColumns));
        return boardMapper.toResponse(board, taskRepository);
    }

    @Override
    public BoardResponse getBoardById(Long id) {
        return boardMapper.toResponse(getBoardOrThrow(id), taskRepository);
    }

    @Override
    public Page<BoardResponse> getAllBoards(BoardStatus status, Pageable pageable) {
        return boardRepository.findByStatus(status, pageable)
                .map(board -> boardMapper.toResponse(board, taskRepository));
    }

    @Override
    @Transactional
    public void archiveBoard(Long id) {
        Board board = getBoardOrThrow(id);
        board.setStatus(BoardStatus.ARCHIVED);
        boardRepository.save(board);
    }

    @Override
    public List<TaskResponse> getBoardTasks(Long id, Long columnId) {
        Board board = getBoardOrThrow(id);
        if (columnId != null) {
            BoardColumn column = board.getColumns().stream()
                    .filter(c -> c.getId().equals(columnId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("看板列不存在"));
            return taskRepository.findByColumnId(column.getId())
                    .stream()
                    .map(taskMapper::toResponse)
                    .collect(Collectors.toList());
        }
        return board.getColumns().stream()
                .flatMap(column -> taskRepository.findByColumnId(column.getId()).stream())
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BoardResponse updateBoard(Long id, UpdateBoardRequest request) {
        Board board = getBoardOrThrow(id);

        board.setName(request.getName());
        board.setDescription(request.getDescription());

        return boardMapper.toResponse(boardRepository.save(board), taskRepository);
    }

    private Board getBoardOrThrow(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("看板不存在"));
    }
}