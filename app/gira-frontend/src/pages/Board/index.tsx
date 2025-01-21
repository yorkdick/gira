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

import React, { useEffect, useState } from 'react';
import { Row, Col, Card, Space, Typography, Modal, Form, Input, Select, Button, message } from 'antd';
import { useDispatch, useSelector } from 'react-redux';
import { DragDropContext, Droppable, Draggable, DropResult, DraggableStyle } from '@hello-pangea/dnd';
import {
  CheckCircleOutlined,
  ClockCircleOutlined,
  MinusCircleOutlined,
} from '@ant-design/icons';
import { Task, updateTask, setTasks, setCurrentBoard } from '@/store/slices/boardSlice';
import { RootState } from '@/store/types';
import TaskCard from '@/components/TaskCard';
import boardService from '@/services/boardService';
import taskService from '@/services/taskService';
import userService from '@/services/userService';
import type { UserInfo } from '@/store/slices/authSlice';
import styles from './index.module.less';

const { Text } = Typography;

const COLUMNS = [
  { 
    id: 'TODO', 
    title: '待办', 
    icon: <MinusCircleOutlined style={{ color: '#8c8c8c' }} />,
  },
  { 
    id: 'IN_PROGRESS', 
    title: '进行中', 
    icon: <ClockCircleOutlined style={{ color: '#096dd9' }} />,
  },
  { 
    id: 'DONE', 
    title: '已完成', 
    icon: <CheckCircleOutlined style={{ color: '#389e0d' }} />,
  },
];

const getDraggableStyle = (isDragging: boolean, draggableStyle?: DraggableStyle): React.CSSProperties => ({
  ...draggableStyle,
  userSelect: 'none',
  opacity: isDragging ? 0.8 : 1,
  transform: draggableStyle?.transform
});

