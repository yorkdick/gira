package com.rayfay.gira.service.interfaces;

import com.rayfay.gira.dto.request.CreateBoardRequest;
import com.rayfay.gira.dto.request.UpdateBoardColumnsRequest;
import com.rayfay.gira.dto.request.UpdateBoardRequest;
import com.rayfay.gira.dto.response.BoardResponse;
import com.rayfay.gira.dto.response.TaskResponse;
import com.rayfay.gira.entity.BoardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardService {
    BoardResponse createBoard(CreateBoardRequest request);

    BoardResponse updateBoard(Long id, UpdateBoardRequest request);

    BoardResponse updateBoardColumns(Long id, UpdateBoardColumnsRequest request);

    BoardResponse getBoardById(Long id);

    Page<BoardResponse> getAllBoards(BoardStatus status, Pageable pageable);

    void archiveBoard(Long id);

    List<TaskResponse> getBoardTasks(Long id, Long columnId);
}