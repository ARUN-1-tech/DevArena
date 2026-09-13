import { apiClient } from '../lib/api-client';
import { ApiResponse, SystemHealthData, SystemStatusData } from '../types/api';

/**
 * Service to query backend health and status diagnostics.
 */
export const healthService = {
  async getHealth(): Promise<SystemHealthData> {
    const response = await apiClient.get<ApiResponse<SystemHealthData>>('/health', { timeout: 60000 });
    return response.data.data;
  },

  async getStatus(): Promise<SystemStatusData> {
    const response = await apiClient.get<ApiResponse<SystemStatusData>>('/status', { timeout: 60000 });
    return response.data.data;
  },
};
