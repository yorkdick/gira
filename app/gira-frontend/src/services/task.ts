import { http } from '@/utils/request';
import {
  Task,
  TaskListResult,
  TaskQueryParams,
  CreateTaskParams,
  UpdateTaskParams,
} from '@/types/task';

const BASE_URL = '/api/tasks';

export const getTasks = async (params?: TaskQueryParams): Promise<TaskListResult> => {
  const response = await http.get(BASE_URL, { params });
  return response.data;
};

export const getTask = async (id: number): Promise<Task> => {
  const response = await http.get(`${BASE_URL}/${id}`);
  return response.data;
};

export const createTask = async (params: CreateTaskParams): Promise<Task> => {
  const response = await http.post(BASE_URL, params);
  return response.data;
};

export const updateTask = async (id: number, params: UpdateTaskParams): Promise<Task> => {
  const response = await http.put(`${BASE_URL}/${id}`, params);
  return response.data;
};

export const deleteTask = async (id: number): Promise<void> => {
  await http.delete(`${BASE_URL}/${id}`);
};

export const assignTask = async (taskId: number, userId: number): Promise<Task> => {
  const response = await http.post(`${BASE_URL}/${taskId}/assign/${userId}`);
  return response.data;
};

export const unassignTask = async (taskId: number): Promise<Task> => {
  const response = await http.post(`${BASE_URL}/${taskId}/unassign`);
  return response.data;
};

export const moveTask = async (taskId: number, sprintId: number): Promise<Task> => {
  const response = await http.post(`${BASE_URL}/${taskId}/move/${sprintId}`);
  return response.data;
}; 