export type ReportTargetType = 'PLAYER' | 'BATTLE' | 'CHALLENGE' | 'BEHAVIOR';

export type ReportReason = 'CHEATING' | 'HARASSMENT' | 'OFFENSIVE_NAME' | 'EXPLOIT' | 'OTHER';

export type ReportStatus = 'OPEN' | 'REVIEWING' | 'RESOLVED' | 'DISMISSED';

export interface CreateReportRequest {
  targetType: ReportTargetType;
  targetId: string;
  reason: ReportReason;
  description?: string;
}

export interface ReportDto {
  id: string;
  reporterId: string;
  reporterUsername: string;
  targetType: ReportTargetType;
  targetId: string;
  reason: ReportReason;
  description?: string;
  status: ReportStatus;
  resolutionNotes?: string;
  createdAt: string;
  resolvedAt?: string;
  resolvedByUsername?: string;
}

export interface ResolveReportRequest {
  status: ReportStatus;
  resolutionNotes?: string;
}
