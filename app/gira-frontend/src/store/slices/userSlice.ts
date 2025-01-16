import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { message } from 'antd';
import { AxiosError } from 'axios';
import userService from '@/services/userService';
import { UserInfo } from './authSlice';

export interface UserCreateDTO {
  username: string;
  email: string;
  password: string;
  role: UserInfo['role'];
}

export interface UserUpdateDTO {
  username?: string;
  email?: string;
  role?: UserInfo['role'];
}

interface UserState {
  list: UserInfo[];
  loading: boolean;
  error: string | null;
}

const initialState: UserState = {
  list: [],
  loading: false,
  error: null,
};

export const fetchUsers = createAsyncThunk(
  'users/fetchUsers',
  async (_, { rejectWithValue }) => {
    try {
      const response = await userService.getUsers();
      return response.data;
    } catch (error) {
      const err = error as AxiosError;
      message.error('获取用户列表失败');
      return rejectWithValue(err.message);
    }
  }
);

export const createUser = createAsyncThunk(
  'users/createUser',
  async (data: UserCreateDTO, { rejectWithValue }) => {
    try {
      const response = await userService.createUser(data);
      message.success('创建用户成功');
      return response.data;
    } catch (error) {
      const err = error as AxiosError;
      message.error('创建用户失败');
      return rejectWithValue(err.message);
    }
  }
);

export const updateUser = createAsyncThunk(
  'users/updateUser',
  async ({ id, data }: { id: string; data: UserUpdateDTO }, { rejectWithValue }) => {
    try {
      const response = await userService.updateUser(id, data);
      message.success('更新用户成功');
      return response.data;
    } catch (error) {
      const err = error as AxiosError;
      message.error('更新用户失败');
      return rejectWithValue(err.message);
    }
  }
);

export const deleteUser = createAsyncThunk(
  'users/deleteUser',
  async (id: string, { rejectWithValue }) => {
    try {
      await userService.deleteUser(id);
      message.success('删除用户成功');
      return id;
    } catch (error) {
      const err = error as AxiosError;
      message.error('删除用户失败');
      return rejectWithValue(err.message);
    }
  }
);

const userSlice = createSlice({
  name: 'users',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      // fetchUsers
      .addCase(fetchUsers.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchUsers.fulfilled, (state, action) => {
        state.loading = false;
        state.list = action.payload;
      })
      .addCase(fetchUsers.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      // createUser
      .addCase(createUser.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createUser.fulfilled, (state, action) => {
        state.loading = false;
        state.list.push(action.payload);
      })
      .addCase(createUser.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      // updateUser
      .addCase(updateUser.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateUser.fulfilled, (state, action) => {
        state.loading = false;
        const index = state.list.findIndex(user => user.id === action.payload.id);
        if (index !== -1) {
          state.list[index] = action.payload;
        }
      })
      .addCase(updateUser.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      // deleteUser
      .addCase(deleteUser.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteUser.fulfilled, (state, action) => {
        state.loading = false;
        state.list = state.list.filter(user => user.id !== action.payload);
      })
      .addCase(deleteUser.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      });
  },
});

export default userSlice.reducer; 