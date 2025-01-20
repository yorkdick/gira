/**
 * Sprints 页面组件 - Sprint管理界面
 * 
 * 功能：
 * 1. 展示所有Sprint的列表
 * 2. 提供Sprint的创建、编辑、删除功能
 * 3. 支持Sprint状态管理（激活、完成、重新开启）
 * 4. 显示Sprint的任务列表和进度
 * 5. 支持任务的添加和移除
 * 
 * @component
 * @example
 * ```tsx
 * <Sprints />
 * ```
 */

import React, { useEffect, useState } from 'react';
import { Button, List, Space, Modal, message, Spin, Form, Input, Row, Col, Typography, Select, Avatar, Tooltip, Collapse } from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  PlayCircleOutlined,
  CheckCircleOutlined,
  ExclamationCircleOutlined,
  ClockCircleOutlined,
  CheckOutlined,
  ArrowUpOutlined,
  MinusOutlined,
  ArrowDownOutlined,
  CaretRightOutlined,
} from '@ant-design/icons';
import { useDispatch, useSelector } from 'react-redux';
import { Sprint, fetchSprints, updateSprint, resetSprints } from '@/store/slices/sprintSlice';
import type { SprintFormData } from '@/components/SprintForm';
import type { SprintCreateDTO, SprintUpdateDTO } from '@/services/sprintService';
import type { CreateTaskRequest } from '@/services/taskService';
import { RootState, AppDispatch } from '@/store/types';
import SprintForm from '@/components/SprintForm';
import sprintService from '@/services/sprintService';
import taskService from '@/services/taskService';
import styles from './index.module.less';
import { Task } from '@/store/slices/boardSlice';
import userService from '@/services/userService';
import type { UserInfo } from '@/store/slices/authSlice';

const { Text } = Typography;
const { Panel } = Collapse;

/**
 * 获取任务优先级对应的图标和颜色
 */
const getPriorityIcon = (priority: Task['priority']) => {
  const config: Record<Task['priority'], { icon: React.ReactNode; color: string }> = {
    HIGH: { icon: <ArrowUpOutlined />, color: '#f5222d' },
    MEDIUM: { icon: <MinusOutlined />, color: '#faad14' },
    LOW: { icon: <ArrowDownOutlined />, color: '#52c41a' },
  };
  return config[priority];
};

/**
 * 获取任务状态对应的图标和颜色
 */
const getStatusIcon = (status: Task['status']) => {
  const config: Record<Task['status'], { icon: React.ReactNode; color: string }> = {
    TODO: { icon: <ClockCircleOutlined />, color: '#bfbfbf' },
    IN_PROGRESS: { icon: <ExclamationCircleOutlined />, color: '#1890ff' },
    DONE: { icon: <CheckOutlined />, color: '#52c41a' },
  };
  return config[status];
};

/**
 * 获取Sprint状态对应的图标和颜色
 */
const getSprintStatusIcon = (status: Sprint['status']) => {
  const config: Record<Sprint['status'], { icon: React.ReactNode; color: string; text: string }> = {
    PLANNING: { icon: <ClockCircleOutlined />, color: '#bfbfbf', text: '规划中' },
    ACTIVE: { icon: <ExclamationCircleOutlined />, color: '#1890ff', text: '进行中' },
    COMPLETED: { icon: <CheckCircleOutlined />, color: '#52c41a', text: '已完成' },
  };
  return config[status];
};

