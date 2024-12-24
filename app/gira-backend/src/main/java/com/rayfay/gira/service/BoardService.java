package com.rayfay.gira.service;

import com.rayfay.gira.dto.BoardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface BoardService {
    BoardDto createBoard(Long projectId, BoardDto boardDto);

    BoardDto getBoardById(Long id);

    Page<BoardDto> getBoardsByProject(Long projectId, Pageable pageable);

    Page<BoardDto> getActiveBoards(Long projectId, Pageable pageable);

    BoardDto updateBoard(Long id, BoardDto boardDto);

    void deleteBoard(Long id);

    BoardDto archiveBoard(Long id);

    BoardDto unarchiveBoard(Long id);

    boolean hasAccess(Long boardId);

    BoardDto updateBoardColumnsOrder(Long id, List<Long> columnIds);
}