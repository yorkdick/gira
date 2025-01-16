/**
 * TaskForm 组件 - 任务创建和编辑表单
 * 
 * 功能：
 * 1. 提供任务的创建和编辑界面
 * 2. 支持设置任务的标题、描述、优先级、经办人、截止日期和标签
 * 3. 提供表单验证和提交功能
 * 4. 支持加载状态显示
 * 5. 支持初始值设置
 * 
 * @component
 * @example
 * ```tsx
 * <TaskForm
 *   initialValues={taskData}
 *   assigneeOptions={users}
 *   onSubmit={handleSubmit}
 *   onCancel={handleCancel}
 *   loading={isSubmitting}
 * />
 * ```
 */

import React from 'react';
import { Form, Input, Select, DatePicker, Button } from 'antd';
import { Task } from '@/store/slices/boardSlice';
import styles from './index.module.less';

const { TextArea } = Input;
const { Option } = Select;

/**
 * 任务表单数据类型
 * @interface
 */
export interface TaskFormData {
  /** 任务标题 */
  title: string;
  /** 任务描述 */
  description?: string;
  /** 任务优先级 */
  priority: Task['priority'];
  /** 经办人ID */
  assigneeId?: string;
  /** 截止日期 */
  dueDate?: Date;
  /** 标签列表 */
  tags?: string[];
}

/**
 * TaskForm组件属性类型
 * @interface
 */
export interface TaskFormProps {
  /** 表单初始值 */
  initialValues?: TaskFormData;
  /** 经办人选项列表 */
  assigneeOptions: { id: string; name: string }[];
  /** 表单提交回调函数 */
  onSubmit: (values: TaskFormData) => void;
  /** 取消操作回调函数 */
  onCancel: () => void;
  /** 加载状态 */
  loading?: boolean;
}

/**
 * TaskForm组件
 * @param props - 组件属性
 * @returns React组件
 */
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
        <Input placeholder="请输入任务标题" maxLength={100} />
      </Form.Item>

      <Form.Item
        name="description"
        label="描述"
      >
        <TextArea
          placeholder="请输入任务描述"
          autoSize={{ minRows: 3, maxRows: 6 }}
          maxLength={500}
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
        <DatePicker
          style={{ width: '100%' }}
          showTime
          placeholder="请选择截止日期"
        />
      </Form.Item>

      <Form.Item
        name="tags"
        label="标签"
      >
        <Select
          mode="tags"
          placeholder="请输入标签"
          style={{ width: '100%' }}
          maxTagCount={5}
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