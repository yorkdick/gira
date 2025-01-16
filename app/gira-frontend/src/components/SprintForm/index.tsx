/**
 * SprintForm 组件 - Sprint创建和编辑表单
 * 
 * 功能：
 * 1. 提供 Sprint 的创建和编辑界面
 * 2. 支持设置 Sprint 的名称、描述、状态和时间范围
 * 3. 提供表单验证和提交功能
 * 4. 支持加载状态显示
 * 
 * @component
 * @example
 * ```tsx
 * <SprintForm
 *   initialValues={sprintData}
 *   onSubmit={handleSubmit}
 *   onCancel={handleCancel}
 *   loading={isSubmitting}
 * />
 * ```
 */

import React from 'react';
import { Form, Input, DatePicker, Select, Button } from 'antd';
import type { Dayjs } from 'dayjs';
import dayjs from 'dayjs';
import styles from './index.module.less';

const { TextArea } = Input;
const { RangePicker } = DatePicker;

/**
 * Sprint表单数据类型
 * @interface
 */
export interface SprintFormData {
  /** Sprint名称 */
  name: string;
  /** Sprint描述 */
  description: string;
  /** Sprint状态 */
  status: 'PLANNING' | 'ACTIVE' | 'COMPLETED';
  /** 开始时间 */
  startDate: string;
  /** 结束时间 */
  endDate: string;
}

/**
 * 内部表单数据类型
 */
interface InternalFormData {
  name: string;
  description: string;
  status: SprintFormData['status'];
  dateRange: [Dayjs, Dayjs];
}

/**
 * SprintForm组件属性类型
 * @interface
 */
interface SprintFormProps {
  /** 表单初始值 */
  initialValues?: Partial<SprintFormData>;
  /** 表单提交回调函数 */
  onSubmit: (values: SprintFormData) => void;
  /** 取消操作回调函数 */
  onCancel: () => void;
  /** 加载状态 */
  loading?: boolean;
}

/**
 * SprintForm组件
 * @param props - 组件属性
 * @returns React组件
 */
const SprintForm: React.FC<SprintFormProps> = ({
  initialValues,
  onSubmit,
  onCancel,
  loading = false,
}) => {
  const [form] = Form.useForm<InternalFormData>();

  /**
   * 处理表单提交
   * 验证表单数据并调用提交回调函数
   */
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      const [startDate, endDate] = values.dateRange;
      onSubmit({
        name: values.name,
        description: values.description,
        status: values.status,
        startDate: startDate.toISOString(),
        endDate: endDate.toISOString(),
      });
    } catch (error) {
      console.error('Validation failed:', error);
    }
  };

  return (
    <Form
      form={form}
      layout="vertical"
      initialValues={initialValues ? {
        name: initialValues.name,
        description: initialValues.description,
        status: initialValues.status,
        dateRange: [
          dayjs(initialValues.startDate),
          dayjs(initialValues.endDate),
        ],
      } : undefined}
      className={styles.sprintForm}
    >
      <Form.Item
        name="name"
        label="Sprint名称"
        rules={[{ required: true, message: '请输入Sprint名称' }]}
      >
        <Input placeholder="请输入Sprint名称" maxLength={50} />
      </Form.Item>

      <Form.Item
        name="description"
        label="Sprint描述"
        rules={[{ required: true, message: '请输入Sprint描述' }]}
      >
        <TextArea
          placeholder="请输入Sprint描述"
          autoSize={{ minRows: 3, maxRows: 6 }}
          maxLength={500}
        />
      </Form.Item>

      <Form.Item
        name="status"
        label="状态"
        rules={[{ required: true, message: '请选择状态' }]}
      >
        <Select placeholder="请选择状态">
          <Select.Option value="PLANNING">规划中</Select.Option>
          <Select.Option value="ACTIVE">进行中</Select.Option>
          <Select.Option value="COMPLETED">已完成</Select.Option>
        </Select>
      </Form.Item>

      <Form.Item
        name="dateRange"
        label="起止时间"
        rules={[{ required: true, message: '请选择起止时间' }]}
      >
        <RangePicker
          style={{ width: '100%' }}
          placeholder={['开始日期', '结束日期']}
          showTime
        />
      </Form.Item>

      <div className={styles.formActions}>
        <Button onClick={onCancel}>取消</Button>
        <Button type="primary" onClick={handleSubmit} loading={loading}>
          提交
        </Button>
      </div>
    </Form>
  );
};

export default SprintForm; 