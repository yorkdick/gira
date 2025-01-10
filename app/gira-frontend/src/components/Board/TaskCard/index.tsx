import React from 'react';
import { Card, Avatar, Tag, Tooltip } from 'antd';
import {
  ClockCircleOutlined,
  MessageOutlined,
  PaperClipOutlined,
} from '@ant-design/icons';
import { Task, TaskPriority } from '@/types/task';
import { useDragDrop } from '../../../hooks/useDragDrop';
import styles from './style.module.less';

interface TaskCardProps {
  task: Task;
  index: number;
}

// 优先级颜色映射
const priorityColorMap: Record<TaskPriority, string> = {
  [TaskPriority.LOW]: '#52c41a',
  [TaskPriority.MEDIUM]: '#faad14',
  [TaskPriority.HIGH]: '#f5222d',
  [TaskPriority.URGENT]: '#ff4d4f',
};

const TaskCard: React.FC<TaskCardProps> = ({ task, index }) => {
  const { onDragStart } = useDragDrop();

  const handleDragStart = (e: React.DragEvent<HTMLDivElement>) => {
    e.dataTransfer.effectAllowed = 'move';
    onDragStart(task, index);
  };

  return (
    <Card
      className={styles.card}
      bordered={false}
      draggable
      onDragStart={handleDragStart}
    >
      <div className={styles.header}>
        <Tag color={priorityColorMap[task.priority]} className={styles.priority}>
          {task.priority}
        </Tag>
        {task.labels.map((label) => (
          <Tag key={label} className={styles.label}>
            {label}
          </Tag>
        ))}
      </div>
      <div className={styles.title}>{task.title}</div>
      <div className={styles.footer}>
        <div className={styles.left}>
          {task.dueDate && (
            <Tooltip title={task.dueDate}>
              <ClockCircleOutlined className={styles.icon} />
            </Tooltip>
          )}
          {task.attachments.length > 0 && (
            <Tooltip title={`${task.attachments.length} 个附件`}>
              <PaperClipOutlined className={styles.icon} />
              <span className={styles.count}>{task.attachments.length}</span>
            </Tooltip>
          )}
          <MessageOutlined className={styles.icon} />
        </div>
        <div className={styles.right}>
          {task.assignee && (
            <Tooltip title={task.assignee.username}>
              <Avatar
                size="small"
                src={task.assignee.avatar}
                className={styles.avatar}
              >
                {task.assignee.username[0].toUpperCase()}
              </Avatar>
            </Tooltip>
          )}
        </div>
      </div>
    </Card>
  );
};

export default TaskCard; 