import React from 'react';
import { Form, Input, Select, Button, message } from 'antd';
import PrioritySelect from '@/components/PrioritySelect';
import { TaskStatus } from '@/types/task';
import styles from './style.module.less';

interface TaskFormProps {
  onSuccess: () => void;
}

const TaskForm: React.FC<TaskFormProps> = ({ onSuccess }) => {
  const [form] = Form.useForm();

  const handleSubmit = async (values: any) => {
    try {
      // TODO: 实现创建任务的API调用
      message.success('任务创建成功');
      form.resetFields();
      onSuccess();
    } catch (error) {
      message.error('创建任务失败');
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
          <Select.Option value={TaskStatus.IN_REVIEW}>审核中</Select.Option>
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