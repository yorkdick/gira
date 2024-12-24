package com.rayfay.gira.repository;

import com.rayfay.gira.entity.Board;
import com.rayfay.gira.entity.BoardColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardColumnRepository extends JpaRepository<BoardColumn, Long> {
    List<BoardColumn> findByBoard(Board board);

    List<BoardColumn> findByBoardOrderByPosition(Board board);

    int countByBoard(Board board);
}