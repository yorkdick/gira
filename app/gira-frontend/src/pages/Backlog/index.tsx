import React, { useEffect, useState } from 'react';
import { Layout, Modal } from 'antd';
import { useDispatch, useSelector } from 'react-redux';
import { AppDispatch } from '@/store';
import { fetchSprints, selectAllSprints } from '@/store/slices/sprintSlice';
import { fetchTasks } from '@/store/slices/taskSlice';
import { Sprint } from '@/types/sprint';
import { Task } from '@/types/task';
import SprintList from '@/components/Sprint/SprintList';
import SprintForm from '@/components/Sprint/SprintForm';
import TaskPool from '@/components/Backlog/TaskPool';
import styles from './style.module.less';

const { Content } = Layout;

const Backlog: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const sprints = useSelector(selectAllSprints);
  const [isSprintModalVisible, setIsSprintModalVisible] = useState(false);
  const [selectedSprint, setSelectedSprint] = useState<Sprint | undefined>(undefined);

  useEffect(() => {
    dispatch(fetchSprints({}));
    dispatch(fetchTasks());
  }, [dispatch]);

  const handleCreateSprint = () => {
    setSelectedSprint(undefined);
    setIsSprintModalVisible(true);
  };

  const handleEditSprint = (sprint: Sprint) => {
    setSelectedSprint(sprint);
    setIsSprintModalVisible(true);
  };

  const handleSprintModalClose = () => {
    setIsSprintModalVisible(false);
    setSelectedSprint(undefined);
  };

  const handleSprintFormSuccess = () => {
    setIsSprintModalVisible(false);
    setSelectedSprint(undefined);
    dispatch(fetchSprints({}));
  };

  const handleCreateTask = () => {
    // TODO: Implement task creation
  };

  const handleEditTask = (task: Task) => {
    // TODO: Implement task editing
  };

  return (
    <Layout className={styles.layout}>
      <Content className={styles.content}>
        <SprintList
          sprints={sprints}
          onCreateSprint={handleCreateSprint}
          onEdit={handleEditSprint}
        />
        <TaskPool
          onCreateTask={handleCreateTask}
          onEditTask={handleEditTask}
        />
      </Content>

      <Modal
        title={selectedSprint ? '编辑Sprint' : '创建Sprint'}
        open={isSprintModalVisible}
        onCancel={handleSprintModalClose}
        footer={null}
        destroyOnClose
      >
        <SprintForm
          sprint={selectedSprint}
          onSuccess={handleSprintFormSuccess}
        />
      </Modal>
    </Layout>
  );
};

export default Backlog; 