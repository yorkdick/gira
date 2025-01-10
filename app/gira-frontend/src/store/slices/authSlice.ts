import { createSlice } from '@reduxjs/toolkit';
import { RootState } from '../index';

interface AuthState {
  token: string | null;
}

const initialState: AuthState = {
  token: null,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setToken: (state, action) => {
      state.token = action.payload;
    },
    clearToken: (state) => {
      state.token = null;
    },
  },
});

export const { setToken, clearToken } = authSlice.actions;

// 选择器
export const selectIsAuthenticated = (state: RootState) => Boolean(state.auth.token);

export default authSlice.reducer; 