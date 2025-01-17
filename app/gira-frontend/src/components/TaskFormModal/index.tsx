/**
 * TaskFormModal 组件 - 任务表单弹窗
 * 
 * 功能：
 * 1. 提供任务创建和编辑的表单界面
 * 2. 处理表单提交和取消操作
 * 3. 支持初始值加载和表单重置
 * 
 * @component
 */

import React, { useEffect } from 'react';
import { Modal, Form, Input, Select, message } from 'antd';
import { useSelector, useDispatch } from 'react-redux';
import { Task, updateTask, setTasks } from '@/store/slices/boardSlice';
import { RootState, AppDispatch } from '@/store/types';
import boardService from '@/services/boardService';
import { fetchUsers } from '@/store/slices/userSlice';

interface TaskFormData {
  title: string;
  description?: string;
  priority: Task['priority'];
  assigneeId?: string;
}

interface TaskFormModalProps {
  visible: boolean;
  task: Task | null;
  onClose: () => void;
}

const TaskFormModal: React.FC<TaskFormModalProps> = ({
  visible,
  task,
  onClose,
}) => {
  const [form] = Form.useForm<TaskFormData>();
  const dispatch = useDispatch<AppDispatch>();
  const { tasks } = useSelector((state: RootState) => state.board);
  const { list: users } = useSelector((state: RootState) => state.users);

  useEffect(() => {
    if (visible) {
      dispatch(fetchUsers());
    }
  }, [visible, dispatch]);

  // 处理表单提交
  const handleSubmit = async (values: TaskFormData) => {
    try {
      const formData = {
        ...values,
      };

      if (task) {
        // 更新任务
        const response = await boardService.updateTask(task.id, formData);
        dispatch(updateTask(response.data));
        message.success('更新任务成功');
      } else {
        // 创建任务
        const response = await boardService.createTask({
          ...formData,
          status: 'TODO',
        });
        dispatch(setTasks([...tasks, response.data]));
        message.success('创建任务成功');
      }
      form.resetFields();
      onClose();
    } catch {
      message.error(task ? '更新任务失败' : '创建任务失败');
    }
  };

  // 监听弹窗显示状态和任务数据变化
  useEffect(() => {
    if (!visible) {
      form.resetFields();
      return;
    }

    if (task) {
      form.setFieldsValue({
        title: task.title,
        description: task.description,
        priority: task.priority,
        assigneeId: task.assignee?.id,
      });
    } else {
      form.resetFields();
    }
  }, [visible, task, form]);

  return (
    <Modal
      title={task ? '编辑任务' : '创建任务'}
      open={visible}
      onCancel={() => {
        form.resetFields();
        onClose();
      }}
      onOk={() => form.submit()}
      width={640}
      destroyOnClose
    >
      <Form
        form={form}
        layout="vertical"
        onFinish={handleSubmit}
        preserve={false}
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
          <Input.TextArea rows={4} placeholder="请输入任务描述" />
        </Form.Item>

        <Form.Item
          name="priority"
          label="优先级"
          rules={[{ required: true, message: '请选择优先级' }]}
        >
          <Select>
            <Select.Option value="HIGH">高</Select.Option>
            <Select.Option value="MEDIUM">中</Select.Option>
            <Select.Option value="LOW">低</Select.Option>
          </Select>
        </Form.Item>

        <Form.Item
          name="assigneeId"
          label="经办人"
        >
          <Select allowClear placeholder="请选择经办人">
            {users.map(user => (
              <Select.Option key={user.id} value={user.id}>
                {user.username}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default TaskFormModal; 