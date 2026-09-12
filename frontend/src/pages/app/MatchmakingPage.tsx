import React, { useEffect, useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { Swords, Trophy, XCircle, Zap } from 'lucide-react';
import { battleService } from '../../services/battleService';
import { MatchFoundPayload } from '../../types/battle';
import { webSocketService } from '../../services/webSocketService';
import { useAuth } from '../../contexts/AuthContext';
import { Button } from '../../components/ui/Button';

export const MatchmakingPage: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();

  const [elapsedSeconds, setElapsedSeconds] = useState(0);
  const [searchRadius, setSearchRadius] = useState(150);
  const [matchFound, setMatchFound] = useState<MatchFoundPayload | null>(null);
  const [error, setError] = useState<string | null>(null);

  const timerRef = useRef<any>(null);
  const pollRef = useRef<any>(null);

  const playerRating = user?.stats?.rating || 1000;

  useEffect(() => {
    let isSubscribed = true;

    // 1. Join matchmaking queue and subscribe to STOMP events
    const startMatchmaking = async () => {
      try {
        await battleService.joinMatchmaking();

        // Connect STOMP if not already connected
        await webSocketService.connect();

        // Subscribe to user private matchmaking queue
        const unsubscribe = webSocketService.subscribe('/user/queue/matchmaking', (event: any) => {
          if (isSubscribed) {
            handleMatchEvent(event);
          }
        });

        // Also subscribe to broadcast topic for fallback
        let unsubscribeTopic = () => {};
        if (user?.id) {
          unsubscribeTopic = webSocketService.subscribe(`/topic/matchmaking.${user.id}`, (event: any) => {
            if (isSubscribed) {
              handleMatchEvent(event);
            }
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
      isSubscribed = false;
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
    <div className="min-h-screen bg-transparent flex flex-col items-center justify-center p-4 relative overflow-hidden">
      {/* Background Atmosphere - Luminous Light Jewels */}
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[650px] h-[650px] bg-gradient-to-tr from-indigo-500/10 via-cyan-500/10 to-transparent rounded-full blur-3xl pointer-events-none" />

      {/* Main Luxury Light Card */}
      <motion.div
        initial={{ opacity: 0, scale: 0.96 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.3 }}
        className="w-full max-w-lg bg-white/95 border border-slate-200/90 rounded-3xl p-8 sm:p-10 shadow-premium-hover backdrop-blur-2xl relative z-10 flex flex-col items-center text-center shimmer-card"
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
              {/* Radar Pulsing Radar Rings */}
              <div className="relative w-36 h-36 flex items-center justify-center mb-6">
                <motion.div
                  animate={{ scale: [1, 1.4, 1.8], opacity: [0.6, 0.25, 0] }}
                  transition={{ duration: 2.5, repeat: Infinity, ease: 'easeOut' }}
                  className="absolute inset-0 rounded-full border-2 border-indigo-500/40"
                />
                <motion.div
                  animate={{ scale: [1, 1.25, 1.5], opacity: [0.8, 0.3, 0] }}
                  transition={{ duration: 2.5, delay: 0.6, repeat: Infinity, ease: 'easeOut' }}
                  className="absolute inset-0 rounded-full border-2 border-cyan-400/40"
                />
                <div className="w-24 h-24 rounded-full bg-gradient-to-tr from-indigo-600 via-cyan-600 to-violet-600 p-0.5 shadow-lg shadow-indigo-500/25 flex items-center justify-center">
                  <div className="w-full h-full rounded-full bg-white flex items-center justify-center">
                    <Swords className="w-10 h-10 text-indigo-600 animate-pulse" />
                  </div>
                </div>
              </div>

              {/* Status Headings */}
              <h2 className="text-2xl font-black tracking-tight text-slate-900 mb-1">
                FINDING OPPONENT
              </h2>
              <p className="text-slate-500 text-sm mb-6 font-medium">
                Searching for a competitor near your skill bracket...
              </p>

              {/* Stats Strip */}
              <div className="w-full grid grid-cols-3 gap-3 mb-8">
                <div className="bg-slate-50/80 border border-slate-200/80 rounded-2xl p-3 flex flex-col items-center shadow-2xs">
                  <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-1 font-mono">Your MMR</span>
                  <div className="flex items-center gap-1.5 text-amber-600 font-black text-lg">
                    <Trophy className="w-4 h-4 text-amber-500" />
                    <span>{playerRating}</span>
                  </div>
                </div>

                <div className="bg-slate-50/80 border border-slate-200/80 rounded-2xl p-3 flex flex-col items-center shadow-2xs">
                  <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-1 font-mono">Queue Time</span>
                  <span className="text-indigo-600 font-mono font-black text-lg">
                    {formatTime(elapsedSeconds)}
                  </span>
                </div>

                <div className="bg-slate-50/80 border border-slate-200/80 rounded-2xl p-3 flex flex-col items-center shadow-2xs">
                  <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-1 font-mono">Range</span>
                  <span className="text-emerald-600 font-black text-lg font-mono">
                    ±{searchRadius}
                  </span>
                </div>
              </div>

              {error && (
                <div className="w-full mb-4 p-3 bg-rose-50 border border-rose-200 rounded-xl text-rose-700 text-sm font-medium">
                  {error}
                </div>
              )}

              {/* Cancel Button */}
              <Button
                variant="outline"
                size="md"
                onClick={handleCancel}
                className="w-full py-3 text-slate-700 hover:text-slate-900 rounded-xl flex items-center justify-center gap-2 font-bold"
              >
                <XCircle className="w-4 h-4 text-slate-400" />
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
              <div className="w-20 h-20 rounded-2xl bg-emerald-50 border border-emerald-200 flex items-center justify-center mb-4 text-emerald-600 shadow-md shadow-emerald-500/15">
                <Zap className="w-10 h-10 animate-bounce" />
              </div>

              <h2 className="text-3xl font-black text-slate-900 tracking-tight mb-2">
                MATCH FOUND!
              </h2>
              <p className="text-slate-600 text-sm mb-6">
                Challenge: <strong className="text-indigo-600 font-bold">{matchFound.challengeTitle}</strong> ({matchFound.difficulty})
              </p>

              <div className="w-full bg-slate-50/90 rounded-2xl p-4 border border-slate-200/90 flex items-center justify-around mb-6 shadow-2xs">
                <div className="flex flex-col items-center">
                  <div className="w-12 h-12 rounded-2xl bg-gradient-to-tr from-indigo-600 to-cyan-600 flex items-center justify-center text-white font-black text-lg mb-1 shadow-sm shadow-indigo-500/20">
                    {matchFound.player1.username[0]?.toUpperCase()}
                  </div>
                  <span className="text-slate-900 font-bold text-sm">{matchFound.player1.username}</span>
                  <span className="text-xs text-amber-600 font-bold font-mono">{matchFound.player1.rating} MMR</span>
                </div>

                <span className="text-slate-400 font-black text-xl italic font-mono">VS</span>

                <div className="flex flex-col items-center">
                  <div className="w-12 h-12 rounded-2xl bg-gradient-to-tr from-rose-600 to-orange-600 flex items-center justify-center text-white font-black text-lg mb-1 shadow-sm shadow-rose-500/20">
                    {matchFound.player2.username[0]?.toUpperCase()}
                  </div>
                  <span className="text-slate-900 font-bold text-sm">{matchFound.player2.username}</span>
                  <span className="text-xs text-amber-600 font-bold font-mono">{matchFound.player2.rating} MMR</span>
                </div>
              </div>

              <div className="text-indigo-600 font-semibold text-xs animate-pulse font-mono">
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
