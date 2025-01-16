import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Form, Select, Button, message } from 'antd';
import { AppDispatch, RootState } from '@/store';
import { Task } from '@/store/slices/boardSlice';
import { updateTaskInSprint } from '@/store/slices/sprintSlice';
import styles from './index.module.less';

interface TaskAssignFormProps {
  sprintId: string;
  task: Task;
  onSuccess?: () => void;
}

const TaskAssignForm: React.FC<TaskAssignFormProps> = ({
  sprintId,
  task,
  onSuccess,
}) => {
  const dispatch = useDispatch<AppDispatch>();
  const { users } = useSelector((state: RootState) => state.auth);
  const [form] = Form.useForm();

  const handleSubmit = async (values: { assigneeId: string }) => {
    try {
      await dispatch(
        updateTaskInSprint({
          sprintId,
          taskId: task.id,
          updates: {
            assigneeId: values.assigneeId,
          },
        })
      ).unwrap();
      message.success('任务分配成功');
      onSuccess?.();
    } catch (error) {
      message.error('任务分配失败');
    }
  };

  return (
    <Form
      form={form}
      onFinish={handleSubmit}
      initialValues={{ assigneeId: task.assignee?.id }}
      className={styles.form}
    >
      <Form.Item
        name="assigneeId"
        label="经办人"
        rules={[{ required: true, message: '请选择经办人' }]}
      >
        <Select placeholder="请选择经办人">
          {users?.map((user) => (
            <Select.Option key={user.id} value={user.id}>
              {user.name}
            </Select.Option>
          ))}
        </Select>
      </Form.Item>

      <Form.Item>
        <Button type="primary" htmlType="submit">
          确认分配
        </Button>
      </Form.Item>
    </Form>
  );
};

export default TaskAssignForm; 