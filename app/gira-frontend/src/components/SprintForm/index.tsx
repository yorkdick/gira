import React from 'react';
import { Form, Input, DatePicker, Select, Button } from 'antd';
import type { Moment } from 'moment';
import styles from './index.module.less';

const { TextArea } = Input;
const { RangePicker } = DatePicker;

export interface SprintFormData {
  name: string;
  description: string;
  status: 'PLANNING' | 'ACTIVE' | 'COMPLETED';
  startDate: Moment;
  endDate: Moment;
}

interface SprintFormProps {
  initialValues?: Partial<SprintFormData>;
  onSubmit: (values: SprintFormData) => void;
  onCancel: () => void;
  loading?: boolean;
}

const SprintForm: React.FC<SprintFormProps> = ({
  initialValues,
  onSubmit,
  onCancel,
  loading = false,
}) => {
  const [form] = Form.useForm<SprintFormData>();

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      onSubmit(values);
    } catch (error) {
      console.error('Validation failed:', error);
    }
  };

  return (
    <Form
      form={form}
      layout="vertical"
      initialValues={initialValues}
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
        name={['startDate', 'endDate']}
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