const Board: React.FC = () => {
  const dispatch = useDispatch();
  const { tasks = [], currentBoard } = useSelector((state: RootState) => state.board);
  const { user } = useSelector((state: RootState) => state.auth);
  const [selectedTask, setSelectedTask] = useState<Task | null>(null);
  const [isTaskDetailModalVisible, setIsTaskDetailModalVisible] = useState(false);
  const [users, setUsers] = useState<UserInfo[]>([]);

  useEffect(() => {
    const fetchActiveBoard = async () => {
      try {
        const response = await boardService.getActiveBoard();
        dispatch(setCurrentBoard(response.data));
      } catch (error) {
        console.error('加载看板失败:', error);
      }
    };
    fetchActiveBoard();
  }, [dispatch]);

  useEffect(() => {
    const fetchTasks = async () => {
      if (!currentBoard) return;
      try {
        const response = await boardService.getTasks();
        dispatch(setTasks(response.data));
      } catch (error) {
        console.error('加载任务失败:', error);
      }
    };
    fetchTasks();
  }, [dispatch, currentBoard]);

  useEffect(() => {
    // 获取用户列表
    const fetchUsers = async () => {
      try {
        const response = await userService.getUsers();
        setUsers(response.data.content || []);
      } catch (error) {
        console.error('获取用户列表失败:', error);
        setUsers([]);
      }
    };

    void fetchUsers();
  }, []);

  const handleDragEnd = async (result: DropResult) => {
    if (!result.destination) return;

    const { source, destination, draggableId } = result;
    if (source.droppableId === destination.droppableId) return;

    try {
      const task = tasks.find(t => String(t.id) === draggableId);
      if (!task) return;

      const updatedTask = {
        ...task,
        status: destination.droppableId as Task['status'],
      };

      await boardService.updateTaskStatus(String(task.id), updatedTask.status);
      dispatch(updateTask(updatedTask));
    } catch (error) {
      console.error('更新任务状态失败:', error);
    }
  };

  const getColumnTasks = (status: Task['status']) => {
    return tasks.filter(task => task.status === status);
  };

  const totalTasks = tasks.length;
  const todoTasks = getColumnTasks('TODO').length;
  const inProgressTasks = getColumnTasks('IN_PROGRESS').length;
  const doneTasks = getColumnTasks('DONE').length;

  /**
   * 处理任务更新
   */
  const handleTaskUpdate = async (values: {
    title: string;
    description: string;
    priority: 'LOW' | 'MEDIUM' | 'HIGH';
    assigneeId?: string;
  }) => {
    if (!selectedTask) return;
    try {
      await taskService.updateTask(selectedTask.id, {
        ...values,
        description: values.description || '',
      });
      // 重新获取任务列表
      const response = await boardService.getTasks();
      dispatch(setTasks(response.data));
      message.success('更新任务成功');
      setIsTaskDetailModalVisible(false);
      setSelectedTask(null);
    } catch (error) {
      console.error('更新任务失败:', error);
    }
  };

  /**
   * 处理删除任务
   */
  const handleDeleteTask = async (taskId: string) => {
    try {
      await taskService.deleteTask(taskId);
      // 重新获取任务列表
      const response = await boardService.getTasks();
      dispatch(setTasks(response.data));
      message.success('删除任务成功');
      setIsTaskDetailModalVisible(false);
      setSelectedTask(null);
    } catch (error) {
      console.error('删除任务失败:', error);
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <Space size="middle">
          <Text strong>{currentBoard?.name}</Text>
          <Space size="small">
            <Text type="secondary">总任务:</Text>
            <Text>{totalTasks}</Text>
            <Text type="secondary">待办:</Text>
            <Text>{todoTasks}</Text>
            <Text type="secondary">进行中:</Text>
            <Text>{inProgressTasks}</Text>
            <Text type="secondary">已完成:</Text>
            <Text>{doneTasks}</Text>
            <Text type="secondary">完成率:</Text>
            <Text>{totalTasks > 0 ? Math.round((doneTasks / totalTasks) * 100) : 0}%</Text>
          </Space>
        </Space>
      </div>

      <DragDropContext onDragEnd={handleDragEnd}>
        <Row gutter={16} className={styles.columns}>
          {COLUMNS.map(column => (
            <Col span={8} key={column.id}>
              <Card 
                title={
                  <Space>
                    {column.icon}
                    <Text>{column.title}</Text>
                  </Space>
                } 
                className={styles.column}
              >
                <Droppable 
                  droppableId={column.id}
                  type="TASK"
                >
                  {(provided, snapshot) => (
                    <div
                      ref={provided.innerRef}
                      {...provided.droppableProps}
                      className={`${styles.taskList} ${snapshot.isDraggingOver ? styles.draggingOver : ''}`}
                    >
                      {getColumnTasks(column.id as Task['status']).map((task, index) => (
                        <Draggable 
                          key={String(task.id)} 
                          draggableId={String(task.id)} 
                          index={index}
                        >
                          {(provided, snapshot) => (
                            <div
                              ref={provided.innerRef}
                              {...provided.draggableProps}
                              {...provided.dragHandleProps}
                              className={styles.taskItem}
                              style={getDraggableStyle(snapshot.isDragging, provided.draggableProps.style)}
                              onClick={() => {
                                setSelectedTask(task);
                                setIsTaskDetailModalVisible(true);
                              }}
                            >
                              <TaskCard task={task} />
                            </div>
                          )}
                        </Draggable>
                      ))}
                      {provided.placeholder}
                    </div>
                  )}
                </Droppable>
              </Card>
            </Col>
          ))}
        </Row>
      </DragDropContext>

      {/* 任务详情模态框 */}
      <Modal
        title="任务详情"
        open={isTaskDetailModalVisible}
        onCancel={() => {
          setIsTaskDetailModalVisible(false);
          setSelectedTask(null);
        }}
        footer={null}
      >
        {selectedTask && (
          <Form
            layout="vertical"
            initialValues={{
              title: selectedTask.title,
              description: selectedTask.description,
              priority: selectedTask.priority,
              status: selectedTask.status,
              assigneeId: selectedTask.assignee?.id,
            }}
            onFinish={handleTaskUpdate}
          >
            <Form.Item
              name="title"
              label="标题"
              rules={[{ required: true, message: '请输入任务标题' }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="description"
              label="描述"
            >
              <Input.TextArea rows={4} />
            </Form.Item>
            <Form.Item
              name="priority"
              label="优先级"
              rules={[{ required: true, message: '请选择优先级' }]}
            >
              <Select>
                <Select.Option value="LOW">低</Select.Option>
                <Select.Option value="MEDIUM">中</Select.Option>
                <Select.Option value="HIGH">高</Select.Option>
              </Select>
            </Form.Item>
            <Form.Item
              name="status"
              label="状态"
            >
              <Select disabled>
                <Select.Option value="TODO">待处理</Select.Option>
                <Select.Option value="IN_PROGRESS">进行中</Select.Option>
                <Select.Option value="DONE">已完成</Select.Option>
              </Select>
            </Form.Item>
            <Form.Item
              name="assigneeId"
              label="负责人"
            >
              <Select allowClear>
                {Array.isArray(users) && users.map(user => (
                  <Select.Option key={user.id} value={user.id}>
                    {user.username}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>
            <Form.Item>
              <Space>
                <Button type="primary" htmlType="submit">
                  保存
                </Button>
                {user?.role === 'ADMIN' && (
                  <Button type="primary" danger onClick={() => handleDeleteTask(selectedTask.id)}>
                    删除
                  </Button>
                )}
                <Button onClick={() => {
                  setIsTaskDetailModalVisible(false);
                  setSelectedTask(null);
                }}>
                  取消
                </Button>
              </Space>
            </Form.Item>
          </Form>
        )}
      </Modal>
    </div>
  );
};

export default Board; 