import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import Editor from '@monaco-editor/react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Play,
  Send,
  RotateCcw,
  CheckCircle2,
  Terminal,
  HelpCircle,
  Maximize2,
  Minimize2,
  ArrowLeft,
  Sparkles,
  Loader2,
  ChevronDown,
  ChevronUp,
  History,
  Check,
  AlertCircle,
  AlertTriangle,
  Bot,
  Flag,
} from 'lucide-react';
import { submissionService } from '../../services/submissionService';
import {
  ChallengeDetailWithCode,
  ExecutionLanguage,
  RunCodeResponse,
  SubmitCodeResponse,
  SubmissionSummary,
  TestCaseResult,
  SubmissionStatus,
} from '../../types/submission';
import { Button } from '../../components/ui/Button';
import { Badge } from '../../components/ui/Badge';
import { LevelUpModal } from '../../components/player/LevelUpModal';
import { AiCoachPanel } from '../../components/ai/AiCoachPanel';
import { ReportModal } from '../../components/moderation/ReportModal';

export const CodeLabPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [challenge, setChallenge] = useState<ChallengeDetailWithCode | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [language, setLanguage] = useState<ExecutionLanguage>('JAVA');
  const [sourceCode, setSourceCode] = useState<string>('');
  const [showMinimap, setShowMinimap] = useState(false);
  const [pendingLanguage, setPendingLanguage] = useState<ExecutionLanguage | null>(null);
  const [showResetConfirm, setShowResetConfirm] = useState(false);

  const [isRunning, setIsRunning] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [runResult, setRunResult] = useState<RunCodeResponse | null>(null);
  const [submitResult, setSubmitResult] = useState<SubmitCodeResponse | null>(null);
  const [submissions, setSubmissions] = useState<SubmissionSummary[]>([]);
  const [submissionsLoading, setSubmissionsLoading] = useState(false);

  const [activeTab, setActiveTab] = useState<'testcases' | 'console' | 'history'>('testcases');
  const [selectedTestCaseIndex, setSelectedTestCaseIndex] = useState(0);
  const [showHints, setShowHints] = useState(false);
  const [isAiCoachOpen, setIsAiCoachOpen] = useState(false);
  const [isReportModalOpen, setIsReportModalOpen] = useState(false);

  const [levelUpData, setLevelUpData] = useState<{
    show: boolean;
    level: number;
    prevLevel: number;
    xp: number;
  }>({
    show: false,
    level: 1,
    prevLevel: 1,
    xp: 0,
  });

  const [solveBanner, setSolveBanner] = useState<{
    show: boolean;
    xp: number;
    firstSolve: boolean;
  }>({
    show: false,
    xp: 0,
    firstSolve: false,
  });

  useEffect(() => {
    if (!id) return;
    const fetchChallenge = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await submissionService.getChallengeCodeLab(id);
        setChallenge(data);

        const storageKey = `devarena_code_${id}_${language}`;
        const savedCode = localStorage.getItem(storageKey);
        if (savedCode) {
          setSourceCode(savedCode);
        } else if (data.starterTemplates && data.starterTemplates[language]) {
          setSourceCode(data.starterTemplates[language]);
        }
      } catch (err: any) {
        console.error('Failed to load challenge', err);
        setError(err?.message || 'Failed to load challenge details.');
      } finally {
        setLoading(false);
      }
    };
    fetchChallenge();
  }, [id]);

  const handleCodeChange = (value: string | undefined) => {
    const updated = value || '';
    setSourceCode(updated);
    if (id) {
      localStorage.setItem(`devarena_code_${id}_${language}`, updated);
    }
  };

  const handleLanguageSelect = (newLang: ExecutionLanguage) => {
    if (newLang === language) return;
    const currentStarter = challenge?.starterTemplates?.[language]?.trim() || '';
    const isModified = sourceCode.trim() !== currentStarter;

    if (isModified) {
      setPendingLanguage(newLang);
    } else {
      applyLanguage(newLang);
    }
  };

  const applyLanguage = (newLang: ExecutionLanguage) => {
    setLanguage(newLang);
    setPendingLanguage(null);
    if (!id || !challenge) return;

    const storageKey = `devarena_code_${id}_${newLang}`;
    const savedCode = localStorage.getItem(storageKey);
    if (savedCode) {
      setSourceCode(savedCode);
    } else if (challenge.starterTemplates?.[newLang]) {
      setSourceCode(challenge.starterTemplates[newLang]);
    } else {
      setSourceCode('');
    }
  };

  const handleResetCode = () => {
    if (!challenge) return;
    const starter = challenge.starterTemplates?.[language] || '';
    setSourceCode(starter);
    if (id) {
      localStorage.setItem(`devarena_code_${id}_${language}`, starter);
    }
    setShowResetConfirm(false);
  };

  const handleRunCode = async () => {
    if (!id || isRunning || isSubmitting) return;
    try {
      setIsRunning(true);
      setRunResult(null);
      setActiveTab('testcases');

      const res = await submissionService.runCode({
        challengeId: id,
        language,
        sourceCode,
      });
      setRunResult(res);
      setSelectedTestCaseIndex(0);
    } catch (err: any) {
      console.error('Run failed', err);
      setRunResult({
        status: 'SYSTEM_ERROR',
        passedTests: 0,
        totalTests: challenge?.sampleTestCases.length || 0,
        executionTimeMs: 0,
        memoryUsedBytes: 0,
        stdout: '',
        stderr: err?.message || 'Failed to execute code.',
        errorMessage: err?.message || 'Execution failed',
        testResults: [],
      });
    } finally {
      setIsRunning(false);
    }
  };

  const handleSubmitCode = async () => {
    if (!id || isRunning || isSubmitting) return;
    try {
      setIsSubmitting(true);
      setSubmitResult(null);
      setActiveTab('testcases');

      const res = await submissionService.submitCode({
        challengeId: id,
        language,
        sourceCode,
      });
      setSubmitResult(res);
      setSelectedTestCaseIndex(0);

      if (res.status === 'PASSED') {
        if (challenge) {
          setChallenge({ ...challenge, progressStatus: 'SOLVED' });
        }
        if (res.xpResult?.leveledUp) {
          setLevelUpData({
            show: true,
            level: res.xpResult.newLevel,
            prevLevel: res.xpResult.previousLevel,
            xp: res.xpEarned,
          });
        } else {
          setSolveBanner({
            show: true,
            xp: res.xpEarned,
            firstSolve: res.firstSolve,
          });
        }
      }
      loadSubmissionHistory();
    } catch (err: any) {
      console.error('Submission failed', err);
      setSubmitResult({
        submissionId: '',
        status: 'SYSTEM_ERROR',
        passedTests: 0,
        totalTests: 0,
        executionTimeMs: 0,
        memoryUsedBytes: 0,
        errorMessage: err?.message || 'Submission failed.',
        xpEarned: 0,
        firstSolve: false,
        xpResult: null,
        createdAt: new Date().toISOString(),
        testResults: [],
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  const loadSubmissionHistory = async () => {
    if (!id) return;
    try {
      setSubmissionsLoading(true);
      const res = await submissionService.getChallengeSubmissions(id);
      setSubmissions(res.content);
    } catch (err) {
      console.error('Failed to load submissions', err);
    } finally {
      setSubmissionsLoading(false);
    }
  };

  const handleTabChange = (tab: 'testcases' | 'console' | 'history') => {
    setActiveTab(tab);
    if (tab === 'history') {
      loadSubmissionHistory();
    }
  };

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[600px] space-y-4">
        <Loader2 className="w-10 h-10 animate-spin text-cyan-600" />
        <p className="text-sm font-mono text-slate-500">Initializing DevArena Code Lab sandbox...</p>
      </div>
    );
  }

  if (error || !challenge) {
    return (
      <div className="max-w-md mx-auto mt-20 p-8 text-center bg-white rounded-2xl border border-slate-200 shadow-sm space-y-4">
        <AlertCircle className="w-12 h-12 text-rose-500 mx-auto" />
        <h2 className="text-xl font-bold text-slate-900">Code Lab Unavailable</h2>
        <p className="text-sm text-slate-600">{error || 'Unable to load coding challenge.'}</p>
        <Button variant="outline" onClick={() => navigate('/challenges')}>
          Back to Challenges
        </Button>
      </div>
    );
  }

  const currentTestResults: TestCaseResult[] =
    submitResult?.testResults ||
    runResult?.testResults ||
    challenge.sampleTestCases.map((stc) => ({
      testCaseId: stc.id,
      orderIndex: stc.orderIndex,
      input: stc.input,
      expectedOutput: stc.expectedOutput,
      actualOutput: null,
      passed: false,
      hidden: false,
      executionTimeMs: 0,
      errorMessage: null,
    }));

  const activeResultStatus: SubmissionStatus | null =
    submitResult?.status || runResult?.status || null;

  return (
    <div className="flex flex-col h-[calc(100vh-5rem)] -m-4 sm:-m-6 lg:-m-8 bg-slate-50 text-slate-800 overflow-hidden">
      {/* Top Bar */}
      <div className="flex flex-wrap items-center justify-between gap-3 px-4 py-3 bg-white/95 backdrop-blur-xl border-b border-slate-200/80 text-sm shadow-2xs">
        <div className="flex items-center gap-3">
          <Link
            to={`/challenges/${challenge.id}`}
            className="inline-flex items-center gap-1.5 text-xs font-semibold text-slate-500 hover:text-indigo-600 transition-colors"
          >
            <ArrowLeft className="w-3.5 h-3.5" />
            BACK
          </Link>
          <div className="h-4 w-px bg-slate-200" />
          <div className="flex items-center gap-2">
            <h1 className="font-bold text-slate-900 truncate max-w-xs sm:max-w-md">
              {challenge.title}
            </h1>
            <Badge
              variant={
                challenge.difficulty === 'EASY'
                  ? 'success'
                  : challenge.difficulty === 'MEDIUM'
                  ? 'warning'
                  : 'danger'
              }
              size="sm"
            >
              {challenge.difficulty}
            </Badge>
            {challenge.progressStatus === 'SOLVED' && (
              <span className="inline-flex items-center gap-1 text-[11px] font-mono font-bold text-emerald-700 bg-emerald-50 border border-emerald-200/80 px-2 py-0.5 rounded-full">
                <Check className="w-3 h-3" /> SOLVED
              </span>
            )}
          </div>
        </div>

        {/* Action Controls */}
        <div className="flex items-center gap-2">
          <div className="inline-flex rounded-xl bg-slate-100 p-1 border border-slate-200/80">
            {(['JAVA', 'PYTHON', 'JAVASCRIPT'] as ExecutionLanguage[]).map((lang) => (
              <button
                key={lang}
                onClick={() => handleLanguageSelect(lang)}
                className={`px-2.5 py-1 text-xs font-mono font-bold rounded-lg transition-all ${
                  language === lang
                    ? 'bg-gradient-to-r from-indigo-600 to-cyan-600 text-white shadow-2xs'
                    : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                {lang === 'JAVASCRIPT' ? 'JavaScript' : lang === 'PYTHON' ? 'Python' : 'Java'}
              </button>
            ))}
          </div>

          <div className="h-4 w-px bg-slate-200 hidden sm:block" />

          <button
            onClick={() => setShowMinimap(!showMinimap)}
            title={showMinimap ? 'Hide minimap' : 'Show minimap'}
            className={`p-1.5 rounded-xl border text-xs transition-colors hidden sm:inline-flex ${
              showMinimap
                ? 'bg-indigo-50 border-indigo-300 text-indigo-700'
                : 'border-slate-200 text-slate-500 hover:text-slate-900 hover:bg-slate-50'
            }`}
          >
            {showMinimap ? <Minimize2 className="w-4 h-4" /> : <Maximize2 className="w-4 h-4" />}
          </button>

          <Button
            variant="outline"
            size="sm"
            onClick={() => setShowResetConfirm(true)}
            leftIcon={<RotateCcw className="w-3.5 h-3.5" />}
            className="border-slate-200 bg-white text-slate-700 hover:bg-slate-50 shadow-2xs"
          >
            RESET
          </Button>

          <Button
            variant="outline"
            size="sm"
            onClick={handleRunCode}
            disabled={isRunning || isSubmitting}
            leftIcon={
              isRunning ? (
                <Loader2 className="w-3.5 h-3.5 animate-spin text-emerald-600" />
              ) : (
                <Play className="w-3.5 h-3.5 text-emerald-600 fill-emerald-600" />
              )
            }
            className="border-emerald-200 bg-emerald-50/70 text-emerald-700 hover:bg-emerald-100 shadow-2xs font-semibold"
          >
            {isRunning ? 'RUNNING...' : 'RUN'}
          </Button>

          <Button
            variant="premium"
            size="sm"
            onClick={handleSubmitCode}
            disabled={isRunning || isSubmitting}
            leftIcon={
              isSubmitting ? (
                <Loader2 className="w-3.5 h-3.5 animate-spin text-white" />
              ) : (
                <Send className="w-3.5 h-3.5" />
              )
            }
          >
            {isSubmitting ? 'EVALUATING...' : 'SUBMIT'}
          </Button>

          <Button
            variant="outline"
            size="sm"
            onClick={() => setIsAiCoachOpen(!isAiCoachOpen)}
            leftIcon={<Bot className="w-3.5 h-3.5 text-indigo-600" />}
            className="border-indigo-200 bg-indigo-50/70 text-indigo-700 hover:bg-indigo-100 shadow-2xs font-semibold"
          >
            AI COACH
          </Button>

          <button
            onClick={() => setIsReportModalOpen(true)}
            title="Report challenge issue"
            className="p-1.5 rounded-xl border border-slate-200 text-slate-400 hover:text-rose-600 hover:bg-rose-50 transition-colors"
          >
            <Flag className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Solve Banner */}
      <AnimatePresence>
        {solveBanner.show && (
          <motion.div
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: 'auto', opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            className="bg-gradient-to-r from-emerald-50 via-teal-50 to-cyan-50 border-b border-emerald-200/80 px-6 py-2.5 flex items-center justify-between text-xs text-emerald-900"
          >
            <div className="flex items-center gap-2 font-bold">
              <Sparkles className="w-4 h-4 text-emerald-600 animate-spin" />
              <span>
                {solveBanner.firstSolve
                  ? `VICTORY! Challenge Solved! +${solveBanner.xp} XP awarded to your arena rank.`
                  : 'All official test cases passed! Challenge solution verified.'}
              </span>
            </div>
            <button
              onClick={() => setSolveBanner({ ...solveBanner, show: false })}
              className="text-emerald-700 hover:text-emerald-900 font-mono font-bold"
            >
              DISMISS
            </button>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Workspace */}
      <div className="grid grid-cols-1 lg:grid-cols-12 flex-1 overflow-hidden">
        {/* Left: Challenge Specs */}
        <div className="lg:col-span-5 h-full overflow-y-auto border-r border-slate-200/80 p-6 space-y-6 bg-white/75 backdrop-blur-md">
          <div>
            <div className="text-xs font-mono text-indigo-600 font-bold uppercase tracking-wider mb-1">
              {challenge.category} • {challenge.xpReward} XP
            </div>
            <h2 className="text-2xl font-black text-slate-900 tracking-tight">
              {challenge.title}
            </h2>
          </div>

          <div className="prose prose-slate prose-sm max-w-none text-slate-700 leading-relaxed whitespace-pre-line font-sans">
            {challenge.description}
          </div>

          {challenge.sampleTestCases.length > 0 && (
            <div className="space-y-3">
              <h3 className="text-xs font-mono uppercase tracking-wider font-bold text-slate-500">
                Official Examples
              </h3>
              {challenge.sampleTestCases.map((tc, idx) => (
                <div
                  key={tc.id}
                  className="p-3.5 rounded-2xl bg-white border border-slate-200/90 shadow-2xs space-y-2 text-xs"
                >
                  <p className="font-mono font-bold text-indigo-600">Example {idx + 1}</p>
                  <div>
                    <span className="text-slate-500 font-mono">Input: </span>
                    <code className="bg-slate-50 border border-slate-200/70 px-2 py-0.5 rounded text-slate-800 font-mono">
                      {tc.input}
                    </code>
                  </div>
                  <div>
                    <span className="text-slate-500 font-mono">Expected Output: </span>
                    <code className="bg-emerald-50 border border-emerald-200/70 px-2 py-0.5 rounded text-emerald-800 font-bold font-mono">
                      {tc.expectedOutput}
                    </code>
                  </div>
                  {tc.explanation && (
                    <p className="text-slate-500 italic text-[11px] pt-1 border-t border-slate-100">
                      {tc.explanation}
                    </p>
                  )}
                </div>
              ))}
            </div>
          )}

          <div className="pt-2 border-t border-slate-200/80">
            <button
              onClick={() => setShowHints(!showHints)}
              className="w-full flex items-center justify-between p-3 rounded-xl bg-white border border-slate-200/90 text-xs font-mono font-bold text-slate-700 hover:text-indigo-600 shadow-2xs transition-colors"
            >
              <div className="flex items-center gap-2">
                <HelpCircle className="w-4 h-4 text-amber-500" />
                <span>NEED A HINT?</span>
              </div>
              {showHints ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
            </button>
            <AnimatePresence>
              {showHints && (
                <motion.div
                  initial={{ opacity: 0, height: 0 }}
                  animate={{ opacity: 1, height: 'auto' }}
                  exit={{ opacity: 0, height: 0 }}
                  className="mt-2 p-3.5 rounded-xl bg-amber-50 border border-amber-200/80 text-xs text-amber-900 space-y-2"
                >
                  <p>💡 Check standard library hash tables/dictionaries for O(N) lookups.</p>
                  <p>💡 Pay attention to constraints: inputs can contain duplicate elements or negative values.</p>
                </motion.div>
              )}
            </AnimatePresence>
          </div>
        </div>

        {/* Right: Monaco Editor + Bottom Results */}
        <div className="lg:col-span-7 flex flex-col h-full overflow-hidden bg-white">
          <div className="flex-1 min-h-[350px] relative bg-white">
            <Editor
              height="100%"
              language={language.toLowerCase() === 'javascript' ? 'javascript' : language.toLowerCase()}
              theme="light"
              value={sourceCode}
              onChange={handleCodeChange}
              options={{
                fontSize: 14,
                fontFamily: "'Fira Code', 'JetBrains Mono', Consolas, monospace",
                lineNumbers: 'on',
                minimap: { enabled: showMinimap },
                automaticLayout: true,
                scrollBeyondLastLine: false,
                tabSize: 2,
                cursorBlinking: 'smooth',
                renderWhitespace: 'selection',
                padding: { top: 12, bottom: 12 },
              }}
            />
          </div>

          {/* Results Panel */}
          <div className="h-64 flex flex-col border-t border-slate-200/90 bg-white">
            <div className="flex items-center justify-between px-4 py-2 bg-slate-50/80 border-b border-slate-200/80 text-xs">
              <div className="flex items-center gap-1">
                <button
                  onClick={() => handleTabChange('testcases')}
                  className={`px-3 py-1.5 rounded-lg font-mono font-bold flex items-center gap-1.5 transition-colors ${
                    activeTab === 'testcases'
                      ? 'bg-white text-indigo-700 border border-slate-200/90 shadow-2xs'
                      : 'text-slate-500 hover:text-slate-800'
                  }`}
                >
                  <CheckCircle2 className="w-3.5 h-3.5 text-indigo-600" />
                  Test Results
                </button>

                <button
                  onClick={() => handleTabChange('console')}
                  className={`px-3 py-1.5 rounded-lg font-mono font-bold flex items-center gap-1.5 transition-colors ${
                    activeTab === 'console'
                      ? 'bg-white text-indigo-700 border border-slate-200/90 shadow-2xs'
                      : 'text-slate-500 hover:text-slate-800'
                  }`}
                >
                  <Terminal className="w-3.5 h-3.5 text-slate-500" />
                  Console Output
                </button>

                <button
                  onClick={() => handleTabChange('history')}
                  className={`px-3 py-1.5 rounded-lg font-mono font-bold flex items-center gap-1.5 transition-colors ${
                    activeTab === 'history'
                      ? 'bg-white text-indigo-700 border border-slate-200/90 shadow-2xs'
                      : 'text-slate-500 hover:text-slate-800'
                  }`}
                >
                  <History className="w-3.5 h-3.5 text-amber-500" />
                  Submissions
                </button>
              </div>

              {activeResultStatus && (
                <div className="flex items-center gap-2 font-mono text-xs">
                  <span
                    className={`font-bold ${
                      activeResultStatus === 'PASSED'
                        ? 'text-emerald-600'
                        : activeResultStatus === 'COMPILATION_ERROR' || activeResultStatus === 'RUNTIME_ERROR'
                        ? 'text-rose-600'
                        : 'text-amber-600'
                    }`}
                  >
                    {activeResultStatus}
                  </span>
                  {(submitResult || runResult) && (
                    <span className="text-slate-500 text-[11px]">
                      ({(submitResult || runResult)?.passedTests}/{(submitResult || runResult)?.totalTests} passed in{' '}
                      {(submitResult || runResult)?.executionTimeMs}ms)
                    </span>
                  )}
                </div>
              )}
            </div>

            <div className="flex-1 p-4 overflow-y-auto font-mono text-xs">
              {activeTab === 'testcases' && (
                <div className="space-y-3">
                  <div className="flex items-center gap-2 overflow-x-auto pb-1">
                    {currentTestResults.map((tc, idx) => {
                      const isPassed = tc.passed;
                      const hasExecuted = submitResult || runResult;
                      return (
                        <button
                          key={tc.testCaseId || idx}
                          onClick={() => setSelectedTestCaseIndex(idx)}
                          className={`px-3 py-1.5 rounded-xl border text-xs font-mono font-bold flex items-center gap-1.5 transition-all ${
                            selectedTestCaseIndex === idx
                              ? 'bg-indigo-50 border-indigo-300 text-indigo-800 shadow-2xs'
                              : 'bg-white border-slate-200 text-slate-600 hover:bg-slate-50'
                          }`}
                        >
                          <span
                            className={`w-2 h-2 rounded-full ${
                              !hasExecuted
                                ? 'bg-slate-300'
                                : isPassed
                                ? 'bg-emerald-500'
                                : 'bg-rose-500'
                            }`}
                          />
                          {tc.hidden ? `Hidden Case ${idx + 1}` : `Case ${idx + 1}`}
                        </button>
                      );
                    })}
                  </div>

                  {currentTestResults[selectedTestCaseIndex] ? (
                    <div className="p-3.5 bg-slate-50/70 rounded-2xl border border-slate-200/80 space-y-2">
                      {currentTestResults[selectedTestCaseIndex].hidden ? (
                        <div className="text-center py-4 text-slate-500 italic font-sans">
                          🔒 Hidden test case content is protected to prevent hardcoding.
                          <div className="mt-2">
                            <Badge
                              variant={currentTestResults[selectedTestCaseIndex].passed ? 'success' : 'danger'}
                              size="sm"
                            >
                              {currentTestResults[selectedTestCaseIndex].passed ? 'PASSED' : 'FAILED'}
                            </Badge>
                          </div>
                        </div>
                      ) : (
                        <>
                          <div>
                            <span className="text-slate-500 font-sans font-medium">Input:</span>
                            <pre className="mt-1 p-2 rounded-xl bg-white border border-slate-200 text-slate-800 overflow-x-auto">
                              {currentTestResults[selectedTestCaseIndex].input}
                            </pre>
                          </div>

                          <div className="grid grid-cols-2 gap-3">
                            <div>
                              <span className="text-slate-500 font-sans font-medium">Expected Output:</span>
                              <pre className="mt-1 p-2 rounded-xl bg-white border border-emerald-200 text-emerald-800 font-bold overflow-x-auto">
                                {currentTestResults[selectedTestCaseIndex].expectedOutput}
                              </pre>
                            </div>
                            <div>
                              <span className="text-slate-500 font-sans font-medium">Your Output:</span>
                              <pre
                                className={`mt-1 p-2 rounded-xl bg-white border overflow-x-auto ${
                                  currentTestResults[selectedTestCaseIndex].passed
                                    ? 'border-emerald-200 text-emerald-800 font-bold'
                                    : 'border-rose-200 text-rose-800'
                                }`}
                              >
                                {currentTestResults[selectedTestCaseIndex].actualOutput !== null
                                  ? currentTestResults[selectedTestCaseIndex].actualOutput
                                  : '(Not executed yet - Click Run or Submit)'}
                              </pre>
                            </div>
                          </div>
                        </>
                      )}
                    </div>
                  ) : (
                    <div className="text-slate-400 text-center py-6 font-sans">
                      Click Run or Submit to evaluate your solution.
                    </div>
                  )}
                </div>
              )}

              {activeTab === 'console' && (
                <div className="space-y-2">
                  <div className="p-3 bg-slate-50 rounded-xl border border-slate-200 text-slate-800 min-h-[120px] overflow-x-auto">
                    {(runResult?.stdout || submitResult?.status) ? (
                      <div>
                        {runResult?.stdout && (
                          <div>
                            <div className="text-slate-500 text-[10px] uppercase font-bold mb-1 font-sans">Standard Output:</div>
                            <pre className="text-slate-800">{runResult.stdout}</pre>
                          </div>
                        )}
                        {(runResult?.stderr || submitResult?.errorMessage) && (
                          <div className="mt-2 text-rose-600">
                            <div className="text-rose-600 text-[10px] uppercase font-bold mb-1 font-sans">Standard Error:</div>
                            <pre>{runResult?.stderr || submitResult?.errorMessage}</pre>
                          </div>
                        )}
                      </div>
                    ) : (
                      <span className="text-slate-400 font-sans">Console logs and runtime output will appear here.</span>
                    )}
                  </div>
                </div>
              )}

              {activeTab === 'history' && (
                <div>
                  {submissionsLoading ? (
                    <div className="flex items-center justify-center py-6 text-slate-500">
                      <Loader2 className="w-5 h-5 animate-spin mr-2" /> Loading history...
                    </div>
                  ) : submissions.length === 0 ? (
                    <div className="text-slate-400 text-center py-6 font-sans">
                      No submissions recorded for this challenge yet.
                    </div>
                  ) : (
                    <div className="space-y-2">
                      {submissions.map((sub) => (
                        <div
                          key={sub.id}
                          className="flex items-center justify-between p-2.5 rounded-xl bg-white border border-slate-200/90 hover:border-indigo-300 shadow-2xs transition-colors"
                        >
                          <div className="flex items-center gap-3">
                            <span
                              className={`w-2.5 h-2.5 rounded-full ${
                                sub.status === 'PASSED' ? 'bg-emerald-500' : 'bg-rose-500'
                              }`}
                            />
                            <div>
                              <span className="font-bold text-slate-900">{sub.status}</span>
                              <span className="text-slate-500 ml-2">
                                ({sub.passedTests}/{sub.totalTests} tests)
                              </span>
                            </div>
                          </div>
                          <div className="flex items-center gap-4 text-slate-500 text-[11px]">
                            <span>{sub.language}</span>
                            <span>{sub.executionTimeMs}ms</span>
                            <span>{new Date(sub.createdAt).toLocaleTimeString()}</span>
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Language Switch Confirmation Modal */}
      <AnimatePresence>
        {pendingLanguage && (
          <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm"
              onClick={() => setPendingLanguage(null)}
            />
            <motion.div
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.95 }}
              className="relative z-10 w-full max-w-sm bg-white border border-slate-200 rounded-3xl p-6 text-slate-900 shadow-premium-hover space-y-4"
            >
              <div className="flex items-center gap-3 text-amber-600 font-bold">
                <AlertTriangle className="w-5 h-5" />
                <span>Switch Language?</span>
              </div>
              <p className="text-xs text-slate-600 leading-relaxed">
                Switching to <strong className="text-slate-900">{pendingLanguage}</strong> will load its starter code. Your current edits for {language} are saved in local storage.
              </p>
              <div className="flex items-center justify-end gap-2 pt-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setPendingLanguage(null)}
                  className="border-slate-200 text-slate-700 hover:bg-slate-50"
                >
                  Cancel
                </Button>
                <Button
                  variant="primary"
                  size="sm"
                  onClick={() => applyLanguage(pendingLanguage)}
                >
                  Switch Language
                </Button>
              </div>
            </motion.div>
          </div>
        )}
      </AnimatePresence>

      {/* Reset Confirmation Modal */}
      <AnimatePresence>
        {showResetConfirm && (
          <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm"
              onClick={() => setShowResetConfirm(false)}
            />
            <motion.div
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.95 }}
              className="relative z-10 w-full max-w-sm bg-white border border-slate-200 rounded-3xl p-6 text-slate-900 shadow-premium-hover space-y-4"
            >
              <div className="flex items-center gap-3 text-rose-600 font-bold">
                <RotateCcw className="w-5 h-5" />
                <span>Reset to Starter Code?</span>
              </div>
              <p className="text-xs text-slate-600 leading-relaxed">
                This will overwrite your current code with the default starter template for {language}.
              </p>
              <div className="flex items-center justify-end gap-2 pt-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setShowResetConfirm(false)}
                  className="border-slate-200 text-slate-700 hover:bg-slate-50"
                >
                  Keep Code
                </Button>
                <Button
                  variant="danger"
                  size="sm"
                  onClick={handleResetCode}
                >
                  Reset Code
                </Button>
              </div>
            </motion.div>
          </div>
        )}
      </AnimatePresence>

      {/* Level Up Modal */}
      <LevelUpModal
        isOpen={levelUpData.show}
        level={levelUpData.level}
        prevLevel={levelUpData.prevLevel}
        xpEarned={levelUpData.xp}
        onClose={() => setLevelUpData({ ...levelUpData, show: false })}
      />

      {/* Module 09 AI Coach Panel */}
      <AiCoachPanel
        challengeId={challenge.id}
        currentCode={sourceCode}
        isOpen={isAiCoachOpen}
        onClose={() => setIsAiCoachOpen(false)}
      />

      {/* Module 09 Moderation Report Modal */}
      <ReportModal
        isOpen={isReportModalOpen}
        onClose={() => setIsReportModalOpen(false)}
        targetType="CHALLENGE"
        targetId={challenge.id}
        targetName={challenge.title}
      />
    </div>
  );
};
