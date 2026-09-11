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
    const status = error.response?.status || 500;
    const data = error.response?.data;

    // Clear stale auth credentials on 401 Unauthorized
    if (status === 401) {
      localStorage.removeItem('devarena_token');
      localStorage.removeItem('devarena_refresh_token');
      localStorage.removeItem('devarena_user');
    }

    if (data && typeof data === 'object' && 'message' in data) {
      return Promise.reject(data as ApiErrorResponse);
    }

    const isConnRefused =
      status === 502 ||
      status === 503 ||
      status === 504 ||
      (status === 500 && (typeof data === 'string' || !data));

    return Promise.reject({
      timestamp: new Date().toISOString(),
      status,
      error: status === 401 ? 'UNAUTHORIZED' : isConnRefused ? 'SERVICE_UNAVAILABLE' : 'NETWORK_ERROR',
      message: status === 401
        ? 'Your session has expired or is invalid. Please log in again.'
        : isConnRefused
        ? 'Cannot connect to the DevArena backend. Please ensure the backend server is running on port 8080.'
        : error.message || 'An unexpected network error occurred.',
      path: error.config?.url || '',
    } as ApiErrorResponse);
  }
);
