import axios, { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios';
import { message } from 'antd';

// 创建axios实例
export const http: AxiosInstance = axios.create({
  baseURL: '/api', // 恢复/api前缀
  timeout: 10000,  // 请求超时时间
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
http.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 从localStorage获取token
    const token = localStorage.getItem('token');
    if (token) {
      config.headers = config.headers || {};
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    // 打印请求信息
    console.log('Request:', {
      method: config.method,
      url: config.url,
      baseURL: config.baseURL,
      data: config.data,
      headers: config.headers
    });
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
http.interceptors.response.use(
  (response: AxiosResponse) => {
    // 打印响应信息
    console.log('Response:', {
      status: response.status,
      data: response.data,
      headers: response.headers
    });
    
    return response.data;
  },
  (error) => {
    // 打印错误信息
    console.error('Response Error:', {
      status: error.response?.status,
      data: error.response?.data,
      headers: error.response?.headers,
      config: error.config
    });
    
    if (error.response) {
      const { status } = error.response;
      switch (status) {
        case 400:
          message.error('请求参数错误');
          break;
        case 401:
          message.error('未授权，请重新登录');
          localStorage.removeItem('token');
          window.location.href = '/login';
          break;
        case 403:
          message.error('拒绝访问');
          break;
        case 404:
          message.error('请求的资源不存在');
          break;
        case 500:
          message.error('服务器错误');
          break;
        default:
          message.error('网络错误');
      }
    } else if (error.request) {
      message.error('服务器无响应');
    } else {
      message.error('网络错误，请检查网络连接');
    }
    return Promise.reject(error);
  }
);

export default http; 