import React, { lazy, Suspense } from 'react';
import { Drawer, Spin } from 'antd';
import { Task } from '@/types/task';
import styles from './style.module.less';

// 懒加载子组件
const TaskForm = lazy(() => import('../Task/TaskForm'));
const TaskDetail = lazy(() => import('./TaskDetail'));

interface TaskDrawerProps {
  visible: boolean;
  task?: Task;
  onClose: () => void;
  onSuccess: () => void;
}

const TaskDrawer: React.FC<TaskDrawerProps> = ({
  visible,
  task,
  onClose,
  onSuccess,
}) => {
  return (
    <Drawer
      title={task ? '编辑任务' : '创建任务'}
      placement="right"
      width={720}
      onClose={onClose}
      open={visible}
      className={styles.taskDrawer}
    >
      <Suspense fallback={<Spin className={styles.loading} />}>
        {task ? (
          <TaskDetail task={task} />
        ) : (
          <TaskForm onSuccess={onSuccess} />
        )}
      </Suspense>
    </Drawer>
  );
};

export default TaskDrawer; 