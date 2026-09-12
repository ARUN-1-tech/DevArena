import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { challengeService } from '../../services/challengeService';
import { Challenge } from '../../types/arena';
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
} from 'lucide-react';
import { motion } from 'framer-motion';

export const ChallengeDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [challenge, setChallenge] = useState<Challenge | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [actionLoading, setActionLoading] = useState(false);
  const [actionMessage, setActionMessage] = useState<string | null>(null);

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

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[400px] space-y-4">
        <Loader2 className="w-8 h-8 animate-spin text-cyan-600" />
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
          Back to Challenge Archive
        </Button>
      </Card>
    );
  }

  const isSolved = challenge.progressStatus === 'SOLVED';
  const isAttempted = challenge.progressStatus === 'ATTEMPTED';

  return (
    <div className="space-y-8 max-w-5xl mx-auto">
      {/* Navigation Header */}
      <div className="flex items-center justify-between">
        <Button
          variant="ghost"
          size="sm"
          onClick={() => navigate('/challenges')}
          leftIcon={<ArrowLeft className="w-4 h-4" />}
          className="text-slate-600 hover:text-slate-900"
        >
          BACK TO CHALLENGES
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
      <Card className="p-6 sm:p-8 bg-white border-slate-200/90 shadow-md rounded-2xl">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-6 pb-6 border-b border-slate-100">
          <div className="space-y-2">
            <div className="flex items-center gap-2 flex-wrap">
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
                {challenge.category.replace('_', ' ')}
              </span>
            </div>

            <h1 className="text-2xl sm:text-3xl font-black text-slate-900 tracking-tight">
              {challenge.title}
            </h1>
          </div>

          {/* Quick Metrics */}
          <div className="flex items-center gap-4 bg-slate-50 p-3 rounded-xl border border-slate-100 shrink-0 font-mono">
            <div className="text-center px-2">
              <p className="text-[10px] text-slate-400 uppercase font-semibold">REWARD</p>
              <p className="text-base font-extrabold text-cyan-600 flex items-center justify-center gap-1">
                <Sparkles className="w-3.5 h-3.5" />
                +{challenge.xpReward} XP
              </p>
            </div>
            <div className="w-px h-8 bg-slate-200" />
            <div className="text-center px-2">
              <p className="text-[10px] text-slate-400 uppercase font-semibold">EST. TIME</p>
              <p className="text-base font-extrabold text-slate-700 flex items-center justify-center gap-1">
                <Clock className="w-3.5 h-3.5 text-slate-400" />
                {challenge.estimatedMinutes}m
              </p>
            </div>
          </div>
        </div>

        {/* Action notification banner */}
        {actionMessage && (
          <motion.div
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            className="mt-4 p-3 rounded-xl bg-cyan-50 border border-cyan-200 text-xs font-mono text-cyan-800 flex items-center justify-between"
          >
            <span>{actionMessage}</span>
            <button
              onClick={() => setActionMessage(null)}
              className="text-cyan-600 hover:text-cyan-900 font-bold ml-2"
            >
              ✕
            </button>
          </motion.div>
        )}

        {/* Problem Description Body */}
        <div className="mt-6 space-y-6">
          <div>
            <h3 className="text-xs font-mono uppercase font-bold text-slate-400 tracking-wider mb-2">
              PROBLEM DESCRIPTION
            </h3>
            <div className="prose prose-slate max-w-none text-sm text-slate-700 leading-relaxed whitespace-pre-line bg-slate-50/50 p-6 rounded-xl border border-slate-100">
              {challenge.description}
            </div>
          </div>

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
                  Completed and verified on the DevArena ledger.
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
