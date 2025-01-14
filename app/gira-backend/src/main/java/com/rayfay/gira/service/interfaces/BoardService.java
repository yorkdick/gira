package com.rayfay.gira.service.interfaces;

import com.rayfay.gira.dto.request.UpdateBoardRequest;
import com.rayfay.gira.dto.response.BoardResponse;
import com.rayfay.gira.entity.BoardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardService {
    BoardResponse updateBoard(Long id, UpdateBoardRequest request);

    BoardResponse getBoardById(Long id);

    BoardResponse getActiveBoard();

    Page<BoardResponse> getAllBoards(BoardStatus status, Pageable pageable);
}