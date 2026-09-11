import { apiClient } from '../lib/api-client';
import { ApiResponse } from '../types/api';
import { CreateReportRequest, ReportDto } from '../types/moderation';

export const reportService = {
  async createReport(request: CreateReportRequest): Promise<ReportDto> {
    const res = await apiClient.post<ApiResponse<ReportDto>>('/reports', request);
    return res.data.data;
  },
};
