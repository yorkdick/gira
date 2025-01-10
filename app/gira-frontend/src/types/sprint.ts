import { Task } from './task';

export enum SprintStatus {
  PLANNING = 'PLANNING',
  ACTIVE = 'ACTIVE',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
}

export interface Sprint {
  id: number;
  name: string;
  description?: string;
  startDate: string;
  endDate: string;
  status: SprintStatus;
  tasks: Task[];
  createdAt: string;
  updatedAt: string;
}

export interface SprintQueryParams {
  keyword?: string;
  status?: SprintStatus;
  startDate?: string;
  endDate?: string;
  page?: number;
  pageSize?: number;
}

export interface SprintListResult {
  items: Sprint[];
  total: number;
  page: number;
  pageSize: number;
}

export interface CreateSprintParams {
  name: string;
  description?: string;
  startDate: string;
  endDate: string;
}

export interface UpdateSprintParams {
  name?: string;
  description?: string;
  startDate?: string;
  endDate?: string;
  status?: SprintStatus;
} 