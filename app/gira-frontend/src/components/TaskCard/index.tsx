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
import { Card, Avatar, Tag, Tooltip, Dropdown } from 'antd';
import type { MenuProps } from 'antd';
import {
  EditOutlined,
  DeleteOutlined,
  MoreOutlined,
  ClockCircleOutlined,
} from '@ant-design/icons';
import { Task } from '@/store/slices/boardSlice';
import styles from './index.module.less';

/**
 * TaskCard 组件的属性类型定义
 * @interface
 */
interface TaskCardProps {
  /** 任务数据对象 */
  task: Task;
  /** 任务编辑回调函数 */
  onEdit?: (task: Task) => void;
  /** 任务删除回调函数 */
  onDelete?: (taskId: string) => void;
  /** 是否可拖拽 */
  draggable?: boolean;
  /** 拖拽开始回调函数 */
  onDragStart?: (e: React.DragEvent<HTMLDivElement>, task: Task) => void;
  /** 拖拽结束回调函数 */
  onDragEnd?: (e: React.DragEvent<HTMLDivElement>) => void;
}

/**
 * 获取优先级对应的标签颜色
 * @param priority - 任务优先级
 * @returns 对应的标签颜色
 */
const getPriorityColor = (priority: Task['priority']) => {
  const colors = {
    HIGH: 'red',
    MEDIUM: 'orange',
    LOW: 'green',
  };
  return colors[priority];
};

/**
 * 获取状态对应的标签颜色
 * @param status - 任务状态
 * @returns 对应的标签颜色
 */
const getStatusColor = (status: Task['status']) => {
  const colors = {
    TODO: 'default',
    IN_PROGRESS: 'processing',
    DONE: 'success',
  };
  return colors[status];
};

const TaskCard: React.FC<TaskCardProps> = ({
  task,
  onEdit,
  onDelete,
  draggable,
  onDragStart,
  onDragEnd,
}) => {
  /**
   * 操作菜单配置
   */
  const menuItems: MenuProps['items'] = [
    {
      key: 'edit',
      icon: <EditOutlined />,
      label: '编辑',
      onClick: () => onEdit?.(task),
    },
    {
      key: 'delete',
      icon: <DeleteOutlined />,
      label: '删除',
      onClick: () => onDelete?.(task.id),
    },
  ];

  return (
    <Card
      className={styles.taskCard}
      draggable={draggable}
      onDragStart={(e) => onDragStart?.(e, task)}
      onDragEnd={onDragEnd}
    >
      <div className={styles.header}>
        <Tag color={getPriorityColor(task.priority)}>{task.priority}</Tag>
        <Dropdown menu={{ items: menuItems }} trigger={['click']}>
          <MoreOutlined className={styles.more} />
        </Dropdown>
      </div>
      <div className={styles.title}>{task.title}</div>
      <div className={styles.description}>{task.description}</div>
      <div className={styles.footer}>
        <Tag color={getStatusColor(task.status)}>{task.status}</Tag>
        {task.dueDate && (
          <Tooltip title={task.dueDate}>
            <ClockCircleOutlined className={styles.clock} />
          </Tooltip>
        )}
        {task.assignee && (
          <Tooltip title={task.assignee.name}>
            <Avatar
              size="small"
              src={task.assignee.avatar}
              icon={<EditOutlined />}
              className={styles.avatar}
            />
          </Tooltip>
        )}
      </div>
    </Card>
  );
};

export default TaskCard; 