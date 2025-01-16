import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Form, Input, DatePicker, Modal, message } from 'antd';
import { AppDispatch, RootState } from '@/store';
import {
  Sprint,
  SprintCreateDTO,
  SprintUpdateDTO,
  createSprint,
  updateSprint,
} from '@/store/slices/sprintSlice';
import dayjs from 'dayjs';

const { TextArea } = Input;
const { RangePicker } = DatePicker;

interface SprintFormProps {
  visible: boolean;
  initialValues?: Sprint | null;
  onCancel: () => void;
}

const SprintForm: React.FC<SprintFormProps> = ({
  visible,
  initialValues,
  onCancel,
}) => {
  const [form] = Form.useForm();
  const dispatch = useDispatch<AppDispatch>();
  const { loading } = useSelector((state: RootState) => state.sprint);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      const [startDate, endDate] = values.dates;
      const data = {
        name: values.name,
        description: values.description,
        startDate: startDate.format('YYYY-MM-DD'),
        endDate: endDate.format('YYYY-MM-DD'),
      };

      if (initialValues) {
        await dispatch(
          updateSprint({
            id: initialValues.id,
            data: data as SprintUpdateDTO,
          })
        ).unwrap();
      } else {
        await dispatch(createSprint(data as SprintCreateDTO)).unwrap();
      }

      form.resetFields();
      onCancel();
    } catch (error) {
      message.error('提交失败，请检查表单');
    }
  };

  React.useEffect(() => {
    if (visible && initialValues) {
      form.setFieldsValue({
        name: initialValues.name,
        description: initialValues.description,
        dates: [
          dayjs(initialValues.startDate),
          dayjs(initialValues.endDate),
        ],
      });
    } else {
      form.resetFields();
    }
  }, [visible, initialValues, form]);

  return (
    <Modal
      title={initialValues ? '编辑 Sprint' : '创建 Sprint'}
      open={visible}
      onOk={handleSubmit}
      onCancel={onCancel}
      confirmLoading={loading}
      destroyOnClose
    >
      <Form
        form={form}
        layout="vertical"
        initialValues={{
          name: '',
          description: '',
          dates: [],
        }}
      >
        <Form.Item
          name="name"
          label="名称"
          rules={[{ required: true, message: '请输入Sprint名称' }]}
        >
          <Input placeholder="请输入Sprint名称" />
        </Form.Item>

        <Form.Item
          name="description"
          label="描述"
          rules={[{ required: true, message: '请输入Sprint描述' }]}
        >
          <TextArea
            placeholder="请输入Sprint描述"
            autoSize={{ minRows: 3, maxRows: 6 }}
          />
        </Form.Item>

        <Form.Item
          name="dates"
          label="起止日期"
          rules={[{ required: true, message: '请选择Sprint起止日期' }]}
        >
          <RangePicker style={{ width: '100%' }} />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default SprintForm; 