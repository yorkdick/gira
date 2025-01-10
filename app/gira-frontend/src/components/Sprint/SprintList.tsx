import React from 'react';
import { List, Card, Button, Tag, Space, Tooltip } from 'antd';
import {
  CalendarOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  DeleteOutlined,
  EditOutlined,
  PlayCircleOutlined,
  PlusOutlined,
} from '@ant-design/icons';
import { useDispatch, useSelector } from 'react-redux';
import { Sprint, SprintStatus } from '@/types/sprint';
import { Task } from '@/types/task';
import { AppDispatch, RootState } from '@/store';
import {
  startSprint,
  completeSprint,
  deleteSprint,
} from '@/store/slices/sprintSlice';
import SprintProgress from './SprintProgress';
import styles from './style.module.less';
import { useDropSprint } from '@/hooks/useDropSprint';

interface SprintListProps {
  sprints: Sprint[];
  onCreateSprint: () => void;
  onEdit: (sprint: Sprint) => void;
}

interface SprintCardProps {
  sprint: Sprint;
  onEdit: (sprint: Sprint) => void;
  onStart: (id: number) => void;
  onComplete: (id: number) => void;
  onDelete: (id: number) => void;
  tasks: Record<number, Task>;
}

const statusColorMap: Record<SprintStatus, string> = {
  [SprintStatus.PLANNING]: '#faad14',
  [SprintStatus.ACTIVE]: '#52c41a',
  [SprintStatus.COMPLETED]: '#8c8c8c',
  [SprintStatus.CANCELLED]: '#ff4d4f',
};

const SprintCard: React.FC<SprintCardProps> = ({
  sprint,
  onEdit,
  onStart,
  onComplete,
  onDelete,
}) => {
  const { drop, isOver } = useDropSprint(sprint);

  const renderActions = () => {
    const actions = [];

    if (sprint.status === SprintStatus.PLANNING) {
      actions.push(
        <Tooltip key="start" title="开始Sprint">
          <Button
            type="text"
            icon={<PlayCircleOutlined />}
            onClick={() => onStart(sprint.id)}
          />
        </Tooltip>
      );
    }

    if (sprint.status === SprintStatus.ACTIVE) {
      actions.push(
        <Tooltip key="complete" title="完成Sprint">
          <Button
            type="text"
            icon={<CheckCircleOutlined />}
            onClick={() => onComplete(sprint.id)}
          />
        </Tooltip>
      );
    }

    actions.push(
      <Tooltip key="edit" title="编辑">
        <Button
          type="text"
          icon={<EditOutlined />}
          onClick={() => onEdit(sprint)}
        />
      </Tooltip>
    );

    if (sprint.status !== SprintStatus.ACTIVE) {
      actions.push(
        <Tooltip key="delete" title="删除">
          <Button
            type="text"
            icon={<DeleteOutlined />}
            onClick={() => onDelete(sprint.id)}
            danger
          />
        </Tooltip>
      );
    }

    return actions;
  };

  const getSprintTasks = () => {
    return sprint.tasks || [];
  };

  return (
    <div
      ref={sprint.status === SprintStatus.PLANNING ? drop : null}
      style={{
        backgroundColor: isOver ? 'rgba(24, 144, 255, 0.1)' : undefined,
        transition: 'background-color 0.3s',
      }}
    >
      <Card
        className={styles.sprintCard}
        title={
          <Space>
            <span>{sprint.name}</span>
            <Tag color={statusColorMap[sprint.status]}>{sprint.status}</Tag>
          </Space>
        }
        extra={<Space>{renderActions()}</Space>}
      >
        <div className={styles.content}>
          <div className={styles.goal}>{sprint.description}</div>
          <Space className={styles.dates}>
            <Space>
              <CalendarOutlined />
              <span>{sprint.startDate}</span>
            </Space>
            <Space>
              <ClockCircleOutlined />
              <span>{sprint.endDate}</span>
            </Space>
          </Space>
          {sprint.status !== SprintStatus.PLANNING && (
            <SprintProgress
              tasks={getSprintTasks()}
            />
          )}
        </div>
      </Card>
    </div>
  );
};

const SprintList: React.FC<SprintListProps> = ({ sprints, onCreateSprint, onEdit }) => {
  const dispatch = useDispatch<AppDispatch>();
  const tasks = useSelector((state: RootState) => state.task.entities.byId);

  const handleStart = (id: number) => {
    dispatch(startSprint(id));
  };

  const handleComplete = (id: number) => {
    dispatch(completeSprint(id));
  };

  const handleDelete = (id: number) => {
    dispatch(deleteSprint(id));
  };

  return (
    <Card
      title="Sprints"
      extra={
        <Button type="primary" icon={<PlusOutlined />} onClick={onCreateSprint}>
          创建Sprint
        </Button>
      }
      className={styles.card}
    >
      <List
        className={styles.list}
        dataSource={sprints}
        renderItem={(sprint) => (
          <List.Item>
            <SprintCard
              sprint={sprint}
              onEdit={onEdit}
              onStart={handleStart}
              onComplete={handleComplete}
              onDelete={handleDelete}
              tasks={tasks}
            />
          </List.Item>
        )}
      />
    </Card>
  );
};

export default SprintList; 