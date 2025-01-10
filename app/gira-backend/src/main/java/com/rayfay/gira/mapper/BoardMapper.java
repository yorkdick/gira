package com.rayfay.gira.mapper;

import com.rayfay.gira.dto.response.BoardResponse;
import com.rayfay.gira.entity.Board;
import com.rayfay.gira.entity.BoardColumn;
import com.rayfay.gira.repository.TaskRepository;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = { UserMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BoardMapper {

    @Mapping(target = "columns", source = "columns", qualifiedByName = "toBoardColumnResponses")
    BoardResponse toResponse(Board board, @Context TaskRepository taskRepository);

    @Named("toBoardColumnResponse")
    @Mapping(target = "taskCount", expression = "java(taskRepository.countByColumnId(column.getId()))")
    BoardResponse.BoardColumnResponse toBoardColumnResponse(BoardColumn column, @Context TaskRepository taskRepository);

    @Named("toBoardColumnResponses")
    default List<BoardResponse.BoardColumnResponse> toBoardColumnResponses(List<BoardColumn> columns,
            @Context TaskRepository taskRepository) {
        if (columns == null) {
            return null;
        }
        return columns.stream()
                .map(column -> toBoardColumnResponse(column, taskRepository))
                .collect(Collectors.toList());
    }
}