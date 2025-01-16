import React from 'react';
import { Card, Tag, Avatar, Typography, Space, Button } from 'antd';
import { EditOutlined, DeleteOutlined } from '@ant-design/icons';
import { Task } from '@/store/slices/boardSlice';
import dayjs from 'dayjs';
import styles from './styles.module.less';

const { Text } = Typography;

const PRIORITY_CONFIG = {
  HIGH: { color: '#f5222d', text: '高' },
  MEDIUM: { color: '#faad14', text: '中' },
  LOW: { color: '#52c41a', text: '低' },
} as const;

export interface TaskCardProps {
  task: Task;
  onEdit?: (taskId: string) => void;
  onDelete?: (taskId: string) => void;
  isAdmin?: boolean;
}

const TaskCard: React.FC<TaskCardProps> = ({ task, onEdit, onDelete, isAdmin }) => {
  const { id, title, description, priority, assignee, dueDate, tags } = task;
  const priorityConfig = PRIORITY_CONFIG[priority];

  return (
    <Card className={styles.taskCard} size="small">
      <div className={styles.header}>
        <Text strong className={styles.title}>{title}</Text>
        <Tag color={priorityConfig.color}>{priorityConfig.text}</Tag>
      </div>
      
      {description && (
        <Text type="secondary" className={styles.description}>
          {description}
        </Text>
      )}
      
      <div className={styles.footer}>
        <Space size={4}>
          {tags?.map(tag => (
            <Tag key={tag} color="blue">{tag}</Tag>
          ))}
        </Space>
        
        <div className={styles.info}>
          <Space>
            {assignee && (
              <Avatar 
                size="small" 
                src={assignee.avatar}
                alt={assignee.name}
              >
                {!assignee.avatar && assignee.name.charAt(0)}
              </Avatar>
            )}
            <Text type="secondary" className={styles.date}>
              {dayjs(dueDate).format('MM-DD')}
            </Text>
          </Space>
        </div>
      </div>

      {(onEdit || (isAdmin && onDelete)) && (
        <div className={styles.actions}>
          {onEdit && (
            <Button
              type="text"
              size="small"
              icon={<EditOutlined />}
              onClick={() => onEdit(id)}
            />
          )}
          {isAdmin && onDelete && (
            <Button
              type="text"
              size="small"
              danger
              icon={<DeleteOutlined />}
              onClick={() => onDelete(id)}
            />
          )}
        </div>
      )}
    </Card>
  );
};

export default TaskCard; 