import React from 'react';
import { Card, Button } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { useSelector } from 'react-redux';
import { RootState } from '@/store';
import { BoardColumn as BoardColumnType } from '@/types/board';
import { useDragDrop } from '../../../hooks/useDragDrop';
import TaskCard from '../../../components/TaskCard';
import styles from './style.module.less';

interface BoardColumnProps {
  column: BoardColumnType;
}

const BoardColumn: React.FC<BoardColumnProps> = ({ column }) => {
  const { tasks } = useSelector((state: RootState) => state.task);
  const { onDragOver, onDrop } = useDragDrop();

  // 获取当前列的任务
  const columnTasks = tasks.filter((task) => task.columnId === column.id);

  const handleDragOver = (e: React.DragEvent<HTMLDivElement>) => {
    onDragOver(e);
  };

  const handleDrop = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    onDrop(column.id, columnTasks.length);
  };

  return (
    <div className={styles.container}>
      <Card
        title={
          <div className={styles.header}>
            <span className={styles.title}>{column.name}</span>
            <span className={styles.count}>{columnTasks.length}</span>
          </div>
        }
        extra={
          <Button
            type="text"
            icon={<PlusOutlined />}
            className={styles.addButton}
          />
        }
        bordered={false}
        className={styles.card}
      >
        <div
          className={styles.tasks}
          onDragOver={handleDragOver}
          onDrop={handleDrop}
        >
          {columnTasks.map((task, index) => (
            <TaskCard key={task.id} task={task} index={index} />
          ))}
        </div>
      </Card>
    </div>
  );
};

export default BoardColumn; 