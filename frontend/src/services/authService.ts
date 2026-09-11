import { apiClient } from '../lib/api-client';
import { ApiResponse } from '../types/api';
import {
  AuthResponseData,
  LoginPayload,
  RegisterPayload,
  PlayerProfile,
  UpdateProfilePayload,
} from '../types/auth';

export const authService = {
  async register(payload: RegisterPayload): Promise<AuthResponseData> {
    const response = await apiClient.post<ApiResponse<AuthResponseData>>('/auth/register', payload);
    return response.data.data;
  },

  async login(payload: LoginPayload): Promise<AuthResponseData> {
    const response = await apiClient.post<ApiResponse<AuthResponseData>>('/auth/login', payload);
    return response.data.data;
  },

  async refreshToken(refreshToken: string): Promise<AuthResponseData> {
    const response = await apiClient.post<ApiResponse<AuthResponseData>>('/auth/refresh', {
      refreshToken,
    });
    return response.data.data;
  },

  async logout(): Promise<void> {
    try {
      await apiClient.post<ApiResponse<void>>('/auth/logout');
    } catch {
      // Ignore network errors on logout
    }
  },

  async getMe(): Promise<PlayerProfile> {
    const response = await apiClient.get<ApiResponse<PlayerProfile>>('/auth/me');
    return response.data.data;
  },

  async updateProfile(payload: UpdateProfilePayload): Promise<PlayerProfile> {
    const response = await apiClient.put<ApiResponse<PlayerProfile>>('/auth/profile', payload);
    return response.data.data;
  },
};
