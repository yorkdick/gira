import React, { useEffect, useMemo } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import DragDropProvider from '@/components/DragDropProvider';
import { RootState, AppDispatch } from '@/store';
import { fetchTasks } from '@/store/slices/taskSlice';
import { Task } from '@/types/task';
import BoardFilter, { BoardFilterValues } from './BoardFilter';
import BoardColumn from './BoardColumn';
import styles from './style.module.less';

const Board: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const board = useSelector((state: RootState) => state.board.currentBoard);
  const tasks = useSelector((state: RootState) => state.task.entities.byId);
  const users = useSelector((state: RootState) => state.user.entities.allIds.map(
    id => state.user.entities.byId[id]
  ));

  useEffect(() => {
    if (board) {
      dispatch(fetchTasks({ boardId: board.id }));
    }
  }, [board, dispatch]);

  const handleSearch = (keyword: string) => {
    if (board) {
      dispatch(fetchTasks({ boardId: board.id, keyword }));
    }
  };

  const handleFilter = (filters: BoardFilterValues) => {
    if (board) {
      dispatch(fetchTasks({ boardId: board.id, ...filters }));
    }
  };

  const tasksByColumn = useMemo(() => {
    if (!board) return new Map<number, Task[]>();

    const taskMap = new Map<number, Task[]>();
    board.columns.forEach((column) => {
      const columnTasks = Object.values(tasks)
        .filter((task) => task.columnId === column.id)
        .sort((a, b) => a.order - b.order);
      taskMap.set(column.id, columnTasks);
    });
    return taskMap;
  }, [board, tasks]);

  if (!board) return null;

  return (
    <div className={styles.board}>
      <BoardFilter
        users={users}
        onSearch={handleSearch}
        onFilter={handleFilter}
      />
      <DragDropProvider>
        <div className={styles.columns}>
          {board.columns.map((column) => (
            <BoardColumn
              key={column.id}
              column={column}
              tasks={tasksByColumn.get(column.id) || []}
            />
          ))}
        </div>
      </DragDropProvider>
    </div>
  );
};

export default Board; 