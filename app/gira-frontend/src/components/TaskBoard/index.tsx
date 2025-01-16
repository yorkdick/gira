import React, { useEffect } from 'react';
import { DragDropContext, Droppable, Draggable, DropResult } from 'react-beautiful-dnd';
import { Card, Space, Typography, Spin } from 'antd';
import { useDispatch, useSelector } from 'react-redux';
import { AppDispatch, RootState } from '@/store';
import { fetchTasks, updateTaskStatusAsync, Task } from '@/store/slices/boardSlice';
import TaskCard from '../TaskCard';
import styles from './styles.module.less';

const { Title } = Typography;

const TASK_STATUS = {
  TODO: { title: '待办', color: '#f0f0f0' },
  IN_PROGRESS: { title: '进行中', color: '#e6f7ff' },
  DONE: { title: '已完成', color: '#f6ffed' },
} as const;

const TaskBoard: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { tasks, loading } = useSelector((state: RootState) => {
    const boardState = state.board;
    return {
      tasks: boardState?.tasks || [],
      loading: boardState?.loading || false,
    };
  });

  useEffect(() => {
    dispatch(fetchTasks());
  }, [dispatch]);

  const handleDragEnd = (result: DropResult) => {
    const { destination, source, draggableId } = result;

    // 如果没有目标位置或者位置没有改变，则不处理
    if (!destination || 
        (destination.droppableId === source.droppableId && 
         destination.index === source.index)) {
      return;
    }

    // 更新任务状态
    dispatch(updateTaskStatusAsync({
      taskId: draggableId,
      status: destination.droppableId as Task['status']
    }));
  };

  const getTasksByStatus = (status: Task['status']): Task[] => {
    return tasks.filter((task: Task) => task.status === status);
  };

  if (loading) {
    return (
      <div className={styles.loadingContainer}>
        <Spin size="large" />
      </div>
    );
  }

  return (
    <DragDropContext onDragEnd={handleDragEnd}>
      <div className={styles.taskBoard}>
        {Object.entries(TASK_STATUS).map(([status, { title, color }]) => (
          <Droppable key={status} droppableId={status}>
            {(provided, snapshot) => (
              <div
                ref={provided.innerRef}
                {...provided.droppableProps}
                className={styles.taskColumn}
                style={{ backgroundColor: snapshot.isDraggingOver ? '#fafafa' : color }}
              >
                <Card
                  title={<Title level={5}>{title} ({getTasksByStatus(status as Task['status']).length})</Title>}
                  className={styles.taskList}
                  bordered={false}
                >
                  <Space direction="vertical" className={styles.taskSpace}>
                    {getTasksByStatus(status as Task['status']).map((task: Task, index: number) => (
                      <Draggable key={task.id} draggableId={task.id} index={index}>
                        {(provided, snapshot) => (
                          <div
                            ref={provided.innerRef}
                            {...provided.draggableProps}
                            {...provided.dragHandleProps}
                            style={{
                              ...provided.draggableProps.style,
                              opacity: snapshot.isDragging ? 0.8 : 1,
                            }}
                          >
                            <TaskCard data={task} />
                          </div>
                        )}
                      </Draggable>
                    ))}
                    {provided.placeholder}
                  </Space>
                </Card>
              </div>
            )}
          </Droppable>
        ))}
      </div>
    </DragDropContext>
  );
};

export default TaskBoard; 