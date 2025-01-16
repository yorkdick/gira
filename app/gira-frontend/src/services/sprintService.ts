import request from '@/utils/request';
import type { Sprint } from '@/store/slices/sprintSlice';
import type { Task } from '@/store/slices/boardSlice';

export interface SprintCreateDTO {
  name: string;
  description: string;
  startDate: string;
  endDate: string;
}

export interface SprintUpdateDTO {
  name?: string;
  description?: string;
  startDate?: string;
  endDate?: string;
  status?: Sprint['status'];
}

const sprintService = {
  // Sprint CRUD 接口
  getSprints: () => request.get<Sprint[]>('/api/sprints'),
  getSprint: (id: string) => request.get<Sprint>(`/api/sprints/${id}`),
  createSprint: (data: SprintCreateDTO) => request.post<Sprint>('/api/sprints', data),
  updateSprint: (id: string, data: SprintUpdateDTO) =>
    request.put<Sprint>(`/api/sprints/${id}`, data),
  deleteSprint: (id: string) => request.delete(`/api/sprints/${id}`),

  // Sprint 状态管理接口
  activateSprint: (id: string) => request.put<Sprint>(`/api/sprints/${id}/activate`),
  completeSprint: (id: string) => request.put<Sprint>(`/api/sprints/${id}/complete`),
  reopenSprint: (id: string) => request.put<Sprint>(`/api/sprints/${id}/reopen`),

  // Sprint 任务管理接口
  getTasks: (sprintId: string) => request.get<Task[]>(`/api/sprints/${sprintId}/tasks`),
  addTask: (sprintId: string, taskId: string) =>
    request.post<Sprint>(`/api/sprints/${sprintId}/tasks/${taskId}`),
  removeTask: (sprintId: string, taskId: string) =>
    request.delete(`/api/sprints/${sprintId}/tasks/${taskId}`),
  updateTask: (sprintId: string, taskId: string, data: Partial<Task>) =>
    request.put<Task>(`/api/sprints/${sprintId}/tasks/${taskId}`, data),
  moveTask: (sprintId: string, taskId: string, targetSprintId: string) =>
    request.post<Sprint>(`/api/sprints/${sprintId}/tasks/${taskId}/move`, { targetSprintId }),
};

export default sprintService; 