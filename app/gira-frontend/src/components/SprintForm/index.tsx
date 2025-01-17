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
import { Form, Input, DatePicker, Button, Space } from 'antd';
import type { RangePickerProps } from 'antd/es/date-picker';
import dayjs from 'dayjs';

export interface SprintFormData {
  name: string;
  startDate: string;
  endDate: string;
}

interface SprintFormProps {
  initialValues?: SprintFormData;
  onSubmit: (values: SprintFormData) => void;
  onCancel: () => void;
}

const SprintForm: React.FC<SprintFormProps> = ({
  initialValues,
  onSubmit,
  onCancel,
}) => {
  const [form] = Form.useForm();

  // 禁用超过4周的日期选择
  const disabledDate: RangePickerProps['disabledDate'] = (current) => {
    const startDate = form.getFieldValue('startDate');
    if (!startDate) {
      return false;
    }
    // 禁用超过开始日期4周的日期
    return current && current.isAfter(dayjs(startDate).add(4, 'week'));
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      onSubmit({
        ...values,
        startDate: values.startDate.format('YYYY-MM-DD'),
        endDate: values.endDate.format('YYYY-MM-DD'),
      });
    } catch (error) {
      console.error('表单验证失败:', error);
    }
  };

  React.useEffect(() => {
    if (initialValues) {
      form.setFieldsValue({
        ...initialValues,
        startDate: dayjs(initialValues.startDate),
        endDate: dayjs(initialValues.endDate),
      });
    }
  }, [form, initialValues]);

  return (
    <Form
      form={form}
      layout="vertical"
      initialValues={{
        name: '',
      }}
    >
      <Form.Item
        name="name"
        label="Sprint名称"
        rules={[{ required: true, message: '请输入Sprint名称' }]}
      >
        <Input placeholder="请输入Sprint名称" />
      </Form.Item>

      <Form.Item
        name="startDate"
        label="开始日期"
        rules={[{ required: true, message: '请选择开始日期' }]}
      >
        <DatePicker style={{ width: '100%' }} />
      </Form.Item>

      <Form.Item
        name="endDate"
        label="结束日期"
        rules={[
          { required: true, message: '请选择结束日期' },
          ({ getFieldValue }) => ({
            validator(_, value) {
              if (!value || !getFieldValue('startDate')) {
                return Promise.resolve();
              }
              if (value.isBefore(getFieldValue('startDate'))) {
                return Promise.reject(new Error('结束日期必须在开始日期之后'));
              }
              return Promise.resolve();
            },
          }),
        ]}
      >
        <DatePicker style={{ width: '100%' }} disabledDate={disabledDate} />
      </Form.Item>

      <Form.Item>
        <Space>
          <Button type="primary" onClick={handleSubmit}>
            提交
          </Button>
          <Button onClick={onCancel}>
            取消
          </Button>
        </Space>
      </Form.Item>
    </Form>
  );
};

export default SprintForm; 