import { useCallback } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useDrag, useDrop, DragSourceMonitor, DropTargetMonitor } from 'react-dnd';
import { message } from 'antd';
import { updateTaskOrder } from '@/store/slices/boardSlice';
import { RootState, AppDispatch } from '@/store';
import { Task } from '@/types/task';
import { BoardColumn } from '@/types/board';

interface DragItem {
  type: string;
  taskId: number;
  columnId: number;
  index: number;
}

export const ItemTypes = {
  TASK: 'task',
};

export const useDragTask = (task: Task, index: number) => {
  const [{ isDragging }, drag] = useDrag({
    type: ItemTypes.TASK,
    item: {
      type: ItemTypes.TASK,
      taskId: task.id,
      columnId: task.columnId,
      index,
    },
    collect: (monitor: DragSourceMonitor) => ({
      isDragging: monitor.isDragging(),
    }),
  });

  return { drag, isDragging };
};

export const useDropTask = (columnId: number, index: number) => {
  const dispatch = useDispatch<AppDispatch>();
  const board = useSelector((state: RootState) => state.board.currentBoard);

  const handleDrop = useCallback(
    async (item: DragItem) => {
      if (!board) return;

      const column = board.columns.find((col: BoardColumn) => col.id === columnId);
      if (!column) return;

      // 检查WIP限制
      const tasksInColumn = board.tasks.filter((task: Task) => task.columnId === columnId);
      if (column.settings?.wipLimit && tasksInColumn.length >= column.settings.wipLimit) {
        message.error(`该列任务数量已达到限制(${column.settings.wipLimit})`);
        return;
      }

      try {
        await dispatch(
          updateTaskOrder({
            boardId: board.id,
            params: {
              taskId: item.taskId,
              columnId,
              order: index,
            },
          })
        ).unwrap();
      } catch (error: unknown) {
        if (error instanceof Error) {
          message.error(error.message || '更新任务顺序失败');
        } else {
          message.error('更新任务顺序失败');
        }
      }
    },
    [board, columnId, index, dispatch]
  );

  const [{ isOver }, drop] = useDrop({
    accept: ItemTypes.TASK,
    drop: handleDrop,
    collect: (monitor: DropTargetMonitor) => ({
      isOver: monitor.isOver(),
    }),
  });

  return { drop, isOver };
}; 