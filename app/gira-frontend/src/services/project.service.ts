import api from './api';

export interface Project {
  id: number;
  name: string;
  description: string;
  key: string;
  avatar: string;
  owner: {
    id: number;
    username: string;
    fullName: string;
    avatar: string;
  };
  members: Array<{
    id: number;
    username: string;
    fullName: string;
    avatar: string;
  }>;
  archived: boolean;
  status: number;
  createdAt: string;
  updatedAt: string;
  createdBy: string;
  updatedBy: string;
}

export interface CreateProjectRequest {
  name: string;
  description: string;
  key: string;
}

export interface UpdateProjectRequest {
  name?: string;
  description?: string;
  key?: string;
  avatar?: string;
}

class ProjectService {
  async getProjects(page = 0, size = 10) {
    const response = await api.get<{ content: Project[]; totalElements: number }>(
      `/projects?page=${page}&size=${size}`
    );
    return response.data;
  }

  async getProjectById(id: number) {
    const response = await api.get<Project>(`/projects/${id}`);
    return response.data;
  }

  async createProject(data: CreateProjectRequest) {
    const response = await api.post<Project>('/projects', data);
    return response.data;
  }

  async updateProject(id: number, data: UpdateProjectRequest) {
    const response = await api.put<Project>(`/projects/${id}`, data);
    return response.data;
  }

  async deleteProject(id: number) {
    await api.delete(`/projects/${id}`);
  }

  async archiveProject(id: number) {
    const response = await api.post<Project>(`/projects/${id}/archive`);
    return response.data;
  }

  async unarchiveProject(id: number) {
    const response = await api.post<Project>(`/projects/${id}/unarchive`);
    return response.data;
  }

  async addMember(projectId: number, userId: number) {
    const response = await api.post<Project>(`/projects/${projectId}/members/${userId}`);
    return response.data;
  }

  async removeMember(projectId: number, userId: number) {
    const response = await api.delete<Project>(`/projects/${projectId}/members/${userId}`);
    return response.data;
  }
}

export default new ProjectService(); 