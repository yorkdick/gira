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
import { useDispatch } from 'react-redux';
import { Sprint, SprintStatus } from '@/types/sprint';
import { AppDispatch } from '@/store';
import {
  startSprint,
  completeSprint,
  deleteSprint,
} from '@/store/slices/sprintSlice';
import styles from './style.module.less';

interface SprintListProps {
  sprints: Sprint[];
  onCreateSprint: () => void;
  onEdit: (sprint: Sprint) => void;
}

const statusColorMap: Record<SprintStatus, string> = {
  [SprintStatus.PLANNING]: '#faad14',
  [SprintStatus.ACTIVE]: '#52c41a',
  [SprintStatus.COMPLETED]: '#8c8c8c',
};

const SprintList: React.FC<SprintListProps> = ({ sprints, onCreateSprint, onEdit }) => {
  const dispatch = useDispatch<AppDispatch>();

  const handleStart = (id: number) => {
    dispatch(startSprint(id));
  };

  const handleComplete = (id: number) => {
    dispatch(completeSprint(id));
  };

  const handleDelete = (id: number) => {
    dispatch(deleteSprint(id));
  };

  const renderActions = (sprint: Sprint) => {
    const actions = [];

    if (sprint.status === SprintStatus.PLANNING) {
      actions.push(
        <Tooltip key="start" title="开始Sprint">
          <Button
            type="text"
            icon={<PlayCircleOutlined />}
            onClick={() => handleStart(sprint.id)}
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
            onClick={() => handleComplete(sprint.id)}
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
            onClick={() => handleDelete(sprint.id)}
            danger
          />
        </Tooltip>
      );
    }

    return actions;
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
            <Card
              className={styles.sprintCard}
              title={
                <Space>
                  <span>{sprint.name}</span>
                  <Tag color={statusColorMap[sprint.status]}>{sprint.status}</Tag>
                </Space>
              }
              extra={<Space>{renderActions(sprint)}</Space>}
            >
              <div className={styles.content}>
                <div className={styles.goal}>{sprint.goal}</div>
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
              </div>
            </Card>
          </List.Item>
        )}
      />
    </Card>
  );
};

export default SprintList; 