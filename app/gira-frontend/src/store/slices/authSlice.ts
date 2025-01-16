import { createSlice, PayloadAction, createAsyncThunk } from '@reduxjs/toolkit';
import { message } from 'antd';
import { AxiosError } from 'axios';
import authService from '@/services/authService';

export interface UserInfo {
  id: string;
  username: string;
  email: string;
  avatar?: string;
  role: 'ADMIN' | 'USER';
  createdAt: string;
  updatedAt: string;
}

interface AuthState {
  token: string | null;
  user: UserInfo | null;
  loading: boolean;
  error: string | null;
}

const initialState: AuthState = {
  token: null,
  user: null,
  loading: false,
  error: null,
};

export const updateProfile = createAsyncThunk(
  'auth/updateProfile',
  async (data: Pick<UserInfo, 'username' | 'email' | 'avatar'>, { rejectWithValue }) => {
    try {
      const response = await authService.updateProfile(data);
      message.success('更新个人信息成功');
      return response.data;
    } catch (error) {
      const err = error as AxiosError;
      message.error('更新个人信息失败');
      return rejectWithValue(err.message);
    }
  }
);

export const updatePassword = createAsyncThunk(
  'auth/updatePassword',
  async (data: { oldPassword: string; newPassword: string }, { rejectWithValue }) => {
    try {
      await authService.updatePassword(data);
      message.success('修改密码成功');
      return true;
    } catch (error) {
      const err = error as AxiosError;
      message.error('修改密码失败');
      return rejectWithValue(err.message);
    }
  }
);

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setToken: (state, action: PayloadAction<string | null>) => {
      state.token = action.payload;
    },
    setUser: (state, action: PayloadAction<UserInfo | null>) => {
      state.user = action.payload;
    },
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.loading = action.payload;
    },
    setError: (state, action: PayloadAction<string | null>) => {
      state.error = action.payload;
    },
    logout: (state) => {
      state.token = null;
      state.user = null;
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(updateProfile.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateProfile.fulfilled, (state, action) => {
        state.loading = false;
        state.user = action.payload;
      })
      .addCase(updateProfile.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      .addCase(updatePassword.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updatePassword.fulfilled, (state) => {
        state.loading = false;
      })
      .addCase(updatePassword.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      });
  },
});

export const { setToken, setUser, setLoading, setError, logout } = authSlice.actions;

export default authSlice.reducer; 