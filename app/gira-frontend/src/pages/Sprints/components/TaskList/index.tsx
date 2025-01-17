import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Card, Tag, Space, Button, Modal, message, Drawer } from 'antd';
import { DeleteOutlined, UserOutlined } from '@ant-design/icons';
import { DragDropContext, Droppable, Draggable, DropResult } from 'react-beautiful-dnd';
import { AppDispatch, RootState } from '@/store';
import { Task } from '@/store/slices/boardSlice';
import { removeTaskFromSprint, updateTaskInSprint } from '@/store/slices/sprintSlice';
import TaskAssignForm from '../TaskAssignForm';
import styles from './index.module.less';

interface TaskListProps {
  sprintId: string;
  tasks: Task[];
}

const TaskList: React.FC<TaskListProps> = ({ sprintId, tasks }) => {
  const dispatch = useDispatch<AppDispatch>();
  const { user } = useSelector((state: RootState) => state.auth);
  const isAdmin = user?.role === 'ADMIN';

  const [assignDrawerVisible, setAssignDrawerVisible] = useState(false);
  const [selectedTask, setSelectedTask] = useState<Task | null>(null);

  const handleDragEnd = async (result: DropResult) => {
    if (!result.destination) return;

    const { draggableId, destination } = result;
    const newStatus = destination.droppableId as Task['status'];
    const task = tasks.find(t => t.id === draggableId);

    if (task && task.status !== newStatus) {
      try {
        await dispatch(updateTaskInSprint({
          sprintId,
          taskId: draggableId,
          updates: { status: newStatus }
        })).unwrap();
        message.success('更新任务状态成功');
      } catch {
        message.error('更新任务状态失败');
      }
    }
  };

  const handleRemoveTask = (taskId: string) => {
    Modal.confirm({
      title: '确认移除',
      content: '确定要从Sprint中移除这个任务吗？',
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        try {
          await dispatch(removeTaskFromSprint({ sprintId, taskId })).unwrap();
        } catch {
          message.error('移除任务失败');
        }
      },
    });
  };

  const handleAssignTask = (task: Task) => {
    setSelectedTask(task);
    setAssignDrawerVisible(true);
  };

  const getPriorityTag = (priority: Task['priority']) => {
    const config = {
      HIGH: { color: '#f5222d', text: '高' },
      MEDIUM: { color: '#faad14', text: '中' },
      LOW: { color: '#52c41a', text: '低' },
    };
    return <Tag color={config[priority].color}>{config[priority].text}</Tag>;
  };

  const getStatusTag = (status: Task['status']) => {
    const config = {
      TODO: { color: 'default', text: '待处理' },
      IN_PROGRESS: { color: 'processing', text: '进行中' },
      DONE: { color: 'success', text: '已完成' },
    };
    return <Tag color={config[status].color}>{config[status].text}</Tag>;
  };

  const columns = [
    { key: 'TODO' as const, title: '待办' },
    { key: 'IN_PROGRESS' as const, title: '进行中' },
    { key: 'DONE' as const, title: '已完成' },
  ];

  return (
    <>
      <DragDropContext onDragEnd={handleDragEnd}>
        <div className={styles.board}>
          {columns.map(column => (
            <div key={column.key} className={styles.column}>
              <div className={styles.columnHeader}>
                <h3>{column.title}</h3>
                <span className={styles.count}>
                  {tasks.filter(task => task.status === column.key).length}
                </span>
              </div>
              <Droppable droppableId={column.key}>
                {(provided) => (
                  <div
                    ref={provided.innerRef}
                    {...provided.droppableProps}
                    className={styles.tasks}
                  >
                    {tasks
                      .filter(task => task.status === column.key)
                      .map((task, index) => (
                        <Draggable
                          key={task.id}
                          draggableId={task.id}
                          index={index}
                        >
                          {(provided) => (
                            <div
                              ref={provided.innerRef}
                              {...provided.draggableProps}
                              {...provided.dragHandleProps}
                            >
                              <Card
                                className={styles.taskCard}
                                size="small"
                                actions={[
                                  <Button
                                    key="assign"
                                    type="text"
                                    icon={<UserOutlined />}
                                    onClick={() => handleAssignTask(task)}
                                  >
                                    分配
                                  </Button>,
                                  ...(isAdmin
                                    ? [
                                        <Button
                                          key="remove"
                                          type="text"
                                          danger
                                          icon={<DeleteOutlined />}
                                          onClick={() => handleRemoveTask(task.id)}
                                        >
                                          移除
                                        </Button>,
                                      ]
                                    : []),
                                ]}
                              >
                                <div className={styles.taskHeader}>
                                  <Space>
                                    {getPriorityTag(task.priority)}
                                    {getStatusTag(task.status)}
                                  </Space>
                                </div>
                                <div className={styles.taskTitle}>{task.title}</div>
                                {task.description && (
                                  <div className={styles.taskDescription}>
                                    {task.description}
                                  </div>
                                )}
                                <div className={styles.taskFooter}>
                                  <Space size={4}>
                                    {task.assignee && (
                                      <Tag icon={<UserOutlined />}>
                                        {task.assignee.username}
                                      </Tag>
                                    )}
                                  </Space>
                                </div>
                              </Card>
                            </div>
                          )}
                        </Draggable>
                      ))}
                    {provided.placeholder}
                  </div>
                )}
              </Droppable>
            </div>
          ))}
        </div>
      </DragDropContext>

      <Drawer
        title="分配任务"
        placement="right"
        width={400}
        open={assignDrawerVisible}
        onClose={() => {
          setAssignDrawerVisible(false);
          setSelectedTask(null);
        }}
      >
        {selectedTask && (
          <TaskAssignForm
            sprintId={sprintId}
            task={selectedTask}
            onSuccess={() => {
              setAssignDrawerVisible(false);
              setSelectedTask(null);
            }}
          />
        )}
      </Drawer>
    </>
  );
};

export default TaskList; 