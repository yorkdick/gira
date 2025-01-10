import { User } from './user';

export enum SprintStatus {
  PLANNING = 'PLANNING',
  ACTIVE = 'ACTIVE',
  COMPLETED = 'COMPLETED',
}

export interface Sprint {
  id: number;
  name: string;
  goal: string;
  startDate: string;
  endDate: string;
  status: SprintStatus;
  taskIds: number[];
  createdBy: User;
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
  goal: string;
  startDate: string;
  endDate: string;
}

export interface UpdateSprintParams {
  name?: string;
  goal?: string;
  startDate?: string;
  endDate?: string;
  status?: SprintStatus;
  taskIds?: number[];
} 