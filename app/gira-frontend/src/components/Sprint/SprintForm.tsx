import React, { useEffect } from 'react';
import { Form, Input, DatePicker, Button } from 'antd';
import { useDispatch } from 'react-redux';
import dayjs from 'dayjs';
import { Sprint, CreateSprintParams, UpdateSprintParams } from '@/types/sprint';
import { createSprint, updateSprint } from '@/store/slices/sprintSlice';
import { AppDispatch } from '@/store';

interface SprintFormProps {
  sprint?: Sprint;
  onSuccess: () => void;
}

interface SprintFormValues {
  name: string;
  goal: string;
  startDate: dayjs.Dayjs;
  endDate: dayjs.Dayjs;
}

const SprintForm: React.FC<SprintFormProps> = ({ sprint, onSuccess }) => {
  const [form] = Form.useForm<SprintFormValues>();
  const dispatch = useDispatch<AppDispatch>();

  useEffect(() => {
    if (sprint) {
      form.setFieldsValue({
        ...sprint,
        startDate: dayjs(sprint.startDate),
        endDate: dayjs(sprint.endDate),
      });
    }
  }, [sprint, form]);

  const handleSubmit = async (values: SprintFormValues) => {
    const params = {
      ...values,
      startDate: values.startDate.format('YYYY-MM-DD'),
      endDate: values.endDate.format('YYYY-MM-DD'),
    };

    if (sprint) {
      await dispatch(
        updateSprint({
          id: sprint.id,
          params: params as UpdateSprintParams,
        })
      );
    } else {
      await dispatch(createSprint(params as CreateSprintParams));
    }

    onSuccess();
    form.resetFields();
  };

  return (
    <Form
      form={form}
      layout="vertical"
      onFinish={handleSubmit}
      initialValues={{
        startDate: dayjs(),
        endDate: dayjs().add(2, 'week'),
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
        name="goal"
        label="Sprint目标"
        rules={[{ required: true, message: '请输入Sprint目标' }]}
      >
        <Input.TextArea
          placeholder="请输入Sprint目标"
          autoSize={{ minRows: 2, maxRows: 6 }}
        />
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
              if (!value || getFieldValue('startDate').isBefore(value)) {
                return Promise.resolve();
              }
              return Promise.reject(new Error('结束日期必须晚于开始日期'));
            },
          }),
        ]}
      >
        <DatePicker style={{ width: '100%' }} />
      </Form.Item>

      <Form.Item>
        <Button type="primary" htmlType="submit" block>
          {sprint ? '更新' : '创建'} Sprint
        </Button>
      </Form.Item>
    </Form>
  );
};

export default SprintForm; 