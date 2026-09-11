import React, { useEffect, useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { Swords, XCircle, Trophy, Zap } from 'lucide-react';
import { battleService } from '../../services/battleService';
import { webSocketService } from '../../services/webSocketService';
import { useAuth } from '../../contexts/AuthContext';
import { Button } from '../../components/ui/Button';

export const MatchmakingPage: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();

  const [elapsedSeconds, setElapsedSeconds] = useState(0);
  const [searchRadius, setSearchRadius] = useState(150);
  const [playerRating, setPlayerRating] = useState(user?.stats?.rating || 1000);
  const [matchFound, setMatchFound] = useState<any | null>(null);
  const [error, setError] = useState<string | null>(null);

  const timerRef = useRef<NodeJS.Timeout | null>(null);
  const pollRef = useRef<NodeJS.Timeout | null>(null);

  useEffect(() => {
    // 1. Join matchmaking queue
    const startMatchmaking = async () => {
      try {
        setError(null);
        const status = await battleService.joinMatchmaking();
        setPlayerRating(status.playerRating || user?.stats?.rating || 1000);
        setSearchRadius(status.searchRadius || 150);

        if (status.matchedBattleId) {
          navigate(`/battle/${status.matchedBattleId}`);
          return;
        }

        // Connect WebSocket and subscribe
        await webSocketService.connect();

        // Subscribe to private match events
        const unsubscribe = webSocketService.subscribe('/user/queue/match', (event) => {
          handleMatchEvent(event);
        });

        // Also subscribe to topic fallback by user ID if available
        let unsubscribeTopic = () => {};
        if (user?.id) {
          unsubscribeTopic = webSocketService.subscribe(`/topic/match.${user.id}`, (event) => {
            handleMatchEvent(event);
          });
        }

        return () => {
          unsubscribe();
          unsubscribeTopic();
        };
      } catch (err: any) {
        setError(err.message || 'Failed to enter matchmaking queue.');
      }
    };

    const cleanupSub = startMatchmaking();

    // 2. Start timer
    timerRef.current = setInterval(() => {
      setElapsedSeconds((prev) => {
        const next = prev + 1;
        // Expand search radius every 5 seconds
        setSearchRadius(Math.min(600, 150 + Math.floor(next / 5) * 50));
        return next;
      });
    }, 1000);

    // 3. Fallback status poller every 2 seconds
    pollRef.current = setInterval(async () => {
      try {
        const status = await battleService.getMatchmakingStatus();
        if (status.matchedBattleId) {
          navigate(`/battle/${status.matchedBattleId}`);
        }
      } catch {
        // Ignore background polling errors
      }
    }, 2000);

    return () => {
      if (timerRef.current) clearInterval(timerRef.current);
      if (pollRef.current) clearInterval(pollRef.current);
      cleanupSub.then((cleanup) => cleanup && cleanup());
    };
  }, [navigate, user?.id, user?.stats?.rating]);

  const handleMatchEvent = (event: any) => {
    if (event.type === 'MATCH_FOUND') {
      setMatchFound(event.payload);
      setTimeout(() => {
        navigate(`/battle/${event.payload.battleId}`);
      }, 1500);
    }
  };

  const handleCancel = async () => {
    try {
      await battleService.leaveMatchmaking();
    } catch {
      // ignore
    }
    navigate('/home');
  };

  const formatTime = (secs: number) => {
    const m = Math.floor(secs / 60);
    const s = secs % 60;
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  };

  return (
    <div className="min-h-screen bg-slate-950 flex flex-col items-center justify-center p-4 relative overflow-hidden">
      {/* Background Animated Atmosphere */}
      <div className="absolute inset-0 bg-[radial-gradient(circle_at_center,rgba(59,130,246,0.08)_0,transparent_70%)] pointer-events-none" />
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-indigo-500/5 rounded-full blur-3xl pointer-events-none" />

      {/* Main Card */}
      <motion.div
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        className="w-full max-w-lg bg-slate-900/90 border border-slate-800 rounded-3xl p-8 shadow-2xl backdrop-blur-xl relative z-10 flex flex-col items-center text-center"
      >
        <AnimatePresence mode="wait">
          {!matchFound ? (
            <motion.div
              key="searching"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0, scale: 0.9 }}
              className="w-full flex flex-col items-center"
            >
              {/* Pulsing Radar Ring */}
              <div className="relative w-36 h-36 flex items-center justify-center mb-6">
                <motion.div
                  animate={{ scale: [1, 1.4, 1.8], opacity: [0.6, 0.3, 0] }}
                  transition={{ duration: 2.5, repeat: Infinity, ease: 'easeOut' }}
                  className="absolute inset-0 rounded-full border-2 border-indigo-500/50"
                />
                <motion.div
                  animate={{ scale: [1, 1.25, 1.5], opacity: [0.8, 0.4, 0] }}
                  transition={{ duration: 2.5, delay: 0.6, repeat: Infinity, ease: 'easeOut' }}
                  className="absolute inset-0 rounded-full border-2 border-blue-400/40"
                />
                <div className="w-24 h-24 rounded-full bg-gradient-to-tr from-indigo-600 to-blue-500 p-0.5 shadow-lg shadow-indigo-500/30 flex items-center justify-center">
                  <div className="w-full h-full rounded-full bg-slate-900 flex items-center justify-center">
                    <Swords className="w-10 h-10 text-indigo-400 animate-pulse" />
                  </div>
                </div>
              </div>

              {/* Status Headings */}
              <h2 className="text-2xl font-black tracking-tight text-white mb-1">
                FINDING OPPONENT
              </h2>
              <p className="text-slate-400 text-sm mb-6">
                Searching for a competitor near your skill bracket...
              </p>

              {/* Stats Strip */}
              <div className="w-full grid grid-cols-3 gap-3 mb-8">
                <div className="bg-slate-800/60 border border-slate-750 rounded-2xl p-3 flex flex-col items-center">
                  <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-1">Your Rating</span>
                  <div className="flex items-center gap-1.5 text-amber-400 font-bold text-lg">
                    <Trophy className="w-4 h-4" />
                    <span>{playerRating}</span>
                  </div>
                </div>

                <div className="bg-slate-800/60 border border-slate-750 rounded-2xl p-3 flex flex-col items-center">
                  <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-1">Queue Time</span>
                  <span className="text-indigo-400 font-mono font-bold text-lg">
                    {formatTime(elapsedSeconds)}
                  </span>
                </div>

                <div className="bg-slate-800/60 border border-slate-750 rounded-2xl p-3 flex flex-col items-center">
                  <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-1">Range</span>
                  <span className="text-emerald-400 font-bold text-lg">
                    ±{searchRadius}
                  </span>
                </div>
              </div>

              {error && (
                <div className="w-full mb-4 p-3 bg-rose-500/10 border border-rose-500/20 rounded-xl text-rose-400 text-sm">
                  {error}
                </div>
              )}

              {/* Cancel Button */}
              <Button
                variant="outline"
                onClick={handleCancel}
                className="w-full py-3 border-slate-700 hover:bg-slate-800 text-slate-300 rounded-xl flex items-center justify-center gap-2"
              >
                <XCircle className="w-4 h-4" />
                <span>CANCEL SEARCH</span>
              </Button>
            </motion.div>
          ) : (
            /* Match Found Announcement */
            <motion.div
              key="match-found"
              initial={{ scale: 0.8, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              className="w-full flex flex-col items-center py-4"
            >
              <div className="w-20 h-20 rounded-2xl bg-emerald-500/20 border border-emerald-500/40 flex items-center justify-center mb-4 text-emerald-400 shadow-xl shadow-emerald-500/20">
                <Zap className="w-10 h-10 animate-bounce" />
              </div>

              <h2 className="text-3xl font-black text-white tracking-tight mb-2">
                MATCH FOUND!
              </h2>
              <p className="text-slate-300 text-sm mb-6">
                Challenge: <strong className="text-indigo-400 font-semibold">{matchFound.challengeTitle}</strong> ({matchFound.difficulty})
              </p>

              <div className="w-full bg-slate-800/80 rounded-2xl p-4 border border-slate-700/80 flex items-center justify-around mb-6">
                <div className="flex flex-col items-center">
                  <div className="w-12 h-12 rounded-xl bg-indigo-600 flex items-center justify-center text-white font-bold text-lg mb-1">
                    {matchFound.player1.username[0]?.toUpperCase()}
                  </div>
                  <span className="text-white font-medium text-sm">{matchFound.player1.username}</span>
                  <span className="text-xs text-amber-400 font-semibold">{matchFound.player1.rating} MMR</span>
                </div>

                <span className="text-slate-500 font-black text-xl italic">VS</span>

                <div className="flex flex-col items-center">
                  <div className="w-12 h-12 rounded-xl bg-rose-600 flex items-center justify-center text-white font-bold text-lg mb-1">
                    {matchFound.player2.username[0]?.toUpperCase()}
                  </div>
                  <span className="text-white font-medium text-sm">{matchFound.player2.username}</span>
                  <span className="text-xs text-amber-400 font-semibold">{matchFound.player2.rating} MMR</span>
                </div>
              </div>

              <div className="text-slate-400 text-xs animate-pulse">
                Entering battle arena lobby...
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </motion.div>
    </div>
  );
};

export default MatchmakingPage;
