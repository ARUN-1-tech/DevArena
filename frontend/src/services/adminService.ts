import { apiClient } from '../lib/api-client';
import { ApiResponse } from '../types/api';
import {
  AdminAuditDto,
  AdminChallengeDto,
  AdminOverviewDto,
  AdminPlayerDto,
  IntegrityEventDto,
  PageResponse,
  ReportDto,
  UpsertChallengeRequest,
} from '../types/admin';

export const adminService = {
  async getOverview(): Promise<AdminOverviewDto> {
    const res = await apiClient.get<ApiResponse<AdminOverviewDto>>('/admin/overview');
    return res.data.data;
  },

  async getPlayers(search?: string, page = 0, size = 20): Promise<PageResponse<AdminPlayerDto>> {
    const params = new URLSearchParams({ page: page.toString(), size: size.toString() });
    if (search) params.append('search', search);
    const res = await apiClient.get<ApiResponse<PageResponse<AdminPlayerDto>>>(`/admin/players?${params}`);
    return res.data.data;
  },

  async suspendPlayer(userId: string, reason: string): Promise<AdminPlayerDto> {
    const res = await apiClient.post<ApiResponse<AdminPlayerDto>>(`/admin/players/${userId}/suspend`, { reason });
    return res.data.data;
  },

  async restorePlayer(userId: string): Promise<AdminPlayerDto> {
    const res = await apiClient.post<ApiResponse<AdminPlayerDto>>(`/admin/players/${userId}/restore`);
    return res.data.data;
  },

  async getChallenges(page = 0, size = 20): Promise<PageResponse<AdminChallengeDto>> {
    const res = await apiClient.get<ApiResponse<PageResponse<AdminChallengeDto>>>(`/admin/challenges?page=${page}&size=${size}`);
    return res.data.data;
  },

  async createChallenge(request: UpsertChallengeRequest): Promise<AdminChallengeDto> {
    const res = await apiClient.post<ApiResponse<AdminChallengeDto>>('/admin/challenges', request);
    return res.data.data;
  },

  async updateChallenge(challengeId: string, request: UpsertChallengeRequest): Promise<AdminChallengeDto> {
    const res = await apiClient.put<ApiResponse<AdminChallengeDto>>(`/admin/challenges/${challengeId}`, request);
    return res.data.data;
  },

  async updateChallengeStatus(challengeId: string, status: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED'): Promise<AdminChallengeDto> {
    const res = await apiClient.patch<ApiResponse<AdminChallengeDto>>(`/admin/challenges/${challengeId}/status`, { status });
    return res.data.data;
  },

  async bulkImportChallenges(challenges: any[]): Promise<{ total: number; inserted: number; skipped: number; errors: string[] }> {
    const res = await apiClient.post<ApiResponse<{ total: number; inserted: number; skipped: number; errors: string[] }>>('/admin/challenges/import', {
      challenges,
    });
    return res.data.data;
  },

  async getReports(status?: string, page = 0, size = 20): Promise<PageResponse<ReportDto>> {
    const params = new URLSearchParams({ page: page.toString(), size: size.toString() });
    if (status) params.append('status', status);
    const res = await apiClient.get<ApiResponse<PageResponse<ReportDto>>>(`/admin/reports?${params}`);
    return res.data.data;
  },

  async resolveReport(reportId: string, status: 'RESOLVED' | 'DISMISSED', resolutionNotes?: string): Promise<ReportDto> {
    const res = await apiClient.post<ApiResponse<ReportDto>>(`/admin/reports/${reportId}/resolve`, { status, resolutionNotes });
    return res.data.data;
  },

  async getIntegrityEvents(page = 0, size = 20): Promise<PageResponse<IntegrityEventDto>> {
    const res = await apiClient.get<ApiResponse<PageResponse<IntegrityEventDto>>>(`/admin/integrity?page=${page}&size=${size}`);
    return res.data.data;
  },

  async reviewIntegrityEvent(eventId: string): Promise<IntegrityEventDto> {
    const res = await apiClient.post<ApiResponse<IntegrityEventDto>>(`/admin/integrity/${eventId}/review`);
    return res.data.data;
  },

  async getAuditLogs(page = 0, size = 20): Promise<PageResponse<AdminAuditDto>> {
    const res = await apiClient.get<ApiResponse<PageResponse<AdminAuditDto>>>(`/admin/audit?page=${page}&size=${size}`);
    return res.data.data;
  },
};
