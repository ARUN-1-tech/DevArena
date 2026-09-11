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
  (error: AxiosError<unknown>) => {
    const data = error.response?.data;
    if (data && typeof data === 'object' && 'message' in data) {
      return Promise.reject(data as ApiErrorResponse);
    }
    const status = error.response?.status || 500;
    const isConnRefused =
      status === 502 ||
      status === 503 ||
      status === 504 ||
      (status === 500 && (typeof data === 'string' || !data));

    return Promise.reject({
      timestamp: new Date().toISOString(),
      status,
      error: isConnRefused ? 'SERVICE_UNAVAILABLE' : 'NETWORK_ERROR',
      message: isConnRefused
        ? 'Cannot connect to the DevArena backend. Please ensure the backend server is running on port 8080.'
        : error.message || 'An unexpected network error occurred.',
      path: error.config?.url || '',
    } as ApiErrorResponse);
  }
);
