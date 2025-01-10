import React from 'react';
import { List, Typography } from 'antd';
import { ProjectOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import styles from './style.module.less';

const { Title } = Typography;

// TODO: 替换为实际的项目数据类型和API调用
const mockProjects = [
  { id: 1, name: 'GIRA项目管理系统' },
  { id: 2, name: '电商平台开发' },
  { id: 3, name: '移动端App开发' },
];

const ProjectList: React.FC = () => {
  const navigate = useNavigate();

  const handleProjectClick = (projectId: number) => {
    navigate(`/projects/${projectId}`);
  };

  return (
    <div className={styles.container}>
      <Title level={5} className={styles.title}>
        <ProjectOutlined /> 我的项目
      </Title>
      <List
        size="small"
        dataSource={mockProjects}
        renderItem={(project) => (
          <List.Item
            className={styles.projectItem}
            onClick={() => handleProjectClick(project.id)}
          >
            <span className={styles.projectName}>{project.name}</span>
          </List.Item>
        )}
      />
    </div>
  );
};

export default ProjectList; 