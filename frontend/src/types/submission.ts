import { ChallengeDifficulty, ChallengeCategory, ChallengeProgressStatus } from './arena';

export type ExecutionLanguage = 'JAVA' | 'PYTHON' | 'JAVASCRIPT';

export type SubmissionStatus =
  | 'QUEUED'
  | 'RUNNING'
  | 'PASSED'
  | 'FAILED'
  | 'TIME_LIMIT'
  | 'MEMORY_LIMIT'
  | 'RUNTIME_ERROR'
  | 'COMPILATION_ERROR'
  | 'SYSTEM_ERROR';

export interface TestCaseResult {
  testCaseId: string;
  orderIndex: number;
  input?: string | null;
  expectedOutput?: string | null;
  actualOutput?: string | null;
  passed: boolean;
  hidden: boolean;
  executionTimeMs: number;
  errorMessage?: string | null;
}

export interface RunCodeRequest {
  challengeId: string;
  language: ExecutionLanguage;
  sourceCode: string;
}

export interface RunCodeResponse {
  status: SubmissionStatus;
  passedTests: number;
  totalTests: number;
  executionTimeMs: number;
  memoryUsedBytes: number;
  stdout: string;
  stderr: string;
  errorMessage?: string | null;
  testResults: TestCaseResult[];
}

export interface SubmitCodeRequest {
  challengeId: string;
  language: ExecutionLanguage;
  sourceCode: string;
}

export interface SubmitCodeResponse {
  submissionId: string;
  status: SubmissionStatus;
  passedTests: number;
  totalTests: number;
  executionTimeMs: number;
  memoryUsedBytes: number;
  errorMessage?: string | null;
  xpEarned: number;
  firstSolve: boolean;
  xpResult?: {
    previousXp: number;
    newXp: number;
    previousLevel: number;
    newLevel: number;
    totalXp: number;
    xpEarned: number;
    xpToNextLevel: number;
    leveledUp: boolean;
  } | null;
  createdAt: string;
  completedAt?: string;
  testResults: TestCaseResult[];
}

export interface SubmissionSummary {
  id: string;
  challengeId: string;
  language: ExecutionLanguage;
  status: SubmissionStatus;
  passedTests: number;
  totalTests: number;
  executionTimeMs: number;
  createdAt: string;
}

export interface SubmissionDetail {
  id: string;
  challengeId: string;
  challengeTitle: string;
  language: ExecutionLanguage;
  sourceCode: string;
  status: SubmissionStatus;
  passedTests: number;
  totalTests: number;
  executionTimeMs: number;
  memoryUsedBytes: number;
  errorMessage?: string | null;
  createdAt: string;
  completedAt?: string;
}

export interface ChallengeTestCaseSummary {
  id: string;
  orderIndex: number;
  input: string;
  expectedOutput: string;
  explanation?: string;
}

export interface ChallengeDetailWithCode {
  id: string;
  title: string;
  slug: string;
  description: string;
  difficulty: ChallengeDifficulty;
  category: ChallengeCategory;
  xpReward: number;
  estimatedMinutes: number;
  tags?: string;
  progressStatus: ChallengeProgressStatus;
  completedAt?: string;
  sampleTestCases: ChallengeTestCaseSummary[];
  starterTemplates: Record<string, string>;
}

export interface ChallengeProgressDetail {
  challengeId: string;
  userId: string;
  status: ChallengeProgressStatus;
  attempts: number;
  bestResult?: string | null;
  lastSubmission?: string | null;
  solvedDate?: string | null;
}
