import axios, { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios';
import { message } from 'antd';
import { store } from '../store';
import { logout } from '@/store/slices/authSlice';

// 创建axios实例
const request: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
});

// 请求拦截器
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const state = store.getState();
    const token = state.auth?.token;
    
    if (token) {
      // 确保token格式正确
      config.headers.Authorization = `Bearer ${token.replace(/^Bearer\s+/, '')}`;
    }

    console.log('Request config:', {
      url: config.url,
      method: config.method,
      headers: config.headers,
      data: config.data
    });
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse) => {
    return response;
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response;
      
      console.log('Response error:', {
        status,
        data,
        config: error.config
      });

      switch (status) {
        case 401:
          message.error('登录已过期，请重新登录');
          store.dispatch(logout());
          window.location.href = '/login';
          break;
        case 403:
          message.error('没有权限访问');
          break;
        case 404:
          if (!error.config.url.includes('/boards/active')) {
            message.error('请求的资源不存在');
          }
          break;
        case 500:
          message.error('服务器错误');
          break;
        default:
          message.error(data.message || '请求失败');
      }
    } else if (error.request) {
      console.log('Request error:', error.request);
      message.error('网络请求失败，请检查网络连接');
    } else {
      console.log('Error:', error.message);
      message.error('请求发生错误');
    }
    return Promise.reject(error);
  }
);

export default request; 