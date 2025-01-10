import { useCallback, useRef } from 'react';
import { useDispatch } from 'react-redux';
import { Task } from '@/types/task';
import { updateTask } from '@/store/slices/taskSlice';
import { AppDispatch } from '@/store';

interface DragItem {
  type: 'TASK';
  id: number;
  columnId: number;
  index: number;
}

interface DragResult {
  onDragStart: (task: Task, index: number) => void;
  onDragOver: (e: React.DragEvent<HTMLDivElement>) => void;
  onDrop: (columnId: number, index: number) => void;
}

export const useDrag = (): DragResult => {
  const dispatch = useDispatch<AppDispatch>();
  const dragItem = useRef<DragItem | null>(null);

  const onDragStart = useCallback((task: Task, index: number) => {
    dragItem.current = {
      type: 'TASK',
      id: task.id,
      columnId: task.columnId,
      index,
    };
  }, []);

  const onDragOver = useCallback((e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.dataTransfer.dropEffect = 'move';
  }, []);

  const onDrop = useCallback(
    (columnId: number, index: number) => {
      if (!dragItem.current) return;

      const { id: taskId, columnId: sourceColumnId } = dragItem.current;

      if (sourceColumnId !== columnId) {
        dispatch(
          updateTask({
            id: taskId,
            params: {
              columnId,
              order: index,
            }
          })
        );
      }

      dragItem.current = null;
    },
    [dispatch]
  );

  return {
    onDragStart,
    onDragOver,
    onDrop,
  };
}; 