const Sprints: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { sprints = [], loading, error } = useSelector((state: RootState) => state.sprint);
  const { user } = useSelector((state: RootState) => state.auth);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingSprint, setEditingSprint] = useState<Sprint | null>(null);
  const [isTaskModalVisible, setIsTaskModalVisible] = useState(false);
  const [currentSprintId, setCurrentSprintId] = useState<string>('');
  const [taskForm] = Form.useForm();
  const [sprintTasks, setSprintTasks] = useState<Record<string, Task[]>>({});
  const [selectedTask, setSelectedTask] = useState<Task | null>(null);
  const [isTaskDetailModalVisible, setIsTaskDetailModalVisible] = useState(false);
  const [users, setUsers] = useState<UserInfo[]>([]);
  const [expandedSprints, setExpandedSprints] = useState<string[]>([]);

  useEffect(() => {
    dispatch(fetchSprints());
    return () => {
      dispatch(resetSprints());
    };
  }, [dispatch]);

  useEffect(() => {
    // 获取每个 Sprint 的任务列表
    const fetchSprintTasks = async () => {
      const tasks: Record<string, Task[]> = {};
      for (const sprint of sprints) {
        try {
          const response = await sprintService.getTasks(sprint.id);
          tasks[sprint.id] = response.data;
        } catch (error) {
          console.error(`获取Sprint ${sprint.id} 的任务列表失败:`, error);
        }
      }
      setSprintTasks(tasks);
    };

    if (sprints.length > 0) {
      void fetchSprintTasks();
    }
  }, [sprints]);

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

  // 设置默认展开的 Sprint
  useEffect(() => {
    if (sprints.length > 0) {
      // 查找进行中的 Sprint
      const activeSprint = sprints.find(sprint => sprint.status === 'ACTIVE');
      if (activeSprint) {
        setExpandedSprints([activeSprint.id]);
      } else {
        // 如果没有进行中的 Sprint，展开第一个
        setExpandedSprints([sprints[0].id]);
      }
    }
  }, [sprints]);

  /**
   * 处理Sprint表单提交
   * @param values - 表单数据
   */
  const handleSubmit = async (values: SprintFormData) => {
    try {
      if (editingSprint) {
        const updateData: SprintUpdateDTO = {
          name: values.name,
          startDate: values.startDate,
          endDate: values.endDate,
        };
        await dispatch(updateSprint({
          id: editingSprint.id,
          data: updateData,
        })).unwrap();
        message.success('更新Sprint成功');
      } else {
        const createData: SprintCreateDTO = {
          name: values.name,
          startDate: values.startDate,
          endDate: values.endDate,
        };
        await sprintService.createSprint(createData);
        void dispatch(fetchSprints());
        message.success('创建Sprint成功');
      }
      setIsModalVisible(false);
      setEditingSprint(null);
    } catch (error) {
      // 不显示错误提示，因为request.ts中已经处理了错误提示
      console.error('Sprint操作失败:', error);
    }
  };

  /**
   * 处理Sprint状态更新
   * @param sprint - 要更新的Sprint
   * @param action - 状态更新动作
   */
  const handleStatusUpdate = async (sprint: Sprint, action: 'start' | 'complete') => {
    try {
      switch (action) {
        case 'start':
          await sprintService.startSprint(sprint.id);
          break;
        case 'complete':
          await sprintService.completeSprint(sprint.id);
          break;
      }
      // 重新获取Sprint列表
      void dispatch(fetchSprints());
      message.success('更新Sprint状态成功');
    } catch (error) {
      console.error('更新Sprint状态失败:', error);
    }
  };

  /**
   * 处理打开任务创建对话框
   * @param sprintId - Sprint ID
   */
  const handleOpenTaskModal = (sprintId: string) => {
    setCurrentSprintId(sprintId);
    setIsTaskModalVisible(true);
  };

  /**
   * 处理任务创建
   * @param values - 表单数据
   */
  const handleTaskSubmit = async (values: {
    title: string;
    description: string;
    priority: 'LOW' | 'MEDIUM' | 'HIGH';
    status: 'TODO' | 'IN_PROGRESS' | 'DONE';
    assigneeId?: string;
  }) => {
    try {
      const taskData: CreateTaskRequest = {
        ...values,
        sprintId: currentSprintId,
        description: values.description || '',
      };
      await taskService.createTask(taskData);
      void dispatch(fetchSprints());
      message.success('创建任务成功');
      setIsTaskModalVisible(false);
      taskForm.resetFields();
    } catch (error) {
      console.error('创建任务失败:', error);
    }
  };

  /**
   * 处理删除任务
   */
  const handleDeleteTask = async (taskId: string) => {
    try {
      await taskService.deleteTask(taskId);
      void dispatch(fetchSprints());
      message.success('删除任务成功');
      setIsTaskDetailModalVisible(false);
      setSelectedTask(null);
    } catch (error) {
      console.error('删除任务失败:', error);
    }
  };

  /**
   * 处理任务更新
   * @param values - 表单数据
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
      void dispatch(fetchSprints());
      message.success('更新任务成功');
      setIsTaskDetailModalVisible(false);
      setSelectedTask(null);
    } catch (error) {
      console.error('更新任务失败:', error);
    }
  };

  // 处理面板展开/折叠
  const handlePanelChange = (keys: string | string[]) => {
    setExpandedSprints(typeof keys === 'string' ? [keys] : keys);
  };

  if (error) {
    return <div className={styles.error}>{error}</div>;
  }

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h2>Sprint列表</h2>
        {user?.role === 'ADMIN' && (
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={() => setIsModalVisible(true)}
          >
            创建Sprint
          </Button>
        )}
      </div>

      <Spin spinning={loading}>
        <Collapse
          ghost
          activeKey={expandedSprints}
          onChange={handlePanelChange}
          expandIcon={({ isActive }) => <CaretRightOutlined rotate={isActive ? 90 : 0} />}
          className={styles.sprintList}
        >
          {Array.isArray(sprints) && sprints.map((sprint) => (
            <Panel
              key={sprint.id}
              header={
                <Row align="middle" style={{ width: '100%' }}>
                  <Col flex="auto">
                    <Space size="middle">
                      <Text strong>{sprint.name}</Text>
                      <Text type="secondary" style={{ fontSize: '12px' }}>
                        {new Date(sprint.startDate).toLocaleDateString()} - {new Date(sprint.endDate).toLocaleDateString()}
                      </Text>
                      <Space size="small">
                        <Text type="secondary">总任务:</Text>
                        <Text>{(sprintTasks[sprint.id] || []).length}</Text>
                        <Text type="secondary">待办:</Text>
                        <Text>{(sprintTasks[sprint.id] || []).filter(t => t.status === 'TODO').length}</Text>
                        <Text type="secondary">进行中:</Text>
                        <Text>{(sprintTasks[sprint.id] || []).filter(t => t.status === 'IN_PROGRESS').length}</Text>
                        <Text type="secondary">已完成:</Text>
                        <Text>{(sprintTasks[sprint.id] || []).filter(t => t.status === 'DONE').length}</Text>
                      </Space>
                    </Space>
                  </Col>
                  <Col>
                    <Space>
                      <Tooltip title={getSprintStatusIcon(sprint.status).text}>
                        <span style={{ color: getSprintStatusIcon(sprint.status).color }}>
                          {getSprintStatusIcon(sprint.status).icon}
                        </span>
                      </Tooltip>
                      {user?.role === 'ADMIN' && (
                        <Space size="small">
                          <Tooltip title="添加任务">
                            <Button
                              type="text"
                              size="small"
                              icon={<PlusOutlined />}
                              onClick={(e) => {
                                e.stopPropagation();
                                handleOpenTaskModal(sprint.id);
                              }}
                              disabled={sprint.status === 'COMPLETED'}
                            />
                          </Tooltip>
                          <Tooltip title="编辑Sprint">
                            <Button
                              type="text"
                              size="small"
                              icon={<EditOutlined />}
                              onClick={(e) => {
                                e.stopPropagation();
                                setEditingSprint(sprint);
                                setIsModalVisible(true);
                              }}
                            />
                          </Tooltip>
                          <Tooltip title="激活Sprint">
                            <Button
                              type="text"
                              size="small"
                              icon={<PlayCircleOutlined />}
                              onClick={(e) => {
                                e.stopPropagation();
                                handleStatusUpdate(sprint, 'start');
                              }}
                              disabled={sprint.status !== 'PLANNING'}
                            />
                          </Tooltip>
                          <Tooltip title="完成Sprint">
                            <Button
                              type="text"
                              size="small"
                              icon={<CheckCircleOutlined />}
                              onClick={(e) => {
                                e.stopPropagation();
                                handleStatusUpdate(sprint, 'complete');
                              }}
                              disabled={sprint.status !== 'ACTIVE'}
                            />
                          </Tooltip>
                        </Space>
                      )}
                    </Space>
                  </Col>
                </Row>
              }
              className={styles.sprintPanel}
            >
              <List
                size="small"
                dataSource={sprintTasks[sprint.id] || []}
                renderItem={task => (
                  <List.Item
                    onClick={() => {
                      setSelectedTask(task);
                      setIsTaskDetailModalVisible(true);
                    }}
                    style={{ cursor: 'pointer' }}
                    className={styles.taskItem}
                  >
                    <Row style={{ width: '100%', alignItems: 'center' }}>
                      <Col flex="auto">
                        <Text>{task.title}</Text>
                      </Col>
                      <Col>
                        <Space size="small">
                          <Tooltip title={`优先级: ${
                            task.priority === 'HIGH' ? '高' :
                            task.priority === 'MEDIUM' ? '中' :
                            '低'
                          }`}>
                            <span style={{ color: getPriorityIcon(task.priority).color }}>
                              {getPriorityIcon(task.priority).icon}
                            </span>
                          </Tooltip>
                          <Tooltip title={`状态: ${
                            task.status === 'TODO' ? '待处理' :
                            task.status === 'IN_PROGRESS' ? '进行中' :
                            '已完成'
                          }`}>
                            <span style={{ color: getStatusIcon(task.status).color }}>
                              {getStatusIcon(task.status).icon}
                            </span>
                          </Tooltip>
                          {task.assignee && (
                            <Tooltip title={`负责人: ${task.assignee.username}`}>
                              <Avatar
                                size="small"
                                style={{ backgroundColor: '#1890ff' }}
                              >
                                {task.assignee.username.slice(0, 1).toUpperCase()}
                              </Avatar>
                            </Tooltip>
                          )}
                        </Space>
                      </Col>
                    </Row>
                  </List.Item>
                )}
              />
            </Panel>
          ))}
        </Collapse>
      </Spin>

      <Modal
        title={editingSprint ? '编辑Sprint' : '创建Sprint'}
        open={isModalVisible}
        onCancel={() => {
          setIsModalVisible(false);
          setEditingSprint(null);
        }}
        footer={null}
      >
        <SprintForm
          initialValues={editingSprint ? {
            name: editingSprint.name,
            startDate: editingSprint.startDate,
            endDate: editingSprint.endDate,
          } : undefined}
          onSubmit={handleSubmit}
          onCancel={() => {
            setIsModalVisible(false);
            setEditingSprint(null);
          }}
        />
      </Modal>

      <Modal
        title="创建任务"
        open={isTaskModalVisible}
        onCancel={() => {
          setIsTaskModalVisible(false);
          taskForm.resetFields();
        }}
        footer={null}
      >
        <Form
          form={taskForm}
          layout="vertical"
          onFinish={handleTaskSubmit}
          initialValues={{
            priority: 'MEDIUM',
            status: 'TODO',
          }}
        >
          <Form.Item
            name="title"
            label="标题"
            rules={[{ required: true, message: '请输入任务标题' }]}
          >
            <Input placeholder="请输入任务标题" />
          </Form.Item>
          <Form.Item
            name="description"
            label="描述"
          >
            <Input.TextArea placeholder="请输入任务描述" rows={4} />
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
            rules={[{ required: true, message: '请选择状态' }]}
          >
            <Select>
              <Select.Option value="TODO">待处理</Select.Option>
              <Select.Option value="IN_PROGRESS">进行中</Select.Option>
              <Select.Option value="DONE">已完成</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item
            name="assigneeId"
            label="负责人"
          >
            <Select allowClear placeholder="请选择负责人">
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
                创建
              </Button>
              <Button onClick={() => {
                setIsTaskModalVisible(false);
                taskForm.resetFields();
              }}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>

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

export default Sprints; 