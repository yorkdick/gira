import React from 'react';
import { Card, List, Button, Tag, Space } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { useSelector } from 'react-redux';
import { RootState } from '@/store';
import { Task, TaskPriority } from '@/types/task';
import { useDragTask } from '@/hooks/useDragTask';
import styles from './style.module.less';

interface TaskPoolProps {
  onCreateTask: () => void;
  onEditTask: (task: Task) => void;
}

const priorityColorMap: Record<TaskPriority, string> = {
  [TaskPriority.LOW]: '#52c41a',
  [TaskPriority.MEDIUM]: '#1890ff',
  [TaskPriority.HIGH]: '#faad14',
};

const TaskItem: React.FC<{ task: Task; onEdit: (task: Task) => void }> = ({
  task,
  onEdit,
}) => {
  const { drag, isDragging } = useDragTask(task);

  return (
    <div
      ref={drag}
      style={{ opacity: isDragging ? 0.5 : 1, cursor: 'move' }}
      onClick={() => onEdit(task)}
    >
      <Card size="small" className={styles.taskCard}>
        <Space direction="vertical" style={{ width: '100%' }}>
          <div className={styles.taskTitle}>{task.title}</div>
          <Space>
            <Tag color={priorityColorMap[task.priority]}>{task.priority}</Tag>
            {task.assignee && (
              <Tag>{task.assignee.name}</Tag>
            )}
          </Space>
        </Space>
      </Card>
    </div>
  );
};

const TaskPool: React.FC<TaskPoolProps> = ({ onCreateTask, onEditTask }) => {
  const tasks = useSelector((state: RootState) =>
    Object.values(state.task.entities.byId).filter(task => !task.sprintId)
  );

  return (
    <Card
      title="任务池"
      extra={
        <Button type="primary" icon={<PlusOutlined />} onClick={onCreateTask}>
          创建任务
        </Button>
      }
      className={styles.taskPool}
    >
      <List
        dataSource={tasks}
        renderItem={(task) => (
          <List.Item>
            <TaskItem task={task} onEdit={onEditTask} />
          </List.Item>
        )}
      />
    </Card>
  );
};

export default TaskPool; 