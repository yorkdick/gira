import { useDrag } from 'react-dnd';
import { Task } from '@/types/task';

export const ItemTypes = {
  TASK: 'task',
};

export interface DragItem {
  type: string;
  taskId: number;
  task: Task;
}

export function useDragTask(task: Task) {
  const [{ isDragging }, drag] = useDrag(() => ({
    type: ItemTypes.TASK,
    item: { type: ItemTypes.TASK, taskId: task.id, task },
    collect: (monitor) => ({
      isDragging: monitor.isDragging(),
    }),
  }));

  return {
    drag,
    isDragging,
  };
} 