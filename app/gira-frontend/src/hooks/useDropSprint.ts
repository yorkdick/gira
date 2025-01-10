import { useDrop } from 'react-dnd';
import { useDispatch } from 'react-redux';
import { message } from 'antd';
import { Sprint } from '@/types/sprint';
import { moveTask } from '@/store/slices/taskSlice';
import { AppDispatch } from '@/store';
import { ItemTypes, DragItem } from './useDragTask';

export function useDropSprint(sprint: Sprint) {
  const dispatch = useDispatch<AppDispatch>();

  const [{ isOver }, drop] = useDrop(() => ({
    accept: ItemTypes.TASK,
    drop: async (item: DragItem) => {
      try {
        await dispatch(
          moveTask({
            taskId: item.taskId,
            sprintId: sprint.id,
          })
        ).unwrap();
        message.success('任务已移动到Sprint');
      } catch {
        message.error('移动任务失败');
      }
    },
    collect: (monitor) => ({
      isOver: monitor.isOver(),
    }),
  }));

  return {
    drop,
    isOver,
  };
} 