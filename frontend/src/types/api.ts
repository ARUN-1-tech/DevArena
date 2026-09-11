/**
 * Standard DevArena API response wrapper.
 */
export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  timestamp: string;
}

/**
 * Standard DevArena API error response structure.
 */
export interface ApiErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  details?: Record<string, string>;
}

/**
 * System health payload interface from /api/v1/health.
 */
export interface SystemHealthData {
  status: string;
  service: string;
  motto: string;
  module: string;
  serverTime: string;
}

/**
 * System status payload interface from /api/v1/status.
 */
export interface SystemStatusData {
  version: string;
  environment: string;
  activeModules: string[];
  futureModules: string[];
}
