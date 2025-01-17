/**
 * Board 页面组件 - 任务看板界面
 * 
 * 功能：
 * 1. 展示任务看板的三列布局（待办、进行中、已完成）
 * 2. 支持任务拖拽功能，实现任务状态更新
 * 3. 提供任务的创建、编辑、删除功能
 * 4. 显示任务统计信息
 * 5. 支持任务筛选和排序
 * 
 * @component
 */

import React, { useEffect, useState, memo } from 'react';
import { Button, Row, Col, Card, message } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { useDispatch, useSelector } from 'react-redux';
import { DragDropContext, Droppable, Draggable, DropResult } from 'react-beautiful-dnd';
import { Task, updateTask, setTasks, setCurrentBoard } from '@/store/slices/boardSlice';
import { RootState } from '@/store/types';
import TaskCard from '@/components/TaskCard';
import { TaskFormModal, BoardSummary } from '@/components';
import boardService from '@/services/boardService';
import { AxiosError } from 'axios';
import styles from './index.module.less';

/**
 * 任务列类型定义
 */
interface TaskColumn {
  id: Task['status'];
  title: string;
  tasks: Task[];
}

/**
 * 任务状态列表
 */
const COLUMNS: TaskColumn[] = [
  { id: 'TODO', title: '待办', tasks: [] },
  { id: 'IN_PROGRESS', title: '进行中', tasks: [] },
  { id: 'DONE', title: '已完成', tasks: [] },
];

/**
 * 任务列表属性接口
 */
interface TaskListProps {
  columnId: string;
  tasks: Task[];
  onEdit: (task: Task) => void;
  onDelete: (taskId: string) => void;
  isDragDisabled?: boolean;
}

/**
 * 任务列表容器组件
 */
const TaskList = memo(function TaskList({
  columnId,
  tasks = [],
  onEdit,
  onDelete,
  isDragDisabled = false
}: TaskListProps) {
  const renderDraggable = (task: Task, index: number) => (
    <Draggable
      key={task.id}
      draggableId={task.id}
      index={index}
      isDragDisabled={isDragDisabled}
    >
      {(provided, snapshot) => (
        <div
          ref={provided.innerRef}
          {...provided.draggableProps}
          {...provided.dragHandleProps}
          style={{
            ...provided.draggableProps.style,
            opacity: snapshot.isDragging ? 0.8 : 1
          }}
        >
          <TaskCard
            task={task}
            onEdit={onEdit}
            onDelete={onDelete}
          />
        </div>
      )}
    </Draggable>
  );

  return (
    <Droppable droppableId={columnId} type="TASK">
      {(provided, snapshot) => (
        <div
          ref={provided.innerRef}
          {...provided.droppableProps}
          className={`${styles.taskList} ${snapshot.isDraggingOver ? styles.draggingOver : ''}`}
        >
          {tasks.map((task, index) => renderDraggable(task, index))}
          {provided.placeholder}
        </div>
      )}
    </Droppable>
  );
});

TaskList.displayName = 'TaskList';

const Board: React.FC = () => {
  const dispatch = useDispatch();
  const { tasks, currentBoard } = useSelector((state: RootState) => state.board);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingTask, setEditingTask] = useState<Task | null>(null);

  /**
   * 初始化加载任务数据
   */
  useEffect(() => {
    const fetchActiveBoard = async () => {
      try {
        const response = await boardService.getActiveBoard();
        dispatch(setCurrentBoard(response.data));
      } catch (error) {
        if (error instanceof AxiosError && error.response?.status !== 404) {
          message.error('加载看板失败');
        }
      }
    };
    fetchActiveBoard();
  }, [dispatch]);

  useEffect(() => {
    const fetchTasks = async () => {
      // 如果没有当前看板，不加载任务
      if (!currentBoard) {
        return;
      }
      
      try {
        const response = await boardService.getTasks();
        dispatch(setTasks(response.data));
      } catch (error: unknown) {
        // 忽略 404 错误
        if (error instanceof AxiosError && error.response?.status !== 404) {
          message.error('加载任务失败');
        }
      }
    };
    fetchTasks();
  }, [dispatch, currentBoard]);

  if (!currentBoard) {
    return (
      <div className={styles.container}>
        <Card>暂无看板</Card>
      </div>
    );
  }

  /**
   * 处理任务拖拽结束事件
   */
  const handleDragEnd = async (result: DropResult) => {
    if (!result.destination) return;

    const { source, destination, draggableId } = result;
    if (source.droppableId === destination.droppableId) return;

    try {
      const task = tasks.find(t => t.id === draggableId);
      if (!task) return;

      const updatedTask = {
        ...task,
        status: destination.droppableId as Task['status'],
      };

      await boardService.updateTaskStatus(task.id, updatedTask.status);
      dispatch(updateTask(updatedTask));
      message.success('更新任务状态成功');
    } catch {
      message.error('更新任务状态失败');
    }
  };

  /**
   * 处理任务编辑
   */
  const handleEdit = (task: Task) => {
    setEditingTask(task);
    setIsModalVisible(true);
  };

  /**
   * 处理任务删除
   */
  const handleDelete = async (taskId: string) => {
    try {
      await boardService.deleteTask(taskId);
      dispatch(setTasks(tasks.filter(t => t.id !== taskId)));
      message.success('删除任务成功');
    } catch {
      message.error('删除任务失败');
    }
  };

  /**
   * 获取指定状态的任务列表
   */
  const getColumnTasks = (status: Task['status']) => {
    return tasks.filter(task => task.status === status);
  };

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <BoardSummary />
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={() => setIsModalVisible(true)}
        >
          创建任务
        </Button>
      </div>

      <DragDropContext onDragEnd={handleDragEnd}>
        <Row gutter={16} className={styles.columns}>
          {COLUMNS.map(column => (
            <Col span={8} key={column.id}>
              <Card title={column.title} className={styles.column}>
                <TaskList
                  columnId={column.id}
                  tasks={getColumnTasks(column.id)}
                  onEdit={handleEdit}
                  onDelete={handleDelete}
                />
              </Card>
            </Col>
          ))}
        </Row>
      </DragDropContext>

      <TaskFormModal
        visible={isModalVisible}
        task={editingTask}
        onClose={() => {
          setIsModalVisible(false);
          setEditingTask(null);
        }}
      />
    </div>
  );
};

export default Board; 