/**
 * TaskBoard 组件 - 看板任务管理界面
 * 
 * 功能：
 * 1. 展示任务看板，包含待办、进行中、已完成三个状态列
 * 2. 支持任务拖拽功能，实现任务状态的快速更新
 * 3. 显示每个状态下的任务数量
 * 4. 支持任务卡片的展示和交互
 * 5. 提供加载状态显示
 * 
 * @component
 * @example
 * ```tsx
 * <TaskBoard />
 * ```
 */

import React, { useEffect } from 'react';
import { DragDropContext, Droppable, Draggable, DropResult } from 'react-beautiful-dnd';
import { Card, Space, Typography, Spin } from 'antd';
import { useDispatch, useSelector } from 'react-redux';
import { AppDispatch, RootState } from '@/store';
import { Task, setTasks } from '@/store/slices/boardSlice';
import boardService from '@/services/boardService';
import TaskCard from '../TaskCard';
import styles from './styles.module.less';

const { Title } = Typography;

/**
 * 任务状态配置
 * 定义了每个状态的标题和背景颜色
 */
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

  /**
   * 初始化加载任务数据
   */
  useEffect(() => {
    const fetchTasks = async () => {
      try {
        const response = await boardService.getTasks();
        dispatch(setTasks(response.data));
      } catch (error) {
        console.error('Failed to fetch tasks:', error);
      }
    };
    void fetchTasks();
  }, [dispatch]);

  /**
   * 处理任务拖拽结束事件
   * 更新任务状态并同步到后端
   * @param result - 拖拽结果对象
   */
  const handleDragEnd = async (result: DropResult) => {
    const { destination, source, draggableId } = result;

    // 如果没有目标位置或者位置没有改变，则不处理
    if (!destination || 
        (destination.droppableId === source.droppableId && 
         destination.index === source.index)) {
      return;
    }

    try {
      // 更新任务状态
      await boardService.updateTaskStatus(draggableId, destination.droppableId as Task['status']);
      // 重新获取任务列表
      const response = await boardService.getTasks();
      dispatch(setTasks(response.data));
    } catch (error) {
      console.error('Failed to update task status:', error);
    }
  };

  /**
   * 获取指定状态的任务列表
   * @param status - 任务状态
   * @returns 任务列表
   */
  const getTasksByStatus = (status: Task['status']): Task[] => {
    return tasks.filter((task: Task) => task.status === status);
  };

  // 显示加载状态
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
                            <TaskCard task={task} />
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