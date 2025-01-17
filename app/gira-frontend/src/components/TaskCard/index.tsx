/**
 * TaskCard 组件 - 任务卡片展示组件
 * 
 * 功能：
 * 1. 展示任务的基本信息（标题、描述、状态等）
 * 2. 显示任务的优先级标识
 * 3. 展示经办人信息
 * 4. 提供任务编辑和删除功能
 * 5. 支持拖拽功能用于更新任务状态
 * 
 * @component
 * @example
 * ```tsx
 * <TaskCard
 *   task={taskData}
 *   onEdit={handleEdit}
 *   onDelete={handleDelete}
 *   draggable
 * />
 * ```
 */

import React from 'react';
import { Typography, Tooltip, Avatar, Space } from 'antd';
import {
  ArrowUpOutlined,
  MinusOutlined,
  ArrowDownOutlined,
  CheckOutlined,
  ExclamationCircleOutlined,
  ClockCircleOutlined
} from '@ant-design/icons';
import { Task } from '@/store/slices/boardSlice';
import styles from './index.module.less';

const { Text } = Typography;

interface TaskCardProps {
  task: Task;
}

const TaskCard: React.FC<TaskCardProps> = ({ task }) => {
  const getPriorityIcon = (priority: string) => {
    switch (priority) {
      case 'HIGH':
        return {
          icon: <ArrowUpOutlined />,
          color: '#f5222d'
        };
      case 'MEDIUM':
        return {
          icon: <MinusOutlined />,
          color: '#fa8c16'
        };
      case 'LOW':
        return {
          icon: <ArrowDownOutlined />,
          color: '#52c41a'
        };
      default:
        return {
          icon: <ArrowDownOutlined />,
          color: '#52c41a'
        };
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'DONE':
        return {
          icon: <CheckOutlined />,
          color: '#52c41a'
        };
      case 'IN_PROGRESS':
        return {
          icon: <ExclamationCircleOutlined />,
          color: '#1890ff'
        };
      case 'TODO':
        return {
          icon: <ClockCircleOutlined />,
          color: '#8c8c8c'
        };
      default:
        return {
          icon: <ClockCircleOutlined />,
          color: '#8c8c8c'
        };
    }
  };

  return (
    <div className={styles.taskCard}>
      <div className={styles.header}>
        <Text className={styles.title}>{task.title}</Text>
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
      </div>
      {task.description && (
        <Text className={styles.description}>{task.description}</Text>
      )}
    </div>
  );
};

export default TaskCard; 