import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Card, Col, Row, Spin, Typography, message } from 'antd';
import type { Sprint } from '@/types/sprint';
import SprintProgress from '@/components/Sprint/SprintProgress/index';
import TaskList from '@/components/Task/TaskList';
import useRequest from '@/hooks/useRequest';

const { Title } = Typography;

function SprintDetail(): JSX.Element {
  const { id } = useParams<{ id: string }>();
  const [sprint, setSprint] = useState<Sprint | null>(null);
  const { get } = useRequest();

  useEffect(() => {
    const fetchSprint = async () => {
      if (!id) return;
      
      try {
        const response = await get<Sprint>(`/api/sprints/${id}`);
        setSprint(response);
      } catch {
        message.error('获取Sprint详情失败');
      }
    };

    fetchSprint();
  }, [id, get]);

  if (!sprint) {
    return (
      <div style={{ textAlign: 'center', padding: '50px' }}>
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>{sprint.name}</Title>
      {sprint.description && (
        <Typography.Paragraph style={{ marginBottom: '24px' }}>
          {sprint.description}
        </Typography.Paragraph>
      )}

      <Row gutter={24}>
        <Col span={16}>
          <Card title="任务列表" style={{ marginBottom: '24px' }}>
            <TaskList tasks={sprint.tasks} />
          </Card>
        </Col>
        <Col span={8}>
          <SprintProgress tasks={sprint.tasks} />
        </Col>
      </Row>
    </div>
  );
}

export default SprintDetail; 