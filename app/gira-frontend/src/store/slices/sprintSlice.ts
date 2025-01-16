import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { Task } from './boardSlice';
import sprintService from '@/services/sprintService';

export interface Sprint {
  id: string;
  name: string;
  description: string;
  startDate: string;
  endDate: string;
  status: 'PLANNING' | 'ACTIVE' | 'COMPLETED';
  tasks?: Task[];
  createdAt: string;
  updatedAt: string;
}

interface SprintState {
  sprints: Sprint[];
  currentSprint: Sprint | null;
  loading: boolean;
  error: string | null;
  pagination: {
    pageNumber: number;
    pageSize: number;
    totalElements: number;
    totalPages: number;
  };
}

const initialState: SprintState = {
  sprints: [],
  currentSprint: null,
  loading: false,
  error: null,
  pagination: {
    pageNumber: 0,
    pageSize: 10,
    totalElements: 0,
    totalPages: 0,
  },
};

export const fetchSprints = createAsyncThunk(
  'sprint/fetchSprints',
  async () => {
    const response = await sprintService.getSprints();
    return response.data;
  }
);

export const createSprint = createAsyncThunk(
  'sprint/createSprint',
  async (data: Omit<Sprint, 'id' | 'tasks' | 'createdAt' | 'updatedAt'>) => {
    const response = await sprintService.createSprint(data);
    return response.data;
  }
);

export const updateSprint = createAsyncThunk(
  'sprint/updateSprint',
  async ({ id, data }: { id: string; data: Partial<Sprint> }) => {
    const response = await sprintService.updateSprint(id, data);
    return response.data;
  }
);

export const deleteSprint = createAsyncThunk(
  'sprint/deleteSprint',
  async (id: string) => {
    await sprintService.deleteSprint(id);
    return id;
  }
);

export const removeTaskFromSprint = createAsyncThunk(
  'sprint/removeTaskFromSprint',
  async ({ sprintId, taskId }: { sprintId: string; taskId: string }) => {
    await sprintService.removeTask(sprintId, taskId);
    return { sprintId, taskId };
  }
);

export const updateTaskInSprint = createAsyncThunk(
  'sprint/updateTaskInSprint',
  async ({
    sprintId,
    taskId,
    updates,
  }: {
    sprintId: string;
    taskId: string;
    updates: Partial<Task>;
  }) => {
    const response = await sprintService.updateTask(sprintId, taskId, updates);
    return { sprintId, task: response.data };
  }
);

const sprintSlice = createSlice({
  name: 'sprint',
  initialState,
  reducers: {
    resetSprints: (state) => {
      state.sprints = [];
      state.currentSprint = null;
      state.loading = false;
      state.error = null;
      state.pagination = initialState.pagination;
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchSprints.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchSprints.fulfilled, (state, action) => {
        state.loading = false;
        if (action.payload) {
          state.sprints = action.payload.content || [];
          state.pagination = {
            pageNumber: action.payload.pageNumber,
            pageSize: action.payload.pageSize,
            totalElements: action.payload.totalElements,
            totalPages: action.payload.totalPages,
          };
        }
        state.error = null;
      })
      .addCase(fetchSprints.rejected, (state, action) => {
        state.loading = false;
        state.sprints = [];
        state.error = action.error.message || '获取Sprint列表失败';
      })
      .addCase(createSprint.fulfilled, (state, action) => {
        if (action.payload) {
          state.sprints.push(action.payload);
        }
      })
      .addCase(updateSprint.fulfilled, (state, action) => {
        if (action.payload) {
          const index = state.sprints.findIndex((s) => s.id === action.payload.id);
          if (index !== -1) {
            state.sprints[index] = action.payload;
          }
        }
      })
      .addCase(deleteSprint.fulfilled, (state, action) => {
        if (action.payload) {
          state.sprints = state.sprints.filter((s) => s.id !== action.payload);
        }
      })
      .addCase(removeTaskFromSprint.fulfilled, (state, action) => {
        const { sprintId, taskId } = action.payload;
        const sprint = state.sprints.find((s) => s.id === sprintId);
        if (sprint && Array.isArray(sprint.tasks)) {
          sprint.tasks = sprint.tasks.filter((t) => t.id !== taskId);
        }
      })
      .addCase(updateTaskInSprint.fulfilled, (state, action) => {
        const { sprintId, task } = action.payload;
        const sprint = state.sprints.find((s) => s.id === sprintId);
        if (sprint && Array.isArray(sprint.tasks)) {
          const index = sprint.tasks.findIndex((t) => t.id === task.id);
          if (index !== -1) {
            sprint.tasks[index] = task;
          }
        }
      });
  },
});

export const { resetSprints } = sprintSlice.actions;
export default sprintSlice.reducer; 