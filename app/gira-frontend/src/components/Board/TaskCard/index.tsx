import React from 'react';
import { Card, Tag, Avatar, Tooltip } from 'antd';
import { Task, TaskPriority } from '@/types/task';
import { useDragTask } from '@/hooks/useDragDrop';
import styles from './style.module.less';

interface TaskCardProps {
  task: Task;
  index: number;
  onClick?: (task: Task) => void;
}

const priorityColorMap: Record<TaskPriority, string> = {
  [TaskPriority.LOW]: '#52c41a',
  [TaskPriority.MEDIUM]: '#faad14',
  [TaskPriority.HIGH]: '#f5222d',
  [TaskPriority.URGENT]: '#ff4d4f',
};

const TaskCard: React.FC<TaskCardProps> = ({ task, index, onClick }) => {
  const { drag, isDragging } = useDragTask(task, index);

  return (
    <div
      ref={drag}
      className={`${styles.taskCard} ${isDragging ? styles.dragging : ''}`}
      onClick={() => onClick?.(task)}
    >
      <Card size="small" bordered={false}>
        <div className={styles.header}>
          <Tag color={priorityColorMap[task.priority]}>{task.priority}</Tag>
          {task.labels?.map((label) => (
            <Tag key={label}>{label}</Tag>
          ))}
        </div>
        <div className={styles.title}>{task.title}</div>
        <div className={styles.footer}>
          {task.assignee && (
            <Tooltip title={task.assignee.name}>
              <Avatar
                size="small"
                src={task.assignee.avatar}
                className={styles.avatar}
              >
                {task.assignee.name[0]}
              </Avatar>
            </Tooltip>
          )}
          {task.dueDate && (
            <span className={styles.dueDate}>
              {new Date(task.dueDate).toLocaleDateString()}
            </span>
          )}
        </div>
      </Card>
    </div>
  );
};

export default TaskCard; 