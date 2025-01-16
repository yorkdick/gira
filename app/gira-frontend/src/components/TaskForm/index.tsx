import React from 'react';
import { Form, Input, Select, DatePicker, Button } from 'antd';
import { Task } from '@/store/slices/boardSlice';
import styles from './index.module.less';

const { TextArea } = Input;
const { Option } = Select;

export interface TaskFormData {
  title: string;
  description?: string;
  priority: Task['priority'];
  assigneeId?: string;
  dueDate?: Date;
  tags?: string[];
}

export interface TaskFormProps {
  initialValues?: TaskFormData;
  assigneeOptions: { id: string; name: string }[];
  onSubmit: (values: TaskFormData) => void;
  onCancel: () => void;
  loading?: boolean;
}

const TaskForm: React.FC<TaskFormProps> = ({
  initialValues,
  assigneeOptions,
  onSubmit,
  onCancel,
  loading
}) => {
  const [form] = Form.useForm();

  return (
    <Form
      form={form}
      layout="vertical"
      initialValues={initialValues}
      onFinish={onSubmit}
      className={styles.form}
    >
      <Form.Item
        name="title"
        label="标题"
        rules={[{ required: true, message: '请输入任务标题' }]}
      >
        <Input placeholder="请输入任务标题" />
      </Form.Item>

      <Form.Item
        name="description"
        label="描述"
      >
        <TextArea
          placeholder="请输入任务描述"
          autoSize={{ minRows: 3, maxRows: 6 }}
        />
      </Form.Item>

      <Form.Item
        name="priority"
        label="优先级"
        rules={[{ required: true, message: '请选择优先级' }]}
      >
        <Select placeholder="请选择优先级">
          <Option value="HIGH">高</Option>
          <Option value="MEDIUM">中</Option>
          <Option value="LOW">低</Option>
        </Select>
      </Form.Item>

      <Form.Item
        name="assigneeId"
        label="经办人"
      >
        <Select
          placeholder="请选择经办人"
          allowClear
          showSearch
          optionFilterProp="children"
        >
          {assigneeOptions.map(option => (
            <Option key={option.id} value={option.id}>
              {option.name}
            </Option>
          ))}
        </Select>
      </Form.Item>

      <Form.Item
        name="dueDate"
        label="截止日期"
      >
        <DatePicker style={{ width: '100%' }} />
      </Form.Item>

      <Form.Item
        name="tags"
        label="标签"
      >
        <Select
          mode="tags"
          placeholder="请输入标签"
          style={{ width: '100%' }}
        />
      </Form.Item>

      <Form.Item className={styles.actions}>
        <Button onClick={onCancel}>
          取消
        </Button>
        <Button type="primary" htmlType="submit" loading={loading}>
          确定
        </Button>
      </Form.Item>
    </Form>
  );
};

export default TaskForm; 