import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import { ApiErrorResponse } from '../types/api';

/**
 * Resolves the API base URL from the environment or falls back to local Vite proxy.
 * Supports:
 * - 'https://devarenadevarena-backend.onrender.com' -> 'https://devarenadevarena-backend.onrender.com/api/v1'
 * - 'https://devarenadevarena-backend.onrender.com/api/v1' -> 'https://devarenadevarena-backend.onrender.com/api/v1'
 * - undefined / '' -> '/api/v1' (Vite dev server proxy to localhost:8080)
 */
export const getApiBaseUrl = (): string => {
  const envUrl = import.meta.env.VITE_API_BASE_URL;
  if (!envUrl || envUrl.trim() === '') {
    return '/api/v1';
  }
  const trimmed = envUrl.trim().replace(/\/+$/, '');
  if (trimmed.endsWith('/api/v1')) {
    return trimmed;
  }
  if (trimmed.endsWith('/api')) {
    return `${trimmed}/v1`;
  }
  return `${trimmed}/api/v1`;
};

const BASE_URL = getApiBaseUrl();

export const apiClient = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  // Default timeout 30s to comfortably tolerate cloud cold starts
  timeout: 30000,
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

export interface CustomAxiosRequestConfig extends InternalAxiosRequestConfig {
  _retry?: boolean;
  _retryCount?: number;
  skipRetry?: boolean;
}

// Request Interceptor: Attach JWT token & dynamically configure timeouts
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('devarena_token');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    // Health, status, and diagnostic checks receive up to 60s for Render backend cold start
    const url = config.url || '';
    if (url.includes('/status') || url.includes('/health') || url.includes('/test-validation')) {
      if (!config.timeout || config.timeout === 30000) {
        config.timeout = 60000;
      }
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor: Transparent token refresh, transient error retries & clear error messaging
apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError<unknown>) => {
    const originalRequest = error.config as CustomAxiosRequestConfig;
    if (!originalRequest) {
      return Promise.reject(buildErrorResponse(error));
    }

    const status = error.response?.status;
    const requestUrl = originalRequest.url || '';

    // Handle 401 Unauthorized with token refresh if possible
    const isAuthEndpoint =
      requestUrl.includes('/auth/login') ||
      requestUrl.includes('/auth/refresh') ||
      requestUrl.includes('/auth/register');

    if (status === 401 && !originalRequest._retry && !isAuthEndpoint) {
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

    // Determine if request is eligible for safe retry on Render cold starts
    const isTimeout =
      error.code === 'ECONNABORTED' ||
      (typeof error.message === 'string' && error.message.toLowerCase().includes('timeout'));

    const isTransientError =
      isTimeout ||
      status === 502 ||
      status === 503 ||
      status === 504 ||
      (!error.response && !status);

    // Never retry client errors (4xx) or registration/login POST requests (to prevent duplicate state)
    const isExcludedAuthPost =
      (originalRequest.method?.toUpperCase() === 'POST') &&
      (requestUrl.includes('/auth/register') || requestUrl.includes('/auth/login'));

    const currentRetries = originalRequest._retryCount || 0;
    const canRetry =
      isTransientError &&
      !isExcludedAuthPost &&
      !originalRequest.skipRetry &&
      currentRetries < 2;

    if (canRetry) {
      originalRequest._retryCount = currentRetries + 1;
      // Exponential backoff delay: 1000ms on first retry, 2000ms on second retry
      const backoffDelay = Math.min(1000 * Math.pow(2, currentRetries), 4000);
      await new Promise((resolve) => setTimeout(resolve, backoffDelay));
      return apiClient(originalRequest);
    }

    const data = error.response?.data;
    if (data && typeof data === 'object' && 'message' in data) {
      const errResp = data as ApiErrorResponse;
      if (typeof errResp.message === 'string' && errResp.message.toLowerCase().includes('no static resource')) {
        errResp.message = 'The requested endpoint is temporarily unavailable or cannot be found.';
      }
      return Promise.reject(errResp);
    }

    return Promise.reject(buildErrorResponse(error, status));
  }
);

function buildErrorResponse(error: AxiosError<unknown>, status?: number, customMessage?: string): ApiErrorResponse {
  const isTimeout =
    error.code === 'ECONNABORTED' ||
    (typeof error.message === 'string' && error.message.toLowerCase().includes('timeout'));

  const isColdStartOrDown =
    status === 502 ||
    status === 503 ||
    status === 504 ||
    (!error.response && !status) ||
    (status === 500 && (!error.response?.data || typeof error.response?.data === 'string'));

  let message = customMessage;
  if (!message) {
    if (status === 401) {
      message = 'Your session has expired or is invalid. Please log in again.';
    } else if (isTimeout) {
      message = 'The DevArena server took longer than expected to respond (possibly waking up from a cold start). Please wait a moment and try again.';
    } else if (isColdStartOrDown) {
      message = 'Cannot connect to the DevArena backend. The server may be waking up (Render cold start) or temporarily unreachable. Please retry in a few seconds.';
    } else if (error.message && error.message.toLowerCase().includes('no static resource')) {
      message = 'The requested endpoint is temporarily unavailable or cannot be found.';
    } else {
      message = error.message || 'An unexpected network error occurred.';
    }
  }

  return {
    timestamp: new Date().toISOString(),
    status: status || (isTimeout ? 504 : 500),
    error: status === 401
      ? 'UNAUTHORIZED'
      : isTimeout
      ? 'GATEWAY_TIMEOUT'
      : isColdStartOrDown
      ? 'SERVICE_UNAVAILABLE'
      : 'NETWORK_ERROR',
    message,
    path: error.config?.url || '',
  };
}

