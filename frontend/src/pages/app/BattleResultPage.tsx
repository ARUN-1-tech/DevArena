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
      <div className="min-h-screen bg-slate-950 flex flex-col items-center justify-center text-white">
        <Loader2 className="w-10 h-10 text-indigo-500 animate-spin mb-4" />
        <p className="text-slate-400 font-medium">Calculating Battle Rewards...</p>
      </div>
    );
  }

  if (error || !result) {
    return (
      <div className="min-h-screen bg-slate-950 flex flex-col items-center justify-center p-4">
        <div className="bg-slate-900 border border-slate-800 rounded-3xl p-8 max-w-md w-full text-center">
          <h2 className="text-xl font-bold text-white mb-2">Notice</h2>
          <p className="text-slate-400 text-sm mb-6">{error || 'Result not found.'}</p>
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
    <div className="min-h-screen bg-sandwich-950 flex flex-col items-center justify-center p-4 relative overflow-hidden">
      {/* Dynamic Background Glow */}
      <div
        className={`absolute inset-0 pointer-events-none ${
          isWin
            ? 'bg-[radial-gradient(circle_at_center,rgba(255,255,255,0.05)_0,transparent_70%)]'
            : isLoss
            ? 'bg-[radial-gradient(circle_at_center,rgba(239,68,68,0.06)_0,transparent_70%)]'
            : 'bg-[radial-gradient(circle_at_center,rgba(255,255,255,0.03)_0,transparent_70%)]'
        }`}
      />

      <motion.div
        initial={{ opacity: 0, y: 20, scale: 0.96 }}
        animate={{ opacity: 1, y: 0, scale: 1 }}
        transition={{ duration: 0.5, ease: 'easeOut' }}
        className="w-full max-w-xl bg-sandwich-900/90 border border-sandwich-700/80 rounded-3xl p-8 shadow-luxury-card backdrop-blur-xl relative z-10 flex flex-col items-center text-center"
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
              <div className="w-20 h-20 rounded-3xl bg-sandwich-800 border border-sandwich-500 p-0.5 shadow-glow-white mb-3 flex items-center justify-center">
                <div className="w-full h-full bg-sandwich-950 rounded-3xl flex items-center justify-center">
                  <Trophy className="w-10 h-10 text-sandwich-50 animate-bounce" />
                </div>
              </div>
              <h1 className="text-4xl font-black tracking-tight text-sandwich-50">VICTORY</h1>
              <span className="text-sandwich-300 font-semibold text-xs mt-1 uppercase tracking-widest">
                Winner of the Arena
              </span>
            </div>
          )}

          {isLoss && (
            <div className="inline-flex flex-col items-center">
              <div className="w-20 h-20 rounded-3xl bg-sandwich-800 border border-sandwich-700 p-0.5 mb-3 flex items-center justify-center">
                <Shield className="w-10 h-10 text-sandwich-400" />
              </div>
              <h1 className="text-4xl font-black tracking-tight text-sandwich-200">DEFEAT</h1>
              <span className="text-sandwich-400 font-semibold text-xs mt-1 uppercase tracking-widest">
                Good effort, warrior
              </span>
            </div>
          )}

          {isDraw && (
            <div className="inline-flex flex-col items-center">
              <div className="w-20 h-20 rounded-3xl bg-sandwich-800 border border-sandwich-600 mb-3 flex items-center justify-center">
                <Swords className="w-10 h-10 text-sandwich-200" />
              </div>
              <h1 className="text-4xl font-black tracking-tight text-sandwich-100">DRAW</h1>
              <span className="text-sandwich-400 font-semibold text-xs mt-1 uppercase tracking-widest">
                Evenly matched
              </span>
            </div>
          )}
        </motion.div>

        {/* Challenge details */}
        <div className="text-sandwich-400 text-xs mb-6 flex items-center gap-2">
          <span>Kata: <strong className="text-white">{result.challengeTitle}</strong></span>
          <span>•</span>
          <span className="flex items-center gap-1">
            <Clock className="w-3.5 h-3.5" />
            {formatDuration(result.durationSeconds)}
          </span>
          {result.finishReason && (
            <>
              <span>•</span>
              <span className="text-sandwich-300 font-mono">[{result.finishReason}]</span>
            </>
          )}
        </div>

        {/* Rewards Box */}
        <div className="w-full grid grid-cols-2 gap-4 mb-8">
          {/* MMR Change */}
          <div className="bg-sandwich-950/80 border border-sandwich-800 rounded-2xl p-4 flex flex-col items-center">
            <span className="text-xs font-semibold text-sandwich-400 uppercase tracking-wider mb-1">
              Rating Change
            </span>
            <div
              className={`flex items-center gap-1.5 text-2xl font-black ${
                result.ratingDelta > 0
                  ? 'text-emerald-400'
                  : result.ratingDelta < 0
                  ? 'text-rose-400'
                  : 'text-sandwich-300'
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
            <span className="text-[11px] text-sandwich-400 mt-1">
              New MMR: <strong className="text-white font-mono">{result.newRating}</strong>
            </span>
          </div>

          {/* XP Earned */}
          <div className="bg-sandwich-950/80 border border-sandwich-800 rounded-2xl p-4 flex flex-col items-center">
            <span className="text-xs font-semibold text-sandwich-400 uppercase tracking-wider mb-1">
              XP Earned
            </span>
            <div className="flex items-center gap-1 text-2xl font-black text-sandwich-100">
              <Sparkles className="w-5 h-5 text-sandwich-200" />
              <span>+{result.xpEarned} XP</span>
            </div>
            <span className="text-[11px] text-sandwich-400 mt-1">
              {isWin ? 'Victory Bounty' : isDraw ? 'Draw Honorarium' : 'Participation Reward'}
            </span>
          </div>
        </div>

        {/* Opponent Card Summary */}
        <div className="w-full bg-sandwich-950/80 border border-sandwich-800 rounded-xl p-3 flex items-center justify-between text-xs mb-8">
          <span className="text-sandwich-400">Opponent:</span>
          <div className="flex items-center gap-2">
            <span className="font-bold text-white">{result.opponentUsername}</span>
            <span className="text-sandwich-400 font-mono">({result.opponentRating} MMR)</span>
            <span
              className={`font-semibold ${
                result.opponentRatingDelta > 0 ? 'text-emerald-400' : 'text-rose-400'
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
            className="w-full py-3 border-sandwich-700 hover:bg-sandwich-800 text-sandwich-200 rounded-xl flex items-center justify-center gap-2"
          >
            <Home className="w-4 h-4" />
            <span>Return to HQ</span>
          </Button>

          <Button
            variant="primary"
            onClick={() => navigate('/matchmaking')}
            className="w-full py-3 font-bold rounded-xl flex items-center justify-center gap-2"
          >
            <span>Play Again</span>
            <ArrowRight className="w-4 h-4 text-sandwich-950" />
          </Button>
        </div>
      </motion.div>
    </div>
  );
};

export default BattleResultPage;
