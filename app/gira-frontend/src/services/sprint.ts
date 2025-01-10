import { http } from '@/utils/request';
import {
  Sprint,
  SprintListResult,
  SprintQueryParams,
  CreateSprintParams,
  UpdateSprintParams,
} from '@/types/sprint';

const BASE_URL = '/api/sprints';

export const getSprints = async (params: SprintQueryParams): Promise<SprintListResult> => {
  const response = await http.get(BASE_URL, { params });
  return response.data;
};

export const getSprint = async (id: number): Promise<Sprint> => {
  const response = await http.get(`${BASE_URL}/${id}`);
  return response.data;
};

export const createSprint = async (params: CreateSprintParams): Promise<Sprint> => {
  const response = await http.post(BASE_URL, params);
  return response.data;
};

export const updateSprint = async (id: number, params: UpdateSprintParams): Promise<Sprint> => {
  const response = await http.put(`${BASE_URL}/${id}`, params);
  return response.data;
};

export const deleteSprint = async (id: number): Promise<void> => {
  await http.delete(`${BASE_URL}/${id}`);
};

export const startSprint = async (id: number): Promise<Sprint> => {
  const response = await http.post(`${BASE_URL}/${id}/start`);
  return response.data;
};

export const completeSprint = async (id: number): Promise<Sprint> => {
  const response = await http.post(`${BASE_URL}/${id}/complete`);
  return response.data;
}; 