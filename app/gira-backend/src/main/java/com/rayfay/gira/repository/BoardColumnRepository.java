package com.rayfay.gira.repository;

import com.rayfay.gira.entity.BoardColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardColumnRepository extends JpaRepository<BoardColumn, Long> {
    List<BoardColumn> findByBoardId(Long boardId);

    Optional<BoardColumn> findByBoardIdAndName(Long boardId, String name);
}