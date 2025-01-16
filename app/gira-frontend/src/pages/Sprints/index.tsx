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
import { Card, Button, List, Tag, Space, Modal, message, Spin, Form, Input } from 'antd';
import { PlusOutlined, EditOutlined, PlayCircleOutlined, CheckCircleOutlined, DeleteOutlined } from '@ant-design/icons';
import { useDispatch, useSelector } from 'react-redux';
import { Sprint, fetchSprints, updateSprint, deleteSprint, resetSprints } from '@/store/slices/sprintSlice';
import type { SprintFormData } from '@/components/SprintForm';
import type { SprintCreateDTO, SprintUpdateDTO } from '@/services/sprintService';
import type { CreateTaskRequest } from '@/services/taskService';
import { RootState, AppDispatch } from '@/store/types';
import SprintForm from '@/components/SprintForm';
import sprintService from '@/services/sprintService';
import taskService from '@/services/taskService';
import styles from './index.module.less';
import { Task } from '@/store/slices/boardSlice';

/**
 * 获取Sprint状态对应的标签颜色
 * @param status - Sprint状态
 * @returns 标签颜色
 */
const getStatusColor = (status: Sprint['status']) => {
  const colors: Record<Sprint['status'], string> = {
    PLANNING: 'default',
    ACTIVE: 'processing',
    COMPLETED: 'success',
  };
  return colors[status];
};

/**
 * 获取Sprint状态的中文描述
 * @param status - Sprint状态
 * @returns 状态描述
 */
const getStatusText = (status: Sprint['status']) => {
  const texts: Record<Sprint['status'], string> = {
    PLANNING: '规划中',
    ACTIVE: '进行中',
    COMPLETED: '已完成',
  };
  return texts[status];
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

  /**
   * 处理Sprint表单提交
   * @param values - 表单数据
   */
  const handleSubmit = async (values: SprintFormData) => {
    try {
      if (editingSprint) {
        const updateData: SprintUpdateDTO = {
          name: values.name,
          description: values.description,
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
          description: values.description,
          startDate: values.startDate,
          endDate: values.endDate,
        };
        await sprintService.createSprint(createData);
        void dispatch(fetchSprints());
        message.success('创建Sprint成功');
      }
      setIsModalVisible(false);
      setEditingSprint(null);
    } catch {
      message.error(editingSprint ? '更新Sprint失败' : '创建Sprint失败');
    }
  };

  /**
   * 处理Sprint状态更新
   * @param sprint - 要更新的Sprint
   * @param action - 状态更新动作
   */
  const handleStatusUpdate = async (sprint: Sprint, action: 'start' | 'complete' | 'reopen') => {
    try {
      let response;
      switch (action) {
        case 'start':
          response = await sprintService.startSprint(sprint.id);
          break;
        case 'complete':
          response = await sprintService.completeSprint(sprint.id);
          break;
        case 'reopen':
          response = await sprintService.reopenSprint(sprint.id);
          break;
      }
      if (response?.data) {
        await dispatch(updateSprint({
          id: sprint.id,
          data: response.data,
        })).unwrap();
        message.success('更新Sprint状态成功');
      }
    } catch {
      message.error('更新Sprint状态失败');
    }
  };

  /**
   * 处理Sprint删除
   * @param sprintId - 要删除的SprintID
   */
  const handleDelete = async (sprintId: string) => {
    try {
      await dispatch(deleteSprint(sprintId)).unwrap();
      message.success('删除Sprint成功');
    } catch {
      message.error('删除Sprint失败');
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
  const handleTaskSubmit = async (values: { title: string; description: string }) => {
    try {
      const taskData: CreateTaskRequest = {
        ...values,
        sprintId: currentSprintId,
        priority: 'MEDIUM',
        status: 'TODO',
      };
      await taskService.createTask(taskData);
      void dispatch(fetchSprints());
      message.success('创建任务成功');
      setIsTaskModalVisible(false);
      taskForm.resetFields();
    } catch {
      message.error('创建任务失败');
    }
  };

  /**
   * 处理从Sprint移除任务
   * @param sprintId - Sprint ID
   * @param taskId - 任务ID
   */
  const handleRemoveTask = async (sprintId: string, taskId: string) => {
    try {
      // 将任务的 sprintId 设为 undefined 来移除任务
      await taskService.updateTask(taskId, { sprintId: undefined });
      void dispatch(fetchSprints());
      message.success('移除任务成功');
    } catch {
      message.error('移除任务失败');
    }
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
        <List
          grid={{ gutter: 16, column: 1 }}
          dataSource={Array.isArray(sprints) ? sprints : []}
          locale={{ emptyText: '暂无Sprint数据' }}
          renderItem={(sprint) => (
            <List.Item key={sprint.id}>
              <Card
                title={sprint.name}
                extra={
                  <Tag color={getStatusColor(sprint.status)}>
                    {getStatusText(sprint.status)}
                  </Tag>
                }
                className={styles.sprintCard}
              >
                <p className={styles.description}>{sprint.description}</p>
                <div className={styles.sprintInfo}>
                  <div className={styles.dates}>
                    开始：{new Date(sprint.startDate).toLocaleDateString()} | 结束：{new Date(sprint.endDate).toLocaleDateString()}
                  </div>
                  <div className={styles.tasks}>
                    <List
                      size="small"
                      dataSource={sprintTasks[sprint.id] || []}
                      renderItem={task => (
                        <List.Item
                          actions={[
                            user?.role === 'ADMIN' && sprint.status !== 'COMPLETED' && (
                              <Button
                                type="text"
                                danger
                                size="small"
                                onClick={() => handleRemoveTask(sprint.id, task.id)}
                              >
                                移除
                              </Button>
                            ),
                          ].filter(Boolean)}
                        >
                          <List.Item.Meta
                            title={task.title}
                            description={task.status}
                          />
                        </List.Item>
                      )}
                    />
                  </div>
                </div>

                {user?.role === 'ADMIN' && (
                  <Space className={styles.actions}>
                    <Button
                      type="text"
                      size="small"
                      icon={<PlusOutlined />}
                      onClick={() => handleOpenTaskModal(sprint.id)}
                      disabled={sprint.status === 'COMPLETED'}
                    >
                      添加任务
                    </Button>
                    <Button
                      type="text"
                      size="small"
                      icon={<EditOutlined />}
                      onClick={() => {
                        setEditingSprint(sprint);
                        setIsModalVisible(true);
                      }}
                    >
                      编辑
                    </Button>
                    <Button
                      type="text"
                      size="small"
                      icon={<PlayCircleOutlined />}
                      onClick={() => handleStatusUpdate(sprint, 'start')}
                      disabled={sprint.status !== 'PLANNING'}
                    >
                      激活
                    </Button>
                    <Button
                      type="text"
                      size="small"
                      icon={<CheckCircleOutlined />}
                      onClick={() => handleStatusUpdate(sprint, 'complete')}
                      disabled={sprint.status !== 'ACTIVE'}
                    >
                      完成
                    </Button>
                    <Button
                      type="text"
                      danger
                      size="small"
                      icon={<DeleteOutlined />}
                      onClick={() => handleDelete(sprint.id)}
                    >
                      删除
                    </Button>
                  </Space>
                )}
              </Card>
            </List.Item>
          )}
        />
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
            description: editingSprint.description,
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
    </div>
  );
};

export default Sprints; 