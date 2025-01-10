import React from 'react';
import { Card, Progress, Typography } from 'antd';
import { Task } from '@/types/task';
import { BoardColumn as IBoardColumn } from '@/types/board';
import { useDropTask } from '@/hooks/useDragDrop';
import TaskCard from '../TaskCard';
import styles from './style.module.less';

const { Title } = Typography;

interface BoardColumnProps {
  column: IBoardColumn;
  tasks: Task[];
  onTaskClick?: (task: Task) => void;
}

const BoardColumn: React.FC<BoardColumnProps> = ({
  column,
  tasks,
  onTaskClick,
}) => {
  const { drop, isOver } = useDropTask(column.id, tasks.length);

  // 计算WIP进度
  const wipProgress = column.settings?.wipLimit
    ? (tasks.length / column.settings.wipLimit) * 100
    : 0;

  return (
    <div
      ref={drop}
      className={`${styles.column} ${isOver ? styles.isOver : ''}`}
    >
      <Card
        title={
          <div className={styles.header}>
            <Title level={5}>{column.name}</Title>
            <span className={styles.count}>{tasks.length}</span>
          </div>
        }
        extra={
          column.settings?.wipLimit && (
            <Progress
              type="circle"
              percent={wipProgress}
              width={20}
              format={() => `${tasks.length}/${column.settings?.wipLimit}`}
              status={wipProgress >= 100 ? 'exception' : 'normal'}
            />
          )
        }
        className={styles.card}
      >
        <div className={styles.taskList}>
          {tasks.map((task, index) => (
            <TaskCard
              key={task.id}
              task={task}
              index={index}
              onClick={onTaskClick}
            />
          ))}
        </div>
      </Card>
    </div>
  );
};

export default BoardColumn; 