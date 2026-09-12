import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import { ApiErrorResponse } from '../types/api';

const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api/v1';

export const apiClient = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

// Single-flight token refresh state
let isRefreshing = false;
let failedQueue: Array<{
  resolve: (token: string) => void;
  reject: (err: unknown) => void;
}> = [];

const processQueue = (error: unknown, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else if (token) {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

// Request Interceptor: Attach JWT token if available
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('devarena_token');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

interface CustomAxiosRequestConfig extends InternalAxiosRequestConfig {
  _retry?: boolean;
}

// Response Interceptor: Transparent single-flight token refresh & standardized error handling
apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError<unknown>) => {
    const originalRequest = error.config as CustomAxiosRequestConfig;
    const status = error.response?.status;
    const requestUrl = originalRequest?.url || '';

    // Handle 401 Unauthorized with token refresh if possible
    const isAuthEndpoint =
      requestUrl.includes('/auth/login') ||
      requestUrl.includes('/auth/refresh') ||
      requestUrl.includes('/auth/register');

    if (status === 401 && !originalRequest?._retry && !isAuthEndpoint) {
      const refreshToken = localStorage.getItem('devarena_refresh_token');

      if (refreshToken) {
        if (isRefreshing) {
          // If a refresh is already in progress, enqueue this request
          return new Promise((resolve, reject) => {
            failedQueue.push({ resolve, reject });
          })
            .then((newToken) => {
              if (originalRequest.headers) {
                originalRequest.headers.Authorization = `Bearer ${newToken}`;
              }
              return apiClient(originalRequest);
            })
            .catch((err) => Promise.reject(err));
        }

        originalRequest._retry = true;
        isRefreshing = true;

        try {
          // Direct axios call to bypass apiClient response interceptor
          const res = await axios.post(`${BASE_URL}/auth/refresh`, {
            refreshToken,
          });

          const newAccessToken = res.data?.data?.accessToken;
          const newRefreshToken = res.data?.data?.refreshToken;

          if (newAccessToken) {
            localStorage.setItem('devarena_token', newAccessToken);
            if (newRefreshToken) {
              localStorage.setItem('devarena_refresh_token', newRefreshToken);
            }
            if (originalRequest.headers) {
              originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
            }
            processQueue(null, newAccessToken);
            return apiClient(originalRequest);
          }
        } catch (refreshErr) {
          processQueue(refreshErr, null);
          localStorage.removeItem('devarena_token');
          localStorage.removeItem('devarena_refresh_token');
          localStorage.removeItem('devarena_user');
          return Promise.reject(buildErrorResponse(error, status, 'Session expired. Please log in again.'));
        } finally {
          isRefreshing = false;
        }
      } else {
        localStorage.removeItem('devarena_token');
        localStorage.removeItem('devarena_refresh_token');
        localStorage.removeItem('devarena_user');
      }
    }

    const data = error.response?.data;
    if (data && typeof data === 'object' && 'message' in data) {
      return Promise.reject(data as ApiErrorResponse);
    }

    return Promise.reject(buildErrorResponse(error, status));
  }
);

function buildErrorResponse(error: AxiosError<unknown>, status?: number, customMessage?: string): ApiErrorResponse {
  const isConnRefused =
    status === 502 ||
    status === 503 ||
    status === 504 ||
    (status === 500 && (!error.response?.data || typeof error.response?.data === 'string'));

  return {
    timestamp: new Date().toISOString(),
    status: status || 500,
    error: status === 401 ? 'UNAUTHORIZED' : isConnRefused ? 'SERVICE_UNAVAILABLE' : 'NETWORK_ERROR',
    message: customMessage || (status === 401
      ? 'Your session has expired or is invalid. Please log in again.'
      : isConnRefused
      ? 'Cannot connect to the DevArena backend. Please ensure the backend server is running on port 8080.'
      : error.message || 'An unexpected network error occurred.'),
    path: error.config?.url || '',
  };
}
