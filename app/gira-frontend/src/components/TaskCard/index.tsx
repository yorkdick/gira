import React from 'react';
import { Card, Tag, Avatar, Tooltip, Space, Typography } from 'antd';
import { 
  ClockCircleOutlined, 
  DeleteOutlined, 
  EditOutlined,
  PaperClipOutlined
} from '@ant-design/icons';
import { Task, TaskPriority, TaskStatus } from '@/types/task';
import { formatDate } from '../../utils/date';
import styles from './style.module.less';

const { Paragraph } = Typography;

interface TaskCardProps {
  task: Task;
  index: number;
  onEdit?: (task: Task) => void;
  onDelete?: (task: Task) => void;
}

const priorityColorMap: Record<TaskPriority, string> = {
  [TaskPriority.LOW]: '#52c41a',
  [TaskPriority.MEDIUM]: '#faad14',
  [TaskPriority.HIGH]: '#f5222d',
  [TaskPriority.URGENT]: '#ff4d4f',
};

const statusColorMap: Record<TaskStatus, string> = {
  [TaskStatus.TODO]: '#d9d9d9',
  [TaskStatus.IN_PROGRESS]: '#1890ff',
  [TaskStatus.IN_REVIEW]: '#722ed1',
  [TaskStatus.DONE]: '#52c41a',
};

const TaskCard: React.FC<TaskCardProps> = ({ task, onEdit, onDelete }) => {
  const handleEdit = (e: React.MouseEvent) => {
    e.stopPropagation();
    onEdit?.(task);
  };

  const handleDelete = (e: React.MouseEvent) => {
    e.stopPropagation();
    onDelete?.(task);
  };

  return (
    <Card
      className={styles.taskCard}
      size="small"
      actions={[
        <Tooltip key="edit" title="编辑">
          <EditOutlined onClick={handleEdit} />
        </Tooltip>,
        <Tooltip key="delete" title="删除">
          <DeleteOutlined onClick={handleDelete} />
        </Tooltip>,
      ]}
    >
      <div className={styles.header}>
        <Space>
          <Tag color={priorityColorMap[task.priority]}>{task.priority}</Tag>
          <Tag color={statusColorMap[task.status]}>{task.status}</Tag>
        </Space>
      </div>

      <Paragraph className={styles.title} ellipsis={{ tooltip: task.title }}>
        {task.title}
      </Paragraph>

      {task.description && (
        <Paragraph className={styles.description} type="secondary" ellipsis={{ rows: 2 }}>
          {task.description}
        </Paragraph>
      )}

      <div className={styles.footer}>
        <Space className={styles.left}>
          {task.assignee && (
            <Tooltip title={task.assignee.username}>
              <Avatar size="small" src={task.assignee.avatar}>
                {task.assignee.username[0]}
              </Avatar>
            </Tooltip>
          )}
          {task.dueDate && (
            <Tooltip title="截止日期">
              <Space size={4}>
                <ClockCircleOutlined />
                <span className={styles.dueDate}>
                  {formatDate(task.dueDate)}
                </span>
              </Space>
            </Tooltip>
          )}
        </Space>

        <Space className={styles.right}>
          {task.attachments.length > 0 && (
            <Tooltip title={`${task.attachments.length} 个附件`}>
              <Space size={4}>
                <PaperClipOutlined />
                <span>{task.attachments.length}</span>
              </Space>
            </Tooltip>
          )}
          {task.labels.map((label) => (
            <Tag key={label} className={styles.label}>
              {label}
            </Tag>
          ))}
        </Space>
      </div>
    </Card>
  );
};

export default TaskCard; 