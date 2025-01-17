import { createSlice, PayloadAction, createAsyncThunk } from '@reduxjs/toolkit';
import { message } from 'antd';
import { AxiosError } from 'axios';
import authService from '@/services/authService';

// 从localStorage获取初始状态
const getStoredAuthState = () => {
  try {
    const token = localStorage.getItem('token');
    const userStr = localStorage.getItem('user');
    const user = userStr ? JSON.parse(userStr) : null;
    return { token, user };
  } catch (error) {
    console.error('Failed to parse stored auth state:', error);
    return { token: null, user: null };
  }
};

export interface UserInfo {
  id: string;
  username: string;
  fullName: string;
  email: string;
  role: 'ADMIN' | 'USER';
  status: 'ACTIVE' | 'INACTIVE' | 'BLOCKED';
  createdAt: string;
}

export interface AuthState {
  token: string | null;
  user: UserInfo | null;
  loading: boolean;
  error: string | null;
}

const storedState = getStoredAuthState();

const initialState: AuthState = {
  token: storedState.token,
  user: storedState.user,
  loading: false,
  error: null,
};

export const updateProfile = createAsyncThunk(
  'auth/updateProfile',
  async (data: Pick<UserInfo, 'username' | 'email' | 'fullName'>, { getState, rejectWithValue }) => {
    try {
      const state = getState() as { auth: AuthState };
      const userId = state.auth.user?.id;
      if (!userId) {
        throw new Error('User ID not found');
      }
      const response = await authService.updateProfile(data, userId);
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
  async (data: { oldPassword: string; newPassword: string }, { getState, rejectWithValue }) => {
    try {
      const state = getState() as { auth: AuthState };
      const userId = state.auth.user?.id;
      if (!userId) {
        throw new Error('User ID not found');
      }
      await authService.updatePassword(data, userId);
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
      if (action.payload) {
        localStorage.setItem('token', action.payload);
      } else {
        localStorage.removeItem('token');
      }
    },
    setUser: (state, action: PayloadAction<UserInfo | null>) => {
      state.user = action.payload;
      if (action.payload) {
        localStorage.setItem('user', JSON.stringify(action.payload));
      } else {
        localStorage.removeItem('user');
      }
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
      localStorage.removeItem('token');
      localStorage.removeItem('user');
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