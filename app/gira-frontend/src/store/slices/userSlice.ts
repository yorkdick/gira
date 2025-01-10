import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { message } from 'antd';
import {
  User,
  UserQueryParams,
  CreateUserParams,
  UpdateUserParams,
} from '@/types/user';
import * as userService from '@/services/user';
import {
  NormalizedEntity,
  normalizeEntities,
  addEntity,
  updateEntity,
  createEntitySelectors,
} from '@/utils/normalize';
import { RootState } from '@/store';

interface UserState {
  entities: NormalizedEntity<User>;
  currentUserId: number | null;
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
  currentUser: User | null;
}

const initialState: UserState = {
  entities: {
    byId: {},
    allIds: [],
  },
  currentUserId: null,
  loading: false,
  error: null,
  total: 0,
  page: 1,
  pageSize: 10,
  cache: {},
  currentUser: null,
};

// 缓存时间：5分钟
const CACHE_DURATION = 5 * 60 * 1000;

// 生成缓存key
const generateCacheKey = (params?: UserQueryParams): string => {
  if (!params) return 'all';
  return JSON.stringify(params);
};

// 检查缓存是否有效
const isCacheValid = (timestamp: number): boolean => {
  return Date.now() - timestamp < CACHE_DURATION;
};

export const fetchUsers = createAsyncThunk<
  { items: User[]; total: number; page: number; pageSize: number },
  UserQueryParams | undefined,
  { state: RootState }
>('user/fetchUsers', async (params, { getState }) => {
  const state = getState();
  const cacheKey = generateCacheKey(params);
  const cache = state.user.cache[cacheKey];

  // 如果缓存有效，直接返回缓存数据
  if (cache && isCacheValid(cache.timestamp)) {
    return {
      items: cache.data.map((id) => state.user.entities.byId[id]),
      total: cache.data.length,
      page: state.user.page,
      pageSize: state.user.pageSize,
    };
  }

  const response = await userService.getUsers(params || {});
  return response;
});

export const fetchUser = createAsyncThunk(
  'user/fetchUser',
  async (id: number) => {
    const response = await userService.getUser(id);
    return response;
  }
);

export const createUser = createAsyncThunk(
  'user/createUser',
  async (params: CreateUserParams) => {
    const response = await userService.createUser(params);
    message.success('用户创建成功');
    return response;
  }
);

export const updateUser = createAsyncThunk(
  'user/updateUser',
  async ({ id, params }: { id: number; params: UpdateUserParams }) => {
    const response = await userService.updateUser(id, params);
    message.success('用户更新成功');
    return response;
  }
);

const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    clearUsers: (state) => {
      state.entities = {
        byId: {},
        allIds: [],
      };
      state.total = 0;
      state.cache = {};
    },
    clearCurrentUser: (state) => {
      state.currentUserId = null;
      state.currentUser = null;
    },
    clearCache: (state) => {
      state.cache = {};
    },
    setCurrentUser: (state, action) => {
      state.currentUser = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder
      // fetchUsers
      .addCase(fetchUsers.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchUsers.fulfilled, (state, action) => {
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
      .addCase(fetchUsers.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || '获取用户列表失败';
        message.error(state.error);
      })
      // fetchUser
      .addCase(fetchUser.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchUser.fulfilled, (state, action) => {
        state.loading = false;
        state.entities = addEntity(state.entities, action.payload);
        state.currentUserId = action.payload.id;
      })
      .addCase(fetchUser.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || '获取用户详情失败';
        message.error(state.error);
      })
      // createUser
      .addCase(createUser.fulfilled, (state, action) => {
        state.entities = addEntity(state.entities, action.payload);
        state.total += 1;
        state.cache = {}; // 清除缓存
      })
      // updateUser
      .addCase(updateUser.fulfilled, (state, action) => {
        state.entities = updateEntity(state.entities, action.payload);
        state.cache = {}; // 清除缓存
      });
  },
});

export const { clearUsers, clearCurrentUser, clearCache, setCurrentUser } = userSlice.actions;

// 创建选择器
const selectUserState = (state: RootState) => state.user.entities;

export const {
  selectIds: selectUserIds,
  selectById: selectUserById,
  selectAll: selectAllUsers,
  selectTotal: selectTotalUsers,
} = createEntitySelectors<User, RootState>(selectUserState);

// 其他选择器
export const selectCurrentUser = (state: RootState) =>
  state.user.currentUserId ? state.user.entities.byId[state.user.currentUserId] : null;
export const selectUserLoading = (state: RootState) => state.user.loading;
export const selectUserError = (state: RootState) => state.user.error;
export const selectUserTotal = (state: RootState) => state.user.total;

export default userSlice.reducer; 