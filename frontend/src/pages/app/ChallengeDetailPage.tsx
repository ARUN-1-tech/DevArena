import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { challengeService } from '../../services/challengeService';
import { Challenge, SubmitAnswerResponse } from '../../types/arena';
import { Card } from '../../components/ui/Card';
import { Button } from '../../components/ui/Button';
import { Badge } from '../../components/ui/Badge';
import {
  ArrowLeft,
  Code2,
  Clock,
  Sparkles,
  CheckCircle2,
  Play,
  Check,
  Tag,
  AlertCircle,
  Loader2,
  HelpCircle,
  Zap,
  Lightbulb,
  XCircle,
} from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

export const ChallengeDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [challenge, setChallenge] = useState<Challenge | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [actionLoading, setActionLoading] = useState(false);

  // MCQ / Answer state
  const [selectedOption, setSelectedOption] = useState<string>('');
  const [submittingAnswer, setSubmittingAnswer] = useState(false);
  const [answerResult, setAnswerResult] = useState<SubmitAnswerResponse | null>(null);
  const [showHints, setShowHints] = useState(false);

  useEffect(() => {
    if (!id) return;
    loadChallenge(id);
  }, [id]);

  const loadChallenge = async (challengeId: string) => {
    try {
      setLoading(true);
      setError(null);
      const data = await challengeService.getChallenge(challengeId);
      setChallenge(data);
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Failed to load challenge details.');
    } finally {
      setLoading(false);
    }
  };

  const handleStartChallenge = async () => {
    if (!challenge) return;
    try {
      setActionLoading(true);
      await challengeService.startChallenge(challenge.id);
      navigate(`/challenges/${challenge.id}/solve`);
    } catch {
      navigate(`/challenges/${challenge.id}/solve`);
    } finally {
      setActionLoading(false);
    }
  };

  const handleSubmitAnswer = async () => {
    if (!challenge || !selectedOption.trim()) return;
    try {
      setSubmittingAnswer(true);
      setAnswerResult(null);
      const res = await challengeService.submitAnswer(challenge.id, selectedOption);
      setAnswerResult(res);
      if (res.correct) {
        setChallenge({ ...challenge, progressStatus: 'SOLVED' });
      }
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Failed to submit answer.');
    } finally {
      setSubmittingAnswer(false);
    }
  };

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[400px] space-y-4">
        <Loader2 className="w-8 h-8 animate-spin text-indigo-600" />
        <p className="text-sm font-mono text-slate-500">Decrypting challenge parameters...</p>
      </div>
    );
  }

  if (error || !challenge) {
    return (
      <Card className="p-8 text-center max-w-md mx-auto space-y-4">
        <AlertCircle className="w-10 h-10 text-rose-500 mx-auto" />
        <h3 className="text-lg font-bold text-slate-900">Challenge Not Found</h3>
        <p className="text-sm text-slate-600">{error || 'This challenge may be unavailable.'}</p>
        <Button variant="outline" onClick={() => navigate('/challenges')}>
          Back to Problem Archive
        </Button>
      </Card>
    );
  }

  const isSolved = challenge.progressStatus === 'SOLVED';
  const isAttempted = challenge.progressStatus === 'ATTEMPTED';

  // Parse MCQ options if available
  let parsedOptions: string[] = [];
  if (challenge.options) {
    try {
      if (challenge.options.startsWith('[')) {
        parsedOptions = JSON.parse(challenge.options);
      } else {
        parsedOptions = challenge.options.split('\n').filter((o) => o.trim().length > 0);
      }
    } catch {
      parsedOptions = challenge.options.split('\n').filter((o) => o.trim().length > 0);
    }
  }

  return (
    <div className="space-y-8 max-w-5xl mx-auto pb-16">
      {/* Navigation Header */}
      <div className="flex items-center justify-between">
        <Button
          variant="ghost"
          size="sm"
          onClick={() => navigate('/challenges')}
          leftIcon={<ArrowLeft className="w-4 h-4" />}
          className="text-slate-600 hover:text-slate-900 font-mono"
        >
          BACK TO ARCHIVE
        </Button>

        <div className="flex items-center gap-2 font-mono text-xs">
          {isSolved ? (
            <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-50 text-emerald-700 border border-emerald-200 font-bold">
              <CheckCircle2 className="w-3.5 h-3.5" />
              SOLVED
            </span>
          ) : isAttempted ? (
            <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-amber-50 text-amber-700 border border-amber-200 font-bold">
              <Play className="w-3 h-3 fill-amber-600" />
              IN PROGRESS
            </span>
          ) : (
            <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-slate-100 text-slate-600 border border-slate-200">
              NOT STARTED
            </span>
          )}
        </div>
      </div>

      {/* Main Challenge Header Card */}
      <Card className="p-6 sm:p-8 bg-white/95 backdrop-blur-xl border-slate-200/90 shadow-premium rounded-3xl">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-6 pb-6 border-b border-slate-100">
          <div className="space-y-2">
            <div className="flex items-center gap-2 flex-wrap">
              <span className="text-[10px] font-mono px-2.5 py-0.5 rounded-full font-bold uppercase tracking-wider bg-slate-100 text-slate-700 border border-slate-200">
                {challenge.problemType || 'CODING'}
              </span>

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
              <span className="text-xs font-mono font-semibold text-slate-500 bg-slate-100 px-2.5 py-0.5 rounded-full border border-slate-200">
                {challenge.category.replace(/_/g, ' ')}
              </span>
            </div>

            <h1 className="text-2xl sm:text-3xl font-black text-slate-900 tracking-tight">
              {challenge.title}
            </h1>
          </div>

          {/* Quick Metrics */}
          <div className="flex items-center gap-4 bg-slate-50 p-3 rounded-2xl border border-slate-100 shrink-0 font-mono">
            <div className="text-center px-2">
              <p className="text-[10px] text-slate-400 uppercase font-semibold">REWARD</p>
              <p className="text-base font-extrabold text-indigo-600 flex items-center justify-center gap-1">
                <Sparkles className="w-3.5 h-3.5 text-amber-500" />
                +{challenge.xpReward} XP
              </p>
            </div>
            <div className="w-px h-8 bg-slate-200" />
            <div className="text-center px-2">
              <p className="text-[10px] text-slate-400 uppercase font-semibold">EST. TIME</p>
              <p className="text-base font-extrabold text-slate-700 flex items-center justify-center gap-1">
                <Clock className="w-3.5 h-3.5 text-slate-400" />
                {challenge.estimatedMinutes || Math.round((challenge.timeLimitSeconds || 900) / 60)}m
              </p>
            </div>
          </div>
        </div>

        {/* Problem Description Body */}
        <div className="mt-6 space-y-6">
          <div>
            <h3 className="text-xs font-mono uppercase font-bold text-slate-400 tracking-wider mb-2">
              PROBLEM DESCRIPTION
            </h3>
            <div className="prose prose-slate max-w-none text-sm text-slate-800 leading-relaxed whitespace-pre-line bg-slate-50/70 p-6 rounded-2xl border border-slate-100">
              {challenge.description}
            </div>
          </div>

          {/* Interactive MCQ / Quiz Choices if available */}
          {parsedOptions.length > 0 && (
            <div className="space-y-4 pt-2">
              <h3 className="text-xs font-mono uppercase font-bold text-slate-400 tracking-wider flex items-center gap-1.5">
                <HelpCircle className="w-3.5 h-3.5 text-indigo-600" />
                SELECT THE CORRECT ANSWER
              </h3>

              <div className="grid grid-cols-1 gap-3">
                {parsedOptions.map((opt, idx) => {
                  const letter = String.fromCharCode(65 + idx);
                  const isSelected = selectedOption === opt || selectedOption === letter;

                  return (
                    <button
                      key={idx}
                      onClick={() => setSelectedOption(opt)}
                      disabled={isSolved || submittingAnswer}
                      className={`p-4 rounded-2xl border text-left transition-all flex items-center gap-4 cursor-pointer font-sans ${
                        isSelected
                          ? 'bg-indigo-50/80 border-indigo-500 shadow-sm ring-2 ring-indigo-500/20'
                          : 'bg-white hover:bg-slate-50 border-slate-200'
                      }`}
                    >
                      <span
                        className={`w-8 h-8 rounded-xl flex items-center justify-center font-mono font-bold text-xs shrink-0 ${
                          isSelected
                            ? 'bg-indigo-600 text-white shadow-xs'
                            : 'bg-slate-100 text-slate-600'
                        }`}
                      >
                        {letter}
                      </span>
                      <span className="text-sm font-medium text-slate-800 flex-1">{opt}</span>
                    </button>
                  );
                })}
              </div>

              {/* Submit MCQ Answer Button */}
              {!isSolved && (
                <div className="flex items-center gap-3 pt-2">
                  <Button
                    variant="glow"
                    size="md"
                    onClick={handleSubmitAnswer}
                    disabled={!selectedOption || submittingAnswer}
                    leftIcon={submittingAnswer ? <Loader2 className="w-4 h-4 animate-spin" /> : <Zap className="w-4 h-4" />}
                  >
                    {submittingAnswer ? 'Evaluating Answer...' : 'Submit Answer'}
                  </Button>
                </div>
              )}

              {/* Answer result feedback banner */}
              {answerResult && (
                <motion.div
                  initial={{ opacity: 0, y: 8 }}
                  animate={{ opacity: 1, y: 0 }}
                  className={`p-4 rounded-2xl border text-sm ${
                    answerResult.correct
                      ? 'bg-emerald-50 border-emerald-200 text-emerald-900'
                      : 'bg-rose-50 border-rose-200 text-rose-900'
                  }`}
                >
                  <div className="flex items-start gap-3">
                    {answerResult.correct ? (
                      <CheckCircle2 className="w-5 h-5 text-emerald-600 shrink-0 mt-0.5" />
                    ) : (
                      <XCircle className="w-5 h-5 text-rose-600 shrink-0 mt-0.5" />
                    )}
                    <div className="space-y-1">
                      <p className="font-bold">{answerResult.message}</p>
                      {answerResult.explanation && (
                        <p className="text-xs text-slate-600 leading-relaxed font-sans">
                          {answerResult.explanation}
                        </p>
                      )}
                      {answerResult.rewardResult && (
                        <p className="text-xs font-bold text-emerald-700 font-mono mt-1">
                          +{answerResult.rewardResult.xpEarned} XP Earned! (Total XP: {answerResult.rewardResult.totalXp})
                        </p>
                      )}
                    </div>
                  </div>
                </motion.div>
              )}
            </div>
          )}

          {/* Hints Section */}
          {challenge.hints && (
            <div className="pt-2">
              <button
                onClick={() => setShowHints(!showHints)}
                className="text-xs font-mono font-bold text-amber-700 bg-amber-50 hover:bg-amber-100 border border-amber-200 px-3.5 py-1.5 rounded-xl flex items-center gap-1.5 transition-all cursor-pointer"
              >
                <Lightbulb className="w-3.5 h-3.5 text-amber-600" />
                {showHints ? 'Hide Hints' : 'Need a Hint?'}
              </button>

              <AnimatePresence>
                {showHints && (
                  <motion.div
                    initial={{ opacity: 0, height: 0 }}
                    animate={{ opacity: 1, height: 'auto' }}
                    exit={{ opacity: 0, height: 0 }}
                    className="mt-3 p-4 rounded-2xl bg-amber-50/60 border border-amber-200/80 text-xs font-mono text-amber-900 leading-relaxed"
                  >
                    {challenge.hints}
                  </motion.div>
                )}
              </AnimatePresence>
            </div>
          )}

          {/* Tags */}
          {challenge.tags && (
            <div>
              <h3 className="text-xs font-mono uppercase font-bold text-slate-400 tracking-wider mb-2 flex items-center gap-1.5">
                <Tag className="w-3.5 h-3.5" />
                CONCEPT TAGS
              </h3>
              <div className="flex flex-wrap gap-2">
                {challenge.tags.split(',').map((t, idx) => (
                  <span
                    key={idx}
                    className="text-xs font-mono px-2.5 py-1 rounded-lg bg-slate-100 text-slate-600 border border-slate-200"
                  >
                    #{t.trim()}
                  </span>
                ))}
              </div>
            </div>
          )}

          {/* Bottom Action Tray */}
          <div className="pt-6 border-t border-slate-100 flex flex-col sm:flex-row items-center justify-between gap-4">
            <div className="text-xs font-mono text-slate-500">
              {isSolved ? (
                <span className="text-emerald-600 font-semibold flex items-center gap-1.5">
                  <Check className="w-4 h-4" />
                  Completed and verified on DevArena.
                </span>
              ) : (
                <span>Prepare your solution and test edge cases.</span>
              )}
            </div>

            <div className="flex items-center gap-3 w-full sm:w-auto">
              <Button
                variant="glow"
                size="md"
                onClick={handleStartChallenge}
                disabled={actionLoading}
                leftIcon={<Code2 className="w-4 h-4" />}
              >
                {isSolved ? 'OPEN CODE LAB' : isAttempted ? 'CONTINUE IN CODE LAB' : 'ENTER CODE LAB'}
              </Button>
            </div>
          </div>
        </div>
      </Card>
    </div>
  );
};

