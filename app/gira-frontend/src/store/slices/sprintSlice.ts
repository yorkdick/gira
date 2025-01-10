import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { message } from 'antd';
import {
  Sprint,
  SprintQueryParams,
  CreateSprintParams,
  UpdateSprintParams,
} from '@/types/sprint';
import * as sprintService from '@/services/sprint';
import {
  NormalizedEntity,
  normalizeEntities,
  addEntity,
  updateEntity,
  createEntitySelectors,
} from '@/utils/normalize';
import { RootState } from '@/store';

interface SprintState {
  entities: NormalizedEntity<Sprint>;
  currentSprintId: number | null;
  loading: boolean;
  error: string | null;
  total: number;
  page: number;
  pageSize: number;
  cache: {
    [key: string]: {
      data: number[];
      timestamp: number;
    };
  };
}

const initialState: SprintState = {
  entities: {
    byId: {},
    allIds: [],
  },
  currentSprintId: null,
  loading: false,
  error: null,
  total: 0,
  page: 1,
  pageSize: 10,
  cache: {},
};

// 缓存时间：5分钟
const CACHE_DURATION = 5 * 60 * 1000;

// 生成缓存key
const generateCacheKey = (params?: SprintQueryParams): string => {
  if (!params) return 'all';
  return JSON.stringify(params);
};

// 检查缓存是否有效
const isCacheValid = (timestamp: number): boolean => {
  return Date.now() - timestamp < CACHE_DURATION;
};

export const fetchSprints = createAsyncThunk<
  { items: Sprint[]; total: number; page: number; pageSize: number },
  SprintQueryParams | undefined,
  { state: RootState }
>('sprint/fetchSprints', async (params, { getState }) => {
  const state = getState();
  const cacheKey = generateCacheKey(params);
  const cache = state.sprint.cache[cacheKey];

  // 如果缓存有效，直接返回缓存数据
  if (cache && isCacheValid(cache.timestamp)) {
    return {
      items: cache.data.map((id) => state.sprint.entities.byId[id]),
      total: cache.data.length,
      page: state.sprint.page,
      pageSize: state.sprint.pageSize,
    };
  }

  const response = await sprintService.getSprints(params || {});
  return response;
});

export const fetchSprint = createAsyncThunk(
  'sprint/fetchSprint',
  async (id: number) => {
    const response = await sprintService.getSprint(id);
    return response;
  }
);

export const createSprint = createAsyncThunk(
  'sprint/createSprint',
  async (params: CreateSprintParams) => {
    const response = await sprintService.createSprint(params);
    message.success('Sprint创建成功');
    return response;
  }
);

export const updateSprint = createAsyncThunk(
  'sprint/updateSprint',
  async ({ id, params }: { id: number; params: UpdateSprintParams }) => {
    const response = await sprintService.updateSprint(id, params);
    message.success('Sprint更新成功');
    return response;
  }
);

export const startSprint = createAsyncThunk(
  'sprint/startSprint',
  async (id: number) => {
    const response = await sprintService.startSprint(id);
    return response;
  }
);

export const completeSprint = createAsyncThunk(
  'sprint/completeSprint',
  async (id: number) => {
    const response = await sprintService.completeSprint(id);
    return response;
  }
);

export const deleteSprint = createAsyncThunk(
  'sprint/deleteSprint',
  async (id: number) => {
    await sprintService.deleteSprint(id);
    return id;
  }
);

const sprintSlice = createSlice({
  name: 'sprint',
  initialState,
  reducers: {
    clearSprints: (state) => {
      state.entities = {
        byId: {},
        allIds: [],
      };
      state.total = 0;
      state.cache = {};
    },
    clearCurrentSprint: (state) => {
      state.currentSprintId = null;
    },
    clearCache: (state) => {
      state.cache = {};
    },
  },
  extraReducers: (builder) => {
    builder
      // fetchSprints
      .addCase(fetchSprints.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchSprints.fulfilled, (state, action) => {
        state.loading = false;
        const normalized = normalizeEntities(action.payload.items);
        state.entities = normalized;
        state.total = action.payload.total;
        state.page = action.payload.page;
        state.pageSize = action.payload.pageSize;

        // 更新缓存
        const cacheKey = generateCacheKey(action.meta.arg);
        state.cache[cacheKey] = {
          data: normalized.allIds,
          timestamp: Date.now(),
        };
      })
      .addCase(fetchSprints.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || '获取Sprint列表失败';
        message.error(state.error);
      })
      // fetchSprint
      .addCase(fetchSprint.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchSprint.fulfilled, (state, action) => {
        state.loading = false;
        state.entities = addEntity(state.entities, action.payload);
        state.currentSprintId = action.payload.id;
      })
      .addCase(fetchSprint.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || '获取Sprint详情失败';
        message.error(state.error);
      })
      // createSprint
      .addCase(createSprint.fulfilled, (state, action) => {
        state.entities = addEntity(state.entities, action.payload);
        state.total += 1;
        state.cache = {}; // 清除缓存
      })
      // updateSprint
      .addCase(updateSprint.fulfilled, (state, action) => {
        state.entities = updateEntity(state.entities, action.payload);
        state.cache = {}; // 清除缓存
      })
      // startSprint
      .addCase(startSprint.pending, (state) => {
        state.loading = true;
      })
      .addCase(startSprint.fulfilled, (state, action) => {
        state.loading = false;
        const index = state.entities.allIds.findIndex((id) => id === action.payload.id);
        if (index !== -1) {
          state.entities.byId[action.payload.id] = action.payload;
        }
      })
      .addCase(startSprint.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || null;
      })
      // completeSprint
      .addCase(completeSprint.pending, (state) => {
        state.loading = true;
      })
      .addCase(completeSprint.fulfilled, (state, action) => {
        state.loading = false;
        const index = state.entities.allIds.findIndex((id) => id === action.payload.id);
        if (index !== -1) {
          state.entities.byId[action.payload.id] = action.payload;
        }
      })
      .addCase(completeSprint.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || null;
      })
      // deleteSprint
      .addCase(deleteSprint.pending, (state) => {
        state.loading = true;
      })
      .addCase(deleteSprint.fulfilled, (state, action) => {
        state.loading = false;
        const index = state.entities.allIds.findIndex((id) => id === action.payload);
        if (index !== -1) {
          delete state.entities.byId[action.payload];
          state.entities.allIds.splice(index, 1);
          state.total -= 1;
        }
      })
      .addCase(deleteSprint.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || '删除Sprint失败';
        message.error(state.error);
      });
  },
});

export const { clearSprints, clearCurrentSprint, clearCache } = sprintSlice.actions;

// 创建选择器
const selectSprintState = (state: RootState) => state.sprint.entities;

export const {
  selectIds: selectSprintIds,
  selectById: selectSprintById,
  selectAll: selectAllSprints,
  selectTotal: selectTotalSprints,
} = createEntitySelectors<Sprint, RootState>(selectSprintState);

// 其他选择器
export const selectCurrentSprint = (state: RootState) =>
  state.sprint.currentSprintId ? state.sprint.entities.byId[state.sprint.currentSprintId] : null;
export const selectSprintLoading = (state: RootState) => state.sprint.loading;
export const selectSprintError = (state: RootState) => state.sprint.error;
export const selectSprintTotal = (state: RootState) => state.sprint.total;

export default sprintSlice.reducer; 