import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  Button,
  Card,
  List,
  Space,
  Tag,
  Typography,
  Progress,
  Modal,
  message,
  Drawer,
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  ExclamationCircleOutlined,
  UnorderedListOutlined,
} from '@ant-design/icons';
import { AppDispatch, RootState } from '@/store';
import {
  Sprint,
  fetchSprints,
  deleteSprint,
} from '@/store/slices/sprintSlice';
import SprintForm from './components/SprintForm';
import TaskList from './components/TaskList';
import styles from './index.module.less';

const { Title, Text } = Typography;
const { confirm } = Modal;

const SprintsPage: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { sprints, loading } = useSelector((state: RootState) => state.sprint);
  const { currentUser } = useSelector((state: RootState) => state.auth);
  const isAdmin = currentUser?.role === 'ADMIN';

  const [formVisible, setFormVisible] = useState(false);
  const [editingSprint, setEditingSprint] = useState<Sprint | null>(null);
  const [taskDrawerVisible, setTaskDrawerVisible] = useState(false);
  const [selectedSprint, setSelectedSprint] = useState<Sprint | null>(null);

  useEffect(() => {
    dispatch(fetchSprints());
  }, [dispatch]);

  const handleCreateSprint = () => {
    setEditingSprint(null);
    setFormVisible(true);
  };

  const handleEditSprint = (sprint: Sprint) => {
    setEditingSprint(sprint);
    setFormVisible(true);
  };

  const handleDeleteSprint = (sprintId: string) => {
    confirm({
      title: '确认删除',
      icon: <ExclamationCircleOutlined />,
      content: '确定要删除这个Sprint吗？删除后无法恢复。',
      okText: '确定',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await dispatch(deleteSprint(sprintId)).unwrap();
        } catch (error) {
          message.error('删除Sprint失败');
        }
      },
    });
  };

  const handleViewTasks = (sprint: Sprint) => {
    setSelectedSprint(sprint);
    setTaskDrawerVisible(true);
  };

  const getStatusTag = (status: Sprint['status']) => {
    const statusConfig = {
      PLANNING: { color: 'blue', text: '规划中' },
      ACTIVE: { color: 'green', text: '进行中' },
      COMPLETED: { color: 'gray', text: '已完成' },
    };
    const config = statusConfig[status];
    return <Tag color={config.color}>{config.text}</Tag>;
  };

  const getProgress = (sprint: Sprint) => {
    const total = sprint.tasks.length;
    if (total === 0) return 0;
    const completed = sprint.tasks.filter((task) => task.status === 'DONE').length;
    return Math.round((completed / total) * 100);
  };

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <Title level={2}>Sprint 列表</Title>
        {isAdmin && (
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={handleCreateSprint}
          >
            创建 Sprint
          </Button>
        )}
      </div>

      <List
        grid={{ gutter: 16, column: 3 }}
        dataSource={sprints}
        loading={loading}
        renderItem={(sprint) => (
          <List.Item>
            <Card
              className={styles.sprintCard}
              actions={[
                <Button
                  key="tasks"
                  type="text"
                  icon={<UnorderedListOutlined />}
                  onClick={() => handleViewTasks(sprint)}
                >
                  任务列表
                </Button>,
                ...(isAdmin
                  ? [
                      <Button
                        key="edit"
                        type="text"
                        icon={<EditOutlined />}
                        onClick={() => handleEditSprint(sprint)}
                      >
                        编辑
                      </Button>,
                      <Button
                        key="delete"
                        type="text"
                        danger
                        icon={<DeleteOutlined />}
                        onClick={() => handleDeleteSprint(sprint.id)}
                      >
                        删除
                      </Button>,
                    ]
                  : []),
              ]}
            >
              <div className={styles.cardHeader}>
                <Space>
                  <Text strong>{sprint.name}</Text>
                  {getStatusTag(sprint.status)}
                </Space>
              </div>
              <Text type="secondary" className={styles.description}>
                {sprint.description}
              </Text>
              <div className={styles.dates}>
                <Text type="secondary">
                  {sprint.startDate} ~ {sprint.endDate}
                </Text>
              </div>
              <div className={styles.progress}>
                <Progress percent={getProgress(sprint)} size="small" />
                <Text type="secondary">
                  {sprint.tasks.filter((task) => task.status === 'DONE').length} /{' '}
                  {sprint.tasks.length} 任务完成
                </Text>
              </div>
            </Card>
          </List.Item>
        )}
      />

      <SprintForm
        visible={formVisible}
        initialValues={editingSprint}
        onCancel={() => setFormVisible(false)}
      />

      <Drawer
        title={`${selectedSprint?.name || ''} - 任务列表`}
        placement="right"
        width={640}
        open={taskDrawerVisible}
        onClose={() => {
          setTaskDrawerVisible(false);
          setSelectedSprint(null);
        }}
      >
        {selectedSprint && (
          <TaskList
            sprintId={selectedSprint.id}
            tasks={selectedSprint.tasks}
          />
        )}
      </Drawer>
    </div>
  );
};

export default SprintsPage; 