import React, { useEffect, useState } from 'react';
import { Button, Modal, message } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { DragDropContext, Droppable, Draggable, DropResult } from 'react-beautiful-dnd';
import { useDispatch, useSelector } from 'react-redux';
import TaskCard from '@/components/TaskCard';
import TaskFormModal from '@/components/TaskFormModal';
import { AppDispatch, RootState } from '@/store';
import { 
  fetchTasks, 
  updateTaskStatusAsync,
  deleteTaskAsync,
  createTask,
  updateTaskAsync,
  Task
} from '@/store/slices/boardSlice';
import { TaskFormData } from '@/components/TaskForm';
import styles from './index.module.less';

const Board: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { tasks, loading, error } = useSelector((state: RootState) => state.board);
  const { currentUser } = useSelector((state: RootState) => state.auth);
  const { list: users } = useSelector((state: RootState) => state.users);

  const [taskFormVisible, setTaskFormVisible] = useState(false);
  const [editingTask, setEditingTask] = useState<Task | null>(null);

  useEffect(() => {
    dispatch(fetchTasks());
  }, [dispatch]);

  const columns = [
    { key: 'TODO' as const, title: '待办' },
    { key: 'IN_PROGRESS' as const, title: '进行中' },
    { key: 'DONE' as const, title: '已完成' },
  ];

  const handleDragEnd = (result: DropResult) => {
    if (!result.destination) return;

    const { draggableId, destination } = result;
    const newStatus = destination.droppableId as Task['status'];

    dispatch(updateTaskStatusAsync({ 
      taskId: draggableId, 
      status: newStatus 
    }));
  };

  const handleDeleteTask = (taskId: string) => {
    Modal.confirm({
      title: '确认删除',
      content: '确定要删除这个任务吗？',
      okText: '确定',
      cancelText: '取消',
      onOk: () => {
        dispatch(deleteTaskAsync(taskId));
      },
    });
  };

  const handleCreateTask = () => {
    setEditingTask(null);
    setTaskFormVisible(true);
  };

  const handleEditTask = (taskId: string) => {
    const task = tasks.find(t => t.id === taskId);
    if (task) {
      setEditingTask(task);
      setTaskFormVisible(true);
    }
  };

  const handleTaskFormSubmit = async (values: TaskFormData) => {
    try {
      if (editingTask) {
        await dispatch(updateTaskAsync({ 
          id: editingTask.id, 
          data: values 
        })).unwrap();
        message.success('更新任务成功');
      } else {
        await dispatch(createTask(values)).unwrap();
        message.success('创建任务成功');
      }
      setTaskFormVisible(false);
    } catch (error) {
      message.error('操作失败，请重试');
    }
  };

  const handleTaskFormCancel = () => {
    setTaskFormVisible(false);
    setEditingTask(null);
  };

  if (error) {
    message.error(error);
  }

  if (loading && !tasks.length) {
    return <div>加载中...</div>;
  }

  const assigneeOptions = users.map(user => ({
    id: user.id,
    name: user.fullName || user.username,
  }));

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h2>开发任务</h2>
        <Button 
          type="primary" 
          icon={<PlusOutlined />}
          onClick={handleCreateTask}
        >
          创建任务
        </Button>
      </div>

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
                      .map((task: Task, index: number) => (
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
                              <TaskCard
                                task={task}
                                onEdit={() => handleEditTask(task.id)}
                                onDelete={() => handleDeleteTask(task.id)}
                                isAdmin={currentUser?.role === 'ADMIN'}
                              />
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

      <TaskFormModal
        visible={taskFormVisible}
        title={editingTask ? '编辑任务' : '创建任务'}
        initialValues={editingTask ? {
          title: editingTask.title,
          description: editingTask.description,
          priority: editingTask.priority,
          assigneeId: editingTask.assignee?.id,
          dueDate: editingTask.dueDate ? new Date(editingTask.dueDate) : undefined,
          tags: editingTask.tags,
        } : undefined}
        assigneeOptions={assigneeOptions}
        onSubmit={handleTaskFormSubmit}
        onCancel={handleTaskFormCancel}
        loading={loading}
      />
    </div>
  );
};

export default Board; 