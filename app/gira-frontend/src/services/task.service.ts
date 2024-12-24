import api from './api';
import { Task } from './board.service';

export interface CreateTaskRequest {
  title: string;
  description: string;
  columnId: number;
  priority: string;
  type: string;
  assigneeId?: number;
  labelIds?: number[];
}

export interface UpdateTaskRequest {
  title?: string;
  description?: string;
  columnId?: number;
  position?: number;
  priority?: string;
  status?: string;
  type?: string;
  assigneeId?: number;
  labelIds?: number[];
}

class TaskService {
  async getTasks(columnId: number) {
    const response = await api.get<Task[]>(`/columns/${columnId}/tasks`);
    return response.data;
  }

  async getTaskById(id: number) {
    const response = await api.get<Task>(`/tasks/${id}`);
    return response.data;
  }

  async createTask(data: CreateTaskRequest) {
    const response = await api.post<Task>('/tasks', data);
    return response.data;
  }

  async updateTask(id: number, data: UpdateTaskRequest) {
    const response = await api.put<Task>(`/tasks/${id}`, data);
    return response.data;
  }

  async deleteTask(id: number) {
    await api.delete(`/tasks/${id}`);
  }

  async moveTask(id: number, columnId: number, position: number) {
    const response = await api.put<Task>(`/tasks/${id}/move`, { columnId, position });
    return response.data;
  }

  async assignTask(id: number, userId: number) {
    const response = await api.put<Task>(`/tasks/${id}/assign/${userId}`);
    return response.data;
  }

  async unassignTask(id: number) {
    const response = await api.put<Task>(`/tasks/${id}/unassign`);
    return response.data;
  }

  async addLabel(id: number, labelId: number) {
    const response = await api.post<Task>(`/tasks/${id}/labels/${labelId}`);
    return response.data;
  }

  async removeLabel(id: number, labelId: number) {
    const response = await api.delete<Task>(`/tasks/${id}/labels/${labelId}`);
    return response.data;
  }
}

export default new TaskService(); 