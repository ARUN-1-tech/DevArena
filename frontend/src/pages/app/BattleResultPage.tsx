import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import {
  Trophy,
  Swords,
  Clock,
  Sparkles,
  ArrowRight,
  Home,
  Shield,
  Loader2,
  TrendingUp,
  TrendingDown,
  Minus,
} from 'lucide-react';
import { battleService } from '../../services/battleService';
import { BattleResult } from '../../types/battle';
import { Button } from '../../components/ui/Button';

export const BattleResultPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [result, setResult] = useState<BattleResult | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!id) return;

    const fetchResult = async () => {
      try {
        const data = await battleService.getBattleResult(id);
        setResult(data);
      } catch (err: any) {
        setError(err.message || 'Failed to load battle results.');
      } finally {
        setLoading(false);
      }
    };

    fetchResult();
  }, [id]);

  const formatDuration = (seconds: number) => {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${m}m ${s}s`;
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-50 flex flex-col items-center justify-center text-slate-800">
        <Loader2 className="w-10 h-10 text-indigo-600 animate-spin mb-4" />
        <p className="text-slate-600 font-semibold font-mono text-sm">Calculating Battle Rewards...</p>
      </div>
    );
  }

  if (error || !result) {
    return (
      <div className="min-h-screen bg-slate-50 flex flex-col items-center justify-center p-4">
        <div className="bg-white border border-slate-200 rounded-3xl p-8 max-w-md w-full text-center shadow-premium-hover">
          <h2 className="text-xl font-bold text-slate-900 mb-2">Notice</h2>
          <p className="text-slate-500 text-sm mb-6">{error || 'Result not found.'}</p>
          <Button onClick={() => navigate('/home')} className="w-full">
            Back to Arena HQ
          </Button>
        </div>
      </div>
    );
  }

  const isWin = result.outcome === 'WIN';
  const isLoss = result.outcome === 'LOSS';
  const isDraw = result.outcome === 'DRAW';

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col items-center justify-center p-4 relative overflow-hidden">
      {/* Dynamic Ambient Background Glow */}
      <div
        className={`absolute inset-0 pointer-events-none ${
          isWin
            ? 'bg-[radial-gradient(circle_at_center,rgba(245,158,11,0.12)_0,transparent_70%)]'
            : isLoss
            ? 'bg-[radial-gradient(circle_at_center,rgba(239,68,68,0.08)_0,transparent_70%)]'
            : 'bg-[radial-gradient(circle_at_center,rgba(99,102,241,0.09)_0,transparent_70%)]'
        }`}
      />

      <motion.div
        initial={{ opacity: 0, y: 20, scale: 0.96 }}
        animate={{ opacity: 1, y: 0, scale: 1 }}
        transition={{ duration: 0.5, ease: 'easeOut' }}
        className="w-full max-w-xl bg-white/95 border border-slate-200/90 rounded-3xl p-8 shadow-premium-hover backdrop-blur-2xl relative z-10 flex flex-col items-center text-center"
      >
        {/* Outcome Header Banner */}
        <motion.div
          initial={{ scale: 0.5, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          transition={{ type: 'spring', damping: 12, stiffness: 150, delay: 0.1 }}
          className="mb-4"
        >
          {isWin && (
            <div className="inline-flex flex-col items-center">
              <div className="w-20 h-20 rounded-3xl bg-gradient-to-tr from-amber-400 to-yellow-300 p-0.5 shadow-xl shadow-amber-500/20 mb-3 flex items-center justify-center">
                <div className="w-full h-full bg-white rounded-3xl flex items-center justify-center">
                  <Trophy className="w-10 h-10 text-amber-500 animate-bounce" />
                </div>
              </div>
              <h1 className="text-4xl font-black tracking-tight text-slate-900">VICTORY</h1>
              <span className="text-amber-600 font-bold text-xs mt-1 uppercase tracking-widest">
                Winner of the Arena
              </span>
            </div>
          )}

          {isLoss && (
            <div className="inline-flex flex-col items-center">
              <div className="w-20 h-20 rounded-3xl bg-rose-50 border border-rose-200 p-0.5 mb-3 flex items-center justify-center shadow-sm">
                <Shield className="w-10 h-10 text-rose-500" />
              </div>
              <h1 className="text-4xl font-black tracking-tight text-slate-900">DEFEAT</h1>
              <span className="text-slate-500 font-semibold text-xs mt-1 uppercase tracking-widest">
                Good effort, warrior
              </span>
            </div>
          )}

          {isDraw && (
            <div className="inline-flex flex-col items-center">
              <div className="w-20 h-20 rounded-3xl bg-indigo-50 border border-indigo-200 p-0.5 mb-3 flex items-center justify-center shadow-sm">
                <Swords className="w-10 h-10 text-indigo-600" />
              </div>
              <h1 className="text-4xl font-black tracking-tight text-slate-900">DRAW</h1>
              <span className="text-indigo-600 font-semibold text-xs mt-1 uppercase tracking-widest">
                Evenly matched
              </span>
            </div>
          )}
        </motion.div>

        {/* Challenge details */}
        <div className="text-slate-500 text-xs mb-6 flex items-center gap-2">
          <span>Kata: <strong className="text-slate-800">{result.challengeTitle}</strong></span>
          <span>•</span>
          <span className="flex items-center gap-1">
            <Clock className="w-3.5 h-3.5" />
            {formatDuration(result.durationSeconds)}
          </span>
          {result.finishReason && (
            <>
              <span>•</span>
              <span className="text-slate-600 font-mono">[{result.finishReason}]</span>
            </>
          )}
        </div>

        {/* Rewards Box */}
        <div className="w-full grid grid-cols-2 gap-4 mb-6">
          {/* MMR Change */}
          <div className="bg-slate-50/80 border border-slate-200/80 rounded-2xl p-4 flex flex-col items-center shadow-2xs">
            <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider mb-1">
              Rating Change
            </span>
            <div
              className={`flex items-center gap-1.5 text-2xl font-black ${
                result.ratingDelta > 0
                  ? 'text-emerald-600'
                  : result.ratingDelta < 0
                  ? 'text-rose-600'
                  : 'text-slate-700'
              }`}
            >
              {result.ratingDelta > 0 ? (
                <TrendingUp className="w-5 h-5" />
              ) : result.ratingDelta < 0 ? (
                <TrendingDown className="w-5 h-5" />
              ) : (
                <Minus className="w-5 h-5" />
              )}
              <span>{result.ratingDelta > 0 ? `+${result.ratingDelta}` : result.ratingDelta}</span>
            </div>
            <span className="text-[11px] text-slate-500 mt-1">
              New MMR: <strong className="text-slate-800 font-mono">{result.newRating}</strong>
            </span>
          </div>

          {/* XP Earned */}
          <div className="bg-slate-50/80 border border-slate-200/80 rounded-2xl p-4 flex flex-col items-center shadow-2xs">
            <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider mb-1">
              XP Earned
            </span>
            <div className="flex items-center gap-1 text-2xl font-black text-amber-500">
              <Sparkles className="w-5 h-5" />
              <span>+{result.xpEarned} XP</span>
            </div>
            <span className="text-[11px] text-slate-500 mt-1">
              {isWin ? 'Victory Bounty' : isDraw ? 'Draw Honorarium' : 'Participation Reward'}
            </span>
          </div>
        </div>

        {/* Opponent Card Summary */}
        <div className="w-full bg-slate-50 border border-slate-200/80 rounded-2xl p-3 flex items-center justify-between text-xs mb-8">
          <span className="text-slate-500">Opponent:</span>
          <div className="flex items-center gap-2">
            <span className="font-bold text-slate-900">{result.opponentUsername}</span>
            <span className="text-slate-500 font-mono">({result.opponentRating} MMR)</span>
            <span
              className={`font-semibold ${
                result.opponentRatingDelta > 0 ? 'text-emerald-600' : 'text-rose-600'
              }`}
            >
              {result.opponentRatingDelta > 0 ? `+${result.opponentRatingDelta}` : result.opponentRatingDelta}
            </span>
          </div>
        </div>

        {/* Navigation Action Buttons */}
        <div className="w-full grid grid-cols-2 gap-3">
          <Button
            variant="outline"
            onClick={() => navigate('/home')}
            className="w-full py-3 border-slate-200 hover:bg-slate-50 text-slate-700 rounded-2xl flex items-center justify-center gap-2 shadow-2xs"
          >
            <Home className="w-4 h-4" />
            <span>Return to HQ</span>
          </Button>

          <Button
            variant="premium"
            onClick={() => navigate('/matchmaking')}
            className="w-full py-3 text-white font-bold rounded-2xl flex items-center justify-center gap-2"
          >
            <span>Play Again</span>
            <ArrowRight className="w-4 h-4" />
          </Button>
        </div>
      </motion.div>
    </div>
  );
};

export default BattleResultPage;
