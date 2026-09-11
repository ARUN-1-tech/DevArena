import axios, { AxiosError } from 'axios';
import { ApiErrorResponse } from '../types/api';

const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api/v1';

export const apiClient = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

// Request Interceptor: Attach JWT token if available (prepared for Module 03)
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('devarena_token');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor: Standardize error unwrapping
apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ApiErrorResponse>) => {
    if (error.response?.data) {
      return Promise.reject(error.response.data);
    }
    return Promise.reject({
      timestamp: new Date().toISOString(),
      status: error.response?.status || 500,
      error: 'NETWORK_ERROR',
      message: error.message || 'Network error occurred. Check backend connectivity.',
      path: error.config?.url || '',
    } as ApiErrorResponse);
  }
);
