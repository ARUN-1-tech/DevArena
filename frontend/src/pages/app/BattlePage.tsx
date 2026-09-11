import React, { useEffect, useState, useRef, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Editor from '@monaco-editor/react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Swords,
  Play,
  Send,
  Flag,
  Clock,
  CheckCircle2,
  AlertCircle,
  Trophy,
  Loader2,
  Code2,
  Terminal,
} from 'lucide-react';
import { battleService, BattleSubmitResult } from '../../services/battleService';
import { submissionService } from '../../services/submissionService';
import { webSocketService } from '../../services/webSocketService';
import { BattleDetail, BattlePlayer } from '../../types/battle';
import { ExecutionLanguage, RunCodeResponse } from '../../types/submission';
import { useAuth } from '../../contexts/AuthContext';
import { Button } from '../../components/ui/Button';
import { Badge } from '../../components/ui/Badge';

export const BattlePage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();

  // Battle State
  const [battle, setBattle] = useState<BattleDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Ready & Countdown
  const [isReady, setIsReady] = useState(false);
  const [countdown, setCountdown] = useState<number | null>(null);

  // Time remaining in seconds (server-authoritative)
  const [timeRemaining, setTimeRemaining] = useState<number>(900);

  // Code Editor State
  const [language, setLanguage] = useState<ExecutionLanguage>('PYTHON');
  const [code, setCode] = useState<string>('');
  const [isRunning, setIsRunning] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [runResult, setRunResult] = useState<RunCodeResponse | null>(null);
  const [activeTab, setActiveTab] = useState<'tests' | 'console'>('tests');

  // Opponent Live Beacon
  const [opponentStatusText, setOpponentStatusText] = useState<string>('Opponent connected');
  const [opponentStatusType, setOpponentStatusType] = useState<'info' | 'active' | 'warning'>('info');

  // Forfeit Confirmation Modal
  const [showForfeitModal, setShowForfeitModal] = useState(false);

  // Keystroke Beacon Debounce
  const lastCodingBeaconRef = useRef<number>(0);

  const fetchBattleState = useCallback(async () => {
    if (!id) return;
    try {
      const data = await battleService.getBattle(id);
      setBattle(data);

      // Set initial code from starter templates
      if (!code && data.challenge?.starterTemplates) {
        setCode(data.challenge.starterTemplates[language] || data.challenge.starterTemplates.PYTHON || '');
      }

      // Calculate server remaining time
      if (data.status === 'IN_PROGRESS' && data.startedAt) {
        const elapsed = Math.floor((Date.now() - new Date(data.startedAt).getTime()) / 1000);
        const remaining = Math.max(0, data.durationSeconds - elapsed);
        setTimeRemaining(remaining);
      }

      if (data.status === 'COMPLETED') {
        navigate(`/battle/${id}/result`);
      }
    } catch (err: any) {
      setError(err.message || 'Failed to load battle session.');
    } finally {
      setLoading(false);
    }
  }, [id, language, code, navigate]);

  useEffect(() => {
    fetchBattleState();

    // Connect to WebSocket and subscribe to this battle's room
    let unsubscribe = () => {};
    const initSocket = async () => {
      if (!id) return;
      await webSocketService.connect();
      unsubscribe = webSocketService.subscribe(`/topic/battle.${id}`, (event) => {
        handleBattleRoomEvent(event);
      });
    };

    initSocket();

    return () => {
      unsubscribe();
    };
  }, [id, fetchBattleState]);

  // Handle STOMP battle events
  const handleBattleRoomEvent = (event: any) => {
    if (!event || !event.type) return;

    switch (event.type) {
      case 'PLAYER_STATUS':
        if (event.payload?.userId !== user?.id) {
          if (event.payload?.status === 'CODING') {
            setOpponentStatusText('Opponent is coding...');
            setOpponentStatusType('active');
            setTimeout(() => {
              setOpponentStatusText('Opponent connected');
              setOpponentStatusType('info');
            }, 3000);
          } else if (event.payload?.status === 'READY') {
            setOpponentStatusText('Opponent is ready!');
            setOpponentStatusType('active');
          }
        }
        break;

      case 'BATTLE_STARTED':
        // Trigger 3, 2, 1, FIGHT countdown
        runLobbyCountdown();
        break;

      case 'PLAYER_SUBMITTED':
        if (event.payload?.userId !== user?.id) {
          setOpponentStatusText('Opponent submitted code!');
          setOpponentStatusType('warning');
        }
        break;

      case 'BATTLE_FINISHED':
        setOpponentStatusText('Battle finished!');
        setTimeout(() => {
          navigate(`/battle/${id}/result`);
        }, 1200);
        break;

      case 'OPPONENT_DISCONNECTED':
        setOpponentStatusText('Opponent disconnected (grace period active)');
        setOpponentStatusType('warning');
        break;
    }
  };

  const runLobbyCountdown = () => {
    setCountdown(3);
    const interval = setInterval(() => {
      setCountdown((prev) => {
        if (prev === null || prev <= 1) {
          clearInterval(interval);
          setCountdown(0); // Display "FIGHT!"
          setTimeout(() => {
            setCountdown(null);
            fetchBattleState();
          }, 900);
          return null;
        }
        return prev - 1;
      });
    }, 1000);
  };

  // Authoritative Battle Clock Countdown
  useEffect(() => {
    if (!battle || battle.status !== 'IN_PROGRESS') return;

    const interval = setInterval(() => {
      setTimeRemaining((prev) => {
        if (prev <= 1) {
          clearInterval(interval);
          navigate(`/battle/${id}/result`);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(interval);
  }, [battle?.status, id, navigate]);

  // Handle Ready Click
  const handleReadyClick = async () => {
    setIsReady(true);
    webSocketService.send(`/app/battle/${id}/ready`, {});
    try {
      if (id) await battleService.markReady(id);
    } catch {
      // WebSocket is primary
    }
  };

  // Handle Keystrokes (Debounced Activity Beacon)
  const handleEditorChange = (value?: string) => {
    setCode(value || '');
    const now = Date.now();
    if (now - lastCodingBeaconRef.current > 3000) {
      lastCodingBeaconRef.current = now;
      webSocketService.send(`/app/battle/${id}/coding`, {});
    }
  };

  // Switch language
  const handleLanguageChange = (newLang: ExecutionLanguage) => {
    setLanguage(newLang);
    if (battle?.challenge?.starterTemplates && battle.challenge.starterTemplates[newLang]) {
      setCode(battle.challenge.starterTemplates[newLang]);
    }
  };

  // Run Test Cases
  const handleRunCode = async () => {
    if (!battle?.challenge) return;
    setIsRunning(true);
    setRunResult(null);
    try {
      const res = await submissionService.runCode({
        challengeId: battle.challenge.id,
        language,
        sourceCode: code,
      });
      setRunResult(res);
      setActiveTab('tests');
    } catch (err: any) {
      alert(err.message || 'Run execution failed.');
    } finally {
      setIsRunning(false);
    }
  };

  // Submit Solution
  const handleSubmitCode = async () => {
    if (!id) return;
    setIsSubmitting(true);
    try {
      const res: BattleSubmitResult = await battleService.submitBattleCode(id, {
        language,
        sourceCode: code,
      });

      if (res.battleFinished) {
        navigate(`/battle/${id}/result`);
      } else {
        alert(`Solution evaluated: ${res.passedTests}/${res.totalTests} tests passed. Check test output and try again!`);
      }
    } catch (err: any) {
      alert(err.message || 'Submission failed.');
    } finally {
      setIsSubmitting(false);
    }
  };

  // Forfeit Battle
  const handleForfeit = async () => {
    if (!id) return;
    try {
      await battleService.forfeitBattle(id);
      navigate(`/battle/${id}/result`);
    } catch (err: any) {
      alert(err.message || 'Failed to forfeit battle.');
    }
  };

  const formatClock = (seconds: number) => {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-950 flex flex-col items-center justify-center text-white">
        <Loader2 className="w-10 h-10 text-indigo-500 animate-spin mb-4" />
        <p className="text-slate-400 font-medium">Entering Battle Room...</p>
      </div>
    );
  }

  if (error || !battle) {
    return (
      <div className="min-h-screen bg-slate-950 flex flex-col items-center justify-center p-4">
        <div className="bg-slate-900 border border-slate-800 rounded-3xl p-8 max-w-md w-full text-center">
          <AlertCircle className="w-12 h-12 text-rose-500 mx-auto mb-4" />
          <h2 className="text-xl font-bold text-white mb-2">Battle Error</h2>
          <p className="text-slate-400 text-sm mb-6">{error || 'Session not available'}</p>
          <Button onClick={() => navigate('/home')} className="w-full">
            Back to Arena HQ
          </Button>
        </div>
      </div>
    );
  }

  const isMePlayer1 = battle.player1.userId === user?.id;
  const me: BattlePlayer = isMePlayer1 ? battle.player1 : battle.player2;
  const opponent: BattlePlayer = isMePlayer1 ? battle.player2 : battle.player1;

  return (
    <div className="h-screen flex flex-col bg-slate-950 text-slate-100 overflow-hidden select-none">
      {/* 3-2-1 FIGHT Countdown Overlay */}
      <AnimatePresence>
        {countdown !== null && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 z-50 bg-black/85 backdrop-blur-md flex items-center justify-center pointer-events-none"
          >
            <motion.div
              key={countdown}
              initial={{ scale: 0.3, opacity: 0 }}
              animate={{ scale: 1.2, opacity: 1 }}
              exit={{ scale: 2, opacity: 0 }}
              transition={{ duration: 0.8, ease: 'easeOut' }}
              className="text-center"
            >
              <span
                className={`text-8xl md:text-9xl font-black tracking-tighter ${
                  countdown === 0 ? 'text-amber-400' : 'text-indigo-400'
                }`}
              >
                {countdown === 0 ? 'FIGHT!' : countdown}
              </span>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* TOP HEADER: Player vs Opponent & Server Timer */}
      <header className="h-16 bg-slate-900 border-b border-slate-800 px-6 flex items-center justify-between shrink-0 z-20">
        {/* Your Player Card */}
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-indigo-600 flex items-center justify-center font-black text-white text-base shadow-md shadow-indigo-600/30">
            {me.username[0]?.toUpperCase()}
          </div>
          <div className="flex flex-col">
            <div className="flex items-center gap-2">
              <span className="font-bold text-sm text-white">{me.username}</span>
              <span className="text-[10px] uppercase font-bold tracking-wider px-1.5 py-0.5 rounded bg-indigo-500/20 text-indigo-400 border border-indigo-500/30">
                Lvl {me.level}
              </span>
            </div>
            <span className="text-xs font-semibold text-amber-400 flex items-center gap-1">
              <Trophy className="w-3 h-3" /> {me.rating} MMR
            </span>
          </div>
        </div>

        {/* Center: Server Timer & VS Badge */}
        <div className="flex flex-col items-center">
          <div className="flex items-center gap-2 px-4 py-1.5 rounded-full bg-slate-800 border border-slate-700 shadow-inner">
            <Clock
              className={`w-4 h-4 ${
                timeRemaining < 120 ? 'text-rose-400 animate-pulse' : 'text-indigo-400'
              }`}
            />
            <span
              className={`font-mono font-black text-base tracking-wider ${
                timeRemaining < 120 ? 'text-rose-400' : 'text-white'
              }`}
            >
              {formatClock(timeRemaining)}
            </span>
          </div>
          {/* Opponent Status Beacon */}
          <div className="flex items-center gap-1.5 mt-1 text-[11px]">
            <span
              className={`w-2 h-2 rounded-full ${
                opponentStatusType === 'active'
                  ? 'bg-emerald-400 animate-ping'
                  : opponentStatusType === 'warning'
                  ? 'bg-amber-400'
                  : 'bg-slate-400'
              }`}
            />
            <span className="text-slate-400 font-medium">{opponentStatusText}</span>
          </div>
        </div>

        {/* Opponent Player Card */}
        <div className="flex items-center gap-3">
          <div className="flex flex-col text-right">
            <div className="flex items-center justify-end gap-2">
              <span className="text-[10px] uppercase font-bold tracking-wider px-1.5 py-0.5 rounded bg-rose-500/20 text-rose-400 border border-rose-500/30">
                Lvl {opponent.level}
              </span>
              <span className="font-bold text-sm text-white">{opponent.username}</span>
            </div>
            <span className="text-xs font-semibold text-amber-400 flex items-center justify-end gap-1">
              <Trophy className="w-3 h-3" /> {opponent.rating} MMR
            </span>
          </div>
          <div className="w-10 h-10 rounded-xl bg-rose-600 flex items-center justify-center font-black text-white text-base shadow-md shadow-rose-600/30">
            {opponent.username[0]?.toUpperCase()}
          </div>
        </div>
      </header>

      {/* LOBBY WAITING BANNER (If not started yet) */}
      {battle.status === 'WAITING' && (
        <div className="bg-indigo-950/60 border-b border-indigo-900/40 px-6 py-3 flex items-center justify-between z-10">
          <div className="flex items-center gap-2 text-indigo-300 text-sm">
            <Swords className="w-4 h-4 text-indigo-400 animate-pulse" />
            <span>Match is preparing. Both players must mark Ready to begin.</span>
          </div>
          <Button
            size="sm"
            variant={isReady ? 'secondary' : 'primary'}
            onClick={handleReadyClick}
            disabled={isReady}
            className="px-6 font-bold"
          >
            {isReady ? 'READY ✓' : 'CLICK READY'}
          </Button>
        </div>
      )}

      {/* MAIN WORKSPACE: SPLIT SCREEN */}
      <div className="flex-1 flex overflow-hidden">
        {/* LEFT COLUMN: Challenge Specs & Examples */}
        <div className="w-1/2 border-r border-slate-800 flex flex-col bg-slate-900/50 overflow-y-auto p-6">
          <div className="mb-4">
            <div className="flex items-center gap-2 mb-2">
              <Badge variant="purple">{battle.challenge.category}</Badge>
              <Badge
                variant={
                  battle.challenge.difficulty === 'EASY'
                    ? 'success'
                    : battle.challenge.difficulty === 'MEDIUM'
                    ? 'warning'
                    : 'danger'
                }
              >
                {battle.challenge.difficulty}
              </Badge>
              <span className="text-xs text-slate-400 font-medium ml-auto flex items-center gap-1">
                <Clock className="w-3.5 h-3.5" /> 15 min limit
              </span>
            </div>
            <h1 className="text-2xl font-black text-white tracking-tight">
              {battle.challenge.title}
            </h1>
          </div>

          {/* Description */}
          <div className="prose prose-invert prose-sm max-w-none text-slate-300 whitespace-pre-line mb-6 leading-relaxed">
            {battle.challenge.description}
          </div>

          {/* Sample Test Cases */}
          {battle.challenge.sampleTestCases && battle.challenge.sampleTestCases.length > 0 && (
            <div className="mt-4">
              <h3 className="text-xs font-bold uppercase tracking-wider text-slate-400 mb-3">
                Visible Test Cases
              </h3>
              <div className="space-y-3">
                {battle.challenge.sampleTestCases.map((tc, idx) => (
                  <div
                    key={tc.id || idx}
                    className="bg-slate-800/60 border border-slate-750 rounded-xl p-3 text-xs"
                  >
                    <div className="text-slate-400 mb-1 font-semibold">Case {idx + 1}</div>
                    <div className="grid grid-cols-2 gap-2 font-mono">
                      <div>
                        <span className="text-slate-400 block text-[11px]">Input:</span>
                        <div className="bg-slate-950/80 p-2 rounded text-slate-200 mt-0.5 whitespace-pre-wrap">
                          {tc.input}
                        </div>
                      </div>
                      <div>
                        <span className="text-slate-400 block text-[11px]">Expected:</span>
                        <div className="bg-slate-950/80 p-2 rounded text-emerald-300 mt-0.5 whitespace-pre-wrap">
                          {tc.expectedOutput}
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* RIGHT COLUMN: Monaco Editor & Interactive Console */}
        <div className="w-1/2 flex flex-col bg-slate-950">
          {/* Editor Action Header */}
          <div className="h-12 bg-slate-900/90 border-b border-slate-800 px-4 flex items-center justify-between shrink-0">
            {/* Language Selector */}
            <div className="flex items-center gap-2">
              <Code2 className="w-4 h-4 text-indigo-400" />
              {(['JAVA', 'PYTHON', 'JAVASCRIPT'] as ExecutionLanguage[]).map((lang) => (
                <button
                  key={lang}
                  onClick={() => handleLanguageChange(lang)}
                  className={`px-2.5 py-1 text-xs font-bold rounded-lg transition-colors ${
                    language === lang
                      ? 'bg-indigo-600 text-white shadow-sm'
                      : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800'
                  }`}
                >
                  {lang}
                </button>
              ))}
            </div>

            {/* Action Buttons: Run, Submit, Forfeit */}
            <div className="flex items-center gap-2">
              <Button
                size="sm"
                variant="outline"
                onClick={handleRunCode}
                disabled={isRunning || isSubmitting}
                className="text-xs h-8 border-slate-700 hover:bg-slate-800 gap-1.5"
              >
                {isRunning ? (
                  <Loader2 className="w-3.5 h-3.5 animate-spin" />
                ) : (
                  <Play className="w-3.5 h-3.5 text-emerald-400 fill-emerald-400" />
                )}
                Run
              </Button>

              <Button
                size="sm"
                variant="primary"
                onClick={handleSubmitCode}
                disabled={isRunning || isSubmitting}
                className="text-xs h-8 bg-indigo-600 hover:bg-indigo-500 font-bold gap-1.5 shadow-md shadow-indigo-600/30"
              >
                {isSubmitting ? (
                  <Loader2 className="w-3.5 h-3.5 animate-spin" />
                ) : (
                  <Send className="w-3.5 h-3.5" />
                )}
                Submit
              </Button>

              <button
                onClick={() => setShowForfeitModal(true)}
                title="Forfeit Match"
                className="p-1.5 rounded-lg text-slate-500 hover:text-rose-400 hover:bg-rose-500/10 transition-colors ml-2"
              >
                <Flag className="w-4 h-4" />
              </button>
            </div>
          </div>

          {/* Monaco Editor */}
          <div className="flex-1 min-h-[300px] relative">
            <Editor
              height="100%"
              theme="vs-dark"
              language={language === 'JAVA' ? 'java' : language === 'PYTHON' ? 'python' : 'javascript'}
              value={code}
              onChange={handleEditorChange}
              options={{
                minimap: { enabled: false },
                fontSize: 13,
                lineNumbers: 'on',
                scrollBeyondLastLine: false,
                automaticLayout: true,
                padding: { top: 12, bottom: 12 },
              }}
            />
          </div>

          {/* Bottom Results Drawer */}
          <div className="h-44 bg-slate-900 border-t border-slate-800 flex flex-col shrink-0">
            {/* Drawer Tabs */}
            <div className="h-9 border-b border-slate-800 px-4 flex items-center gap-4 text-xs font-semibold">
              <button
                onClick={() => setActiveTab('tests')}
                className={`flex items-center gap-1.5 py-1.5 border-b-2 transition-colors ${
                  activeTab === 'tests'
                    ? 'border-indigo-500 text-indigo-400'
                    : 'border-transparent text-slate-400 hover:text-slate-200'
                }`}
              >
                <CheckCircle2 className="w-3.5 h-3.5" />
                <span>Test Cases</span>
              </button>

              <button
                onClick={() => setActiveTab('console')}
                className={`flex items-center gap-1.5 py-1.5 border-b-2 transition-colors ${
                  activeTab === 'console'
                    ? 'border-indigo-500 text-indigo-400'
                    : 'border-transparent text-slate-400 hover:text-slate-200'
                }`}
              >
                <Terminal className="w-3.5 h-3.5" />
                <span>Console Output</span>
              </button>
            </div>

            {/* Drawer Content */}
            <div className="flex-1 p-3 overflow-y-auto text-xs font-mono">
              {activeTab === 'tests' && (
                <div>
                  {!runResult ? (
                    <span className="text-slate-500 italic">
                      Click "Run" to test your solution against visible test cases.
                    </span>
                  ) : (
                    <div>
                      <div className="flex items-center gap-2 mb-2 font-sans font-bold">
                        <span
                          className={
                            runResult.status === 'PASSED' ? 'text-emerald-400' : 'text-rose-400'
                          }
                        >
                          {runResult.status} ({runResult.passedTests}/{runResult.totalTests} Passed)
                        </span>
                        <span className="text-slate-500 text-xs font-normal">
                          in {runResult.executionTimeMs}ms
                        </span>
                      </div>

                      <div className="space-y-2">
                        {runResult.testResults.map((r, i) => (
                          <div
                            key={i}
                            className={`p-2 rounded border ${
                              r.passed
                                ? 'bg-emerald-500/10 border-emerald-500/20 text-emerald-300'
                                : 'bg-rose-500/10 border-rose-500/20 text-rose-300'
                            }`}
                          >
                            <div className="font-semibold mb-1">
                              Case {r.orderIndex}: {r.passed ? 'PASSED' : 'FAILED'}
                            </div>
                            <div className="text-[11px] text-slate-400">
                              Input: {r.input} | Expected: {r.expectedOutput} | Output: {r.actualOutput || '(none)'}
                            </div>
                            {r.errorMessage && (
                              <div className="text-rose-400 mt-1 whitespace-pre-wrap text-[11px]">
                                {r.errorMessage}
                              </div>
                            )}
                          </div>
                        ))}
                      </div>
                    </div>
                  )}
                </div>
              )}

              {activeTab === 'console' && (
                <div className="whitespace-pre-wrap text-slate-300">
                  {runResult?.stdout || runResult?.stderr || (
                    <span className="text-slate-500 italic">No standard output logged.</span>
                  )}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Forfeit Confirmation Modal */}
      <AnimatePresence>
        {showForfeitModal && (
          <div className="fixed inset-0 z-50 bg-black/75 backdrop-blur-sm flex items-center justify-center p-4">
            <motion.div
              initial={{ scale: 0.9, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.9, opacity: 0 }}
              className="bg-slate-900 border border-slate-800 rounded-3xl p-6 max-w-sm w-full text-center shadow-2xl"
            >
              <Flag className="w-12 h-12 text-rose-500 mx-auto mb-3" />
              <h3 className="text-lg font-bold text-white mb-2">Forfeit Battle?</h3>
              <p className="text-slate-400 text-xs mb-6">
                Are you sure you want to forfeit? Your opponent will instantly receive the victory and rating MMR.
              </p>
              <div className="grid grid-cols-2 gap-3">
                <Button
                  variant="outline"
                  onClick={() => setShowForfeitModal(false)}
                  className="w-full text-xs"
                >
                  Cancel
                </Button>
                <Button
                  variant="danger"
                  onClick={handleForfeit}
                  className="w-full text-xs font-bold"
                >
                  Confirm Forfeit
                </Button>
              </div>
            </motion.div>
          </div>
        )}
      </AnimatePresence>
    </div>
  );
};

export default BattlePage;
