import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { message } from 'antd';
import { Task, TaskQueryParams, CreateTaskParams, UpdateTaskParams } from '@/types/task';
import * as taskService from '@/services/task';
import {
  NormalizedEntity,
  normalizeEntities,
  addEntity,
  updateEntity,
  removeEntity,
  createEntitySelectors,
} from '@/utils/normalize';
import { RootState } from '@/store';

interface TaskState {
  entities: NormalizedEntity<Task>;
  currentTaskId: number | null;
  loading: boolean;
  error: string | null;
  total: number;
  page: number;
  pageSize: number;
  // 请求缓存
  cache: {
    [key: string]: {
      data: number[]; // 存储任务ID
      timestamp: number;
    };
  };
  tasks: Task[];
}

const initialState: TaskState = {
  entities: {
    byId: {},
    allIds: [],
  },
  currentTaskId: null,
  loading: false,
  error: null,
  total: 0,
  page: 1,
  pageSize: 10,
  cache: {},
  tasks: [],
};

// 缓存时间：5分钟
const CACHE_DURATION = 5 * 60 * 1000;

// 生成缓存key
const generateCacheKey = (params?: TaskQueryParams): string => {
  if (!params) return 'all';
  return JSON.stringify(params);
};

// 检查缓存是否有效
const isCacheValid = (timestamp: number): boolean => {
  return Date.now() - timestamp < CACHE_DURATION;
};

export const fetchTasks = createAsyncThunk<
  { items: Task[]; total: number },
  TaskQueryParams | undefined,
  { state: RootState }
>('task/fetchTasks', async (params, { getState }) => {
  const state = getState();
  const cacheKey = generateCacheKey(params);
  const cache = state.task.cache[cacheKey];

  // 如果缓存有效，直接返回缓存数据
  if (cache && isCacheValid(cache.timestamp)) {
    return {
      items: cache.data.map((id) => state.task.entities.byId[id]),
      total: cache.data.length,
    };
  }

  const response = await taskService.getTasks(params);
  return response;
});

export const fetchTask = createAsyncThunk(
  'task/fetchTask',
  async (id: number) => {
    const response = await taskService.getTask(id);
    return response;
  }
);

export const createTask = createAsyncThunk(
  'task/createTask',
  async (params: CreateTaskParams) => {
    const response = await taskService.createTask(params);
    message.success('任务创建成功');
    return response;
  }
);

export const updateTask = createAsyncThunk(
  'task/updateTask',
  async ({ id, params }: { id: number; params: UpdateTaskParams }) => {
    const response = await taskService.updateTask(id, params);
    message.success('任务更新成功');
    return response;
  }
);

export const deleteTask = createAsyncThunk(
  'task/deleteTask',
  async (id: number) => {
    await taskService.deleteTask(id);
    message.success('任务删除成功');
    return id;
  }
);

export const assignTask = createAsyncThunk(
  'task/assignTask',
  async ({ taskId, userId }: { taskId: number; userId: number }) => {
    const response = await taskService.assignTask(taskId, userId);
    message.success('任务分配成功');
    return response;
  }
);

export const unassignTask = createAsyncThunk(
  'task/unassignTask',
  async (taskId: number) => {
    const response = await taskService.unassignTask(taskId);
    message.success('取消分配成功');
    return response;
  }
);

const taskSlice = createSlice({
  name: 'task',
  initialState,
  reducers: {
    clearTasks: (state) => {
      state.entities = {
        byId: {},
        allIds: [],
      };
      state.total = 0;
      state.cache = {};
    },
    clearCurrentTask: (state) => {
      state.currentTaskId = null;
    },
    clearCache: (state) => {
      state.cache = {};
    },
  },
  extraReducers: (builder) => {
    builder
      // fetchTasks
      .addCase(fetchTasks.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchTasks.fulfilled, (state, action) => {
        state.loading = false;
        const normalized = normalizeEntities(action.payload.items);
        state.entities = normalized;
        state.total = action.payload.total;

        // 更新缓存
        const cacheKey = generateCacheKey(action.meta.arg);
        state.cache[cacheKey] = {
          data: normalized.allIds,
          timestamp: Date.now(),
        };
      })
      .addCase(fetchTasks.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || '获取任务列表失败';
        message.error(state.error);
      })
      // fetchTask
      .addCase(fetchTask.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchTask.fulfilled, (state, action) => {
        state.loading = false;
        state.entities = addEntity(state.entities, action.payload);
        state.currentTaskId = action.payload.id;
      })
      .addCase(fetchTask.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || '获取任务详情失败';
        message.error(state.error);
      })
      // createTask
      .addCase(createTask.fulfilled, (state, action) => {
        state.entities = addEntity(state.entities, action.payload);
        state.total += 1;
        state.cache = {}; // 清除缓存
      })
      // updateTask
      .addCase(updateTask.fulfilled, (state, action) => {
        state.entities = updateEntity(state.entities, action.payload);
        state.cache = {}; // 清除缓存
      })
      // deleteTask
      .addCase(deleteTask.fulfilled, (state, action) => {
        state.entities = removeEntity(state.entities, action.payload);
        state.total -= 1;
        if (state.currentTaskId === action.payload) {
          state.currentTaskId = null;
        }
        state.cache = {}; // 清除缓存
      })
      // assignTask
      .addCase(assignTask.fulfilled, (state, action) => {
        state.entities = updateEntity(state.entities, action.payload);
        state.cache = {}; // 清除缓存
      })
      // unassignTask
      .addCase(unassignTask.fulfilled, (state, action) => {
        state.entities = updateEntity(state.entities, action.payload);
        state.cache = {}; // 清除缓存
      });
  },
});

export const { clearTasks, clearCurrentTask, clearCache } = taskSlice.actions;

// 创建选择器
const selectTaskState = (state: RootState) => state.task.entities;

export const {
  selectIds: selectTaskIds,
  selectById: selectTaskById,
  selectAll: selectAllTasks,
  selectTotal: selectTotalTasks,
} = createEntitySelectors<Task, RootState>(selectTaskState);

// 其他选择器
export const selectCurrentTask = (state: RootState) =>
  state.task.currentTaskId ? state.task.entities.byId[state.task.currentTaskId] : null;
export const selectTaskLoading = (state: RootState) => state.task.loading;
export const selectTaskError = (state: RootState) => state.task.error;
export const selectTaskTotal = (state: RootState) => state.task.total;

export default taskSlice.reducer; 