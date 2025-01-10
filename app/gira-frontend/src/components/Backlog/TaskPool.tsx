import React from 'react';
import { List, Card, Button, Space, Tag } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { useSelector } from 'react-redux';
import { RootState } from '@/store';
import { Task, TaskPriority } from '@/types/task';
import styles from './style.module.less';

interface TaskPoolProps {
  onCreateTask: () => void;
  onEditTask: (task: Task) => void;
}

const priorityColorMap: Record<TaskPriority, string> = {
  [TaskPriority.LOW]: '#52c41a',
  [TaskPriority.MEDIUM]: '#faad14',
  [TaskPriority.HIGH]: '#f5222d',
  [TaskPriority.URGENT]: '#ff4d4f',
};

const TaskPool: React.FC<TaskPoolProps> = ({ onCreateTask, onEditTask }) => {
  const tasks = useSelector((state: RootState) => state.task.tasks);

  // 获取未分配到Sprint的任务
  const unassignedTasks = tasks.filter((task: Task) => !task.sprintId);

  return (
    <Card
      title="任务池"
      extra={
        <Button type="primary" icon={<PlusOutlined />} onClick={onCreateTask}>
          创建任务
        </Button>
      }
      className={styles.card}
    >
      <List
        className={styles.list}
        dataSource={unassignedTasks}
        renderItem={(task: Task) => (
          <List.Item
            className={styles.item}
            onClick={() => onEditTask(task)}
          >
            <div className={styles.content}>
              <Space className={styles.header}>
                <Tag color={priorityColorMap[task.priority]}>
                  {task.priority}
                </Tag>
                {task.labels.map((label: string) => (
                  <Tag key={label}>{label}</Tag>
                ))}
              </Space>
              <div className={styles.title}>{task.title}</div>
            </div>
          </List.Item>
        )}
      />
    </Card>
  );
};

export default TaskPool; 