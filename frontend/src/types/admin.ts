import { ReportDto, ReportStatus } from './moderation';

export type UserRole = 'ROLE_USER' | 'ROLE_MODERATOR' | 'ROLE_ADMIN';

export interface AdminOverviewDto {
  totalUsers: number;
  activeUsers: number;
  totalChallenges: number;
  totalSubmissions: number;
  totalBattles: number;
  openReports: number;
  highRiskIntegrityAlerts: number;
  totalAiQueriesToday: number;
}

export interface AdminPlayerDto {
  id: string;
  username: string;
  email: string;
  roles: UserRole[];
  enabled: boolean;
  accountNonLocked: boolean;
  xp: number;
  level: number;
  mmr: number;
  riskScore: number;
  createdAt: string;
}

export interface TestCaseItem {
  input: string;
  expectedOutput: string;
  hidden: boolean;
  orderIndex: number;
  explanation?: string;
}

export interface AdminChallengeDto {
  id: string;
  title: string;
  slug: string;
  description: string;
  difficulty: string;
  category: string;
  xpReward: number;
  estimatedMinutes: number;
  tags?: string;
  status: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED';
  testCaseCount: number;
  createdAt: string;
}

export interface UpsertChallengeRequest {
  title: string;
  slug: string;
  description: string;
  difficulty: string;
  category: string;
  xpReward: number;
  estimatedMinutes: number;
  tags?: string;
  status: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED';
  testCases?: TestCaseItem[];
}

export interface SuspendPlayerRequest {
  reason: string;
}

export interface IntegrityEventDto {
  id: string;
  userId: string;
  username: string;
  eventType: string;
  severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  riskScore: number;
  metadata?: string;
  reviewed: boolean;
  createdAt: string;
}

export interface AdminAuditDto {
  id: string;
  actorId?: string;
  actorUsername: string;
  action: string;
  targetType: string;
  targetId: string;
  metadata?: string;
  createdAt: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export type { ReportDto, ReportStatus };
