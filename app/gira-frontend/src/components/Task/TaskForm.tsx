import React from 'react';
import { Form, Input, Select, Button, message } from 'antd';
import { useDispatch } from 'react-redux';
import PrioritySelect from '@/components/PrioritySelect';
import { TaskStatus, CreateTaskParams, TaskPriority } from '@/types/task';
import { createTask } from '@/store/slices/taskSlice';
import { AppDispatch } from '@/store';
import styles from './style.module.less';

interface TaskFormProps {
  onSuccess: () => void;
  projectId: number;
  columnId: number;
}

interface TaskFormValues {
  title: string;
  description: string;
  priority: TaskPriority;
  status: TaskStatus;
}

const TaskForm: React.FC<TaskFormProps> = ({ onSuccess, projectId, columnId }) => {
  const dispatch = useDispatch<AppDispatch>();
  const [form] = Form.useForm<TaskFormValues>();

  const handleSubmit = async (values: TaskFormValues) => {
    try {
      const taskParams: CreateTaskParams = {
        ...values,
        projectId,
        columnId,
      };
      await dispatch(createTask(taskParams)).unwrap();
      form.resetFields();
      onSuccess();
    } catch (error: unknown) {
      if (error instanceof Error) {
        message.error(error.message || '创建任务失败');
      } else {
        message.error('创建任务失败');
      }
    }
  };

  return (
    <Form
      form={form}
      layout="vertical"
      onFinish={handleSubmit}
      className={styles.taskForm}
    >
      <Form.Item
        name="title"
        label="任务标题"
        rules={[{ required: true, message: '请输入任务标题' }]}
      >
        <Input placeholder="请输入任务标题" />
      </Form.Item>

      <Form.Item
        name="description"
        label="任务描述"
      >
        <Input.TextArea rows={4} placeholder="请输入任务描述" />
      </Form.Item>

      <Form.Item
        name="priority"
        label="优先级"
        rules={[{ required: true, message: '请选择优先级' }]}
      >
        <PrioritySelect />
      </Form.Item>

      <Form.Item
        name="status"
        label="状态"
        rules={[{ required: true, message: '请选择状态' }]}
      >
        <Select placeholder="请选择状态">
          <Select.Option value={TaskStatus.TODO}>待处理</Select.Option>
          <Select.Option value={TaskStatus.IN_PROGRESS}>进行中</Select.Option>
          <Select.Option value={TaskStatus.DONE}>已完成</Select.Option>
        </Select>
      </Form.Item>

      <Form.Item>
        <Button type="primary" htmlType="submit">
          创建任务
        </Button>
      </Form.Item>
    </Form>
  );
};

export default TaskForm; 