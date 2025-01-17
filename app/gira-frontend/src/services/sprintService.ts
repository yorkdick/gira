import request from '@/utils/request';
import type { Sprint } from '@/store/slices/sprintSlice';
import type { Task } from '@/store/slices/boardSlice';

export interface SprintCreateDTO {
  name: string;
  startDate: string;
  endDate: string;
}

export interface SprintUpdateDTO {
  name: string;
  startDate: string;
  endDate: string;
}

export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  sort: {
    sorted: boolean;
    unsorted: boolean;
    empty: boolean;
  };
}

export interface TaskCreateDTO {
  title: string;
  description: string;
  status: 'TODO' | 'IN_PROGRESS' | 'DONE';
}

const sprintService = {
  // Sprint CRUD 接口
  getSprints: () => request.get<PageResponse<Sprint>>('/sprints'),
  getSprint: (id: string) => request.get<Sprint>(`/sprints/${id}`),
  createSprint: (data: SprintCreateDTO) => request.post<Sprint>('/sprints', data),
  updateSprint: (id: string, data: SprintUpdateDTO) =>
    request.put<Sprint>(`/sprints/${id}`, data),
  deleteSprint: (id: string) => request.delete(`/sprints/${id}`),

  // Sprint 状态管理接口
  startSprint: (id: string) => request.put<Sprint>(`/sprints/${id}/start`),
  completeSprint: (id: string) => request.put<Sprint>(`/sprints/${id}/complete`),
  reopenSprint: (id: string) => request.put<Sprint>(`/sprints/${id}/reopen`),

  // Sprint 任务管理接口
  getTasks: (sprintId: string) => request.get<Task[]>(`/sprints/${sprintId}/tasks`),
  addTask: (sprintId: string, taskId: string) =>
    request.post<Sprint>(`/sprints/${sprintId}/tasks/${taskId}`),
  removeTask: (sprintId: string, taskId: string) =>
    request.delete(`/sprints/${sprintId}/tasks/${taskId}`),
  updateTask: (sprintId: string, taskId: string, data: Partial<Task>) =>
    request.put<Task>(`/sprints/${sprintId}/tasks/${taskId}`, data),
  moveTask: (sprintId: string, taskId: string, targetSprintId: string) =>
    request.post<Sprint>(`/sprints/${sprintId}/tasks/${taskId}/move`, { targetSprintId }),
};

export default sprintService; 