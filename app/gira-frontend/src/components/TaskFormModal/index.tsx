import React from 'react';
import { Modal } from 'antd';
import TaskForm, { TaskFormData } from '../TaskForm';
import { Task } from '@/store/slices/boardSlice';

interface TaskFormModalProps {
  visible: boolean;
  title: string;
  initialValues?: TaskFormData;
  assigneeOptions: { id: string; name: string }[];
  onSubmit: (values: TaskFormData) => void;
  onCancel: () => void;
  loading?: boolean;
}

const TaskFormModal: React.FC<TaskFormModalProps> = ({
  visible,
  title,
  initialValues,
  assigneeOptions,
  onSubmit,
  onCancel,
  loading
}) => {
  return (
    <Modal
      title={title}
      open={visible}
      onCancel={onCancel}
      footer={null}
      width={640}
      destroyOnClose
    >
      <TaskForm
        initialValues={initialValues}
        assigneeOptions={assigneeOptions}
        onSubmit={onSubmit}
        onCancel={onCancel}
        loading={loading}
      />
    </Modal>
  );
};

export default TaskFormModal; 