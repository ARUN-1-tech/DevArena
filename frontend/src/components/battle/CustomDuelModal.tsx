import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Swords,
  X,
  Copy,
  Check,
  Clock,
  KeyRound,
  Loader2,
  Sparkles,
  Zap,
  Flame,
  AlertCircle,
} from 'lucide-react';
import { Button } from '../ui/Button';
import { Input } from '../ui/Input';
import { customDuelService } from '../../services/customDuelService';
import { webSocketService } from '../../services/webSocketService';
import { CustomDuelRoom } from '../../types/social';

interface CustomDuelModalProps {
  isOpen: boolean;
  onClose: () => void;
  initialTab?: 'create' | 'join';
}

export const CustomDuelModal: React.FC<CustomDuelModalProps> = ({
  isOpen,
  onClose,
  initialTab = 'create',
}) => {
  const navigate = useNavigate();
  const [tab, setTab] = useState<'create' | 'join'>(initialTab);

  // Create room options
  const [difficulty, setDifficulty] = useState<'ANY' | 'EASY' | 'MEDIUM' | 'HARD'>('ANY');
  const [durationMinutes, setDurationMinutes] = useState<number>(15);
  const [createdRoom, setCreatedRoom] = useState<CustomDuelRoom | null>(null);
  const [creating, setCreating] = useState(false);
  const [copiedCode, setCopiedCode] = useState(false);

  // Join room state
  const [joinCode, setJoinCode] = useState('');
  const [joining, setJoining] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  useEffect(() => {
    setTab(initialTab);
    setErrorMessage(null);
  }, [initialTab, isOpen]);

  // Host waiting room listener and poller
  useEffect(() => {
    if (!createdRoom || !isOpen) return;

    const roomCode = createdRoom.roomCode;

    // 1. Poll room status every 2 seconds
    const pollInterval = setInterval(async () => {
      try {
        const room = await customDuelService.getRoom(roomCode);
        if (room && room.status === 'STARTED' && room.battleId) {
          clearInterval(pollInterval);
          onClose();
          navigate(`/battle/${room.battleId}`);
        }
      } catch (err) {
        console.debug('Failed to poll room status', err);
      }
    }, 2000);

    // 2. Subscribe to room WebSocket topic
    const unsub = webSocketService.subscribe(`/topic/custom-room.${roomCode}`, (event: any) => {
      if (event.type === 'ROOM_STARTED' && event.battleId) {
        clearInterval(pollInterval);
        onClose();
        navigate(`/battle/${event.battleId}`);
      }
    });

    return () => {
      clearInterval(pollInterval);
      if (unsub) unsub();
    };
  }, [createdRoom, isOpen, navigate, onClose]);

  const handleCreateRoom = async () => {
    try {
      setCreating(true);
      setErrorMessage(null);
      const room = await customDuelService.createRoom({
        difficulty: difficulty === 'ANY' ? undefined : difficulty,
        durationSeconds: durationMinutes * 60,
      });
      setCreatedRoom(room);
    } catch (err: any) {
      setErrorMessage(err.response?.data?.message || 'Failed to create custom duel room');
    } finally {
      setCreating(false);
    }
  };

  const handleJoinRoom = async (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    if (!joinCode.trim()) {
      setErrorMessage('Please enter a 6-digit room code');
      return;
    }
    try {
      setJoining(true);
      setErrorMessage(null);
      const room = await customDuelService.joinRoom(joinCode.trim());
      onClose();
      navigate(`/battle/${room.battleId}`);
    } catch (err: any) {
      setErrorMessage(err.response?.data?.message || 'Invalid or expired room code');
    } finally {
      setJoining(false);
    }
  };

  const handleCopyCode = () => {
    if (!createdRoom) return;
    navigator.clipboard.writeText(createdRoom.roomCode);
    setCopiedCode(true);
    setTimeout(() => setCopiedCode(false), 3000);
  };

  const handleCancelCreatedRoom = async () => {
    if (createdRoom) {
      try {
        await customDuelService.cancelRoom(createdRoom.roomCode);
      } catch (e) {
        console.debug('Error cancelling room', e);
      }
    }
    setCreatedRoom(null);
  };

  if (!isOpen) return null;

  return (
    <AnimatePresence>
      <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-sm">
        <motion.div
          initial={{ opacity: 0, scale: 0.95, y: 15 }}
          animate={{ opacity: 1, scale: 1, y: 0 }}
          exit={{ opacity: 0, scale: 0.95, y: 15 }}
          transition={{ type: 'spring', damping: 25, stiffness: 350 }}
          className="w-full max-w-lg bg-white rounded-3xl border border-slate-200/90 shadow-2xl overflow-hidden relative"
        >
          {/* Ambient header glow */}
          <div className="absolute top-0 right-0 w-48 h-48 bg-gradient-to-br from-violet-400/15 via-cyan-400/10 to-transparent rounded-full blur-3xl pointer-events-none" />

          {/* Header */}
          <div className="px-6 pt-6 pb-4 flex items-center justify-between border-b border-slate-100 relative z-10">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-2xl bg-gradient-to-tr from-violet-600 to-indigo-600 text-white flex items-center justify-center shadow-md shadow-violet-500/25">
                <Swords className="w-5 h-5" />
              </div>
              <div>
                <h3 className="text-lg font-black text-slate-900 tracking-tight">
                  Private Custom Duel
                </h3>
                <p className="text-xs text-slate-500 font-mono">
                  SYNCHRONIZED 1V1 ARENA SCRIMMAGE
                </p>
              </div>
            </div>

            <button
              onClick={() => {
                if (createdRoom) handleCancelCreatedRoom();
                onClose();
              }}
              className="p-2 rounded-xl text-slate-400 hover:text-slate-700 hover:bg-slate-100 transition-colors cursor-pointer"
            >
              <X className="w-5 h-5" />
            </button>
          </div>

          {/* Navigation Tabs */}
          {!createdRoom && (
            <div className="px-6 pt-4 flex gap-2 border-b border-slate-100 bg-slate-50/50">
              <button
                onClick={() => {
                  setTab('create');
                  setErrorMessage(null);
                }}
                className={`pb-3 px-3 text-xs font-bold transition-all border-b-2 cursor-pointer flex items-center gap-1.5 ${
                  tab === 'create'
                    ? 'border-violet-600 text-violet-700'
                    : 'border-transparent text-slate-500 hover:text-slate-800'
                }`}
              >
                <Sparkles className="w-3.5 h-3.5" />
                <span>CREATE ROOM</span>
              </button>

              <button
                onClick={() => {
                  setTab('join');
                  setErrorMessage(null);
                }}
                className={`pb-3 px-3 text-xs font-bold transition-all border-b-2 cursor-pointer flex items-center gap-1.5 ${
                  tab === 'join'
                    ? 'border-violet-600 text-violet-700'
                    : 'border-transparent text-slate-500 hover:text-slate-800'
                }`}
              >
                <KeyRound className="w-3.5 h-3.5" />
                <span>JOIN VIA CODE</span>
              </button>
            </div>
          )}

          {/* Body Content */}
          <div className="p-6 space-y-5 relative z-10">
            {errorMessage && (
              <div className="p-3 rounded-xl bg-rose-50 border border-rose-200 text-rose-800 text-xs font-semibold flex items-center gap-2">
                <AlertCircle className="w-4 h-4 text-rose-600 shrink-0" />
                <span>{errorMessage}</span>
              </div>
            )}

            {/* TAB 1: CREATE ROOM FORM */}
            {tab === 'create' && !createdRoom && (
              <div className="space-y-5">
                <div>
                  <label className="block text-xs font-extrabold uppercase text-slate-700 tracking-wider mb-2">
                    Challenge Difficulty
                  </label>
                  <div className="grid grid-cols-4 gap-2">
                    {[
                      { id: 'ANY', label: 'Any', icon: Zap, color: 'text-indigo-600' },
                      { id: 'EASY', label: 'Easy', icon: Sparkles, color: 'text-emerald-600' },
                      { id: 'MEDIUM', label: 'Medium', icon: Flame, color: 'text-amber-600' },
                      { id: 'HARD', label: 'Hard', icon: Swords, color: 'text-rose-600' },
                    ].map((d) => {
                      const Icon = d.icon;
                      const active = difficulty === d.id;
                      return (
                        <button
                          key={d.id}
                          type="button"
                          onClick={() => setDifficulty(d.id as any)}
                          className={`p-3 rounded-xl border text-center transition-all cursor-pointer ${
                            active
                              ? 'bg-violet-50 border-violet-500 shadow-xs'
                              : 'bg-white border-slate-200 hover:border-slate-300'
                          }`}
                        >
                          <Icon className={`w-4 h-4 mx-auto mb-1 ${active ? 'text-violet-600' : d.color}`} />
                          <span className={`text-xs font-bold block ${active ? 'text-violet-900' : 'text-slate-700'}`}>
                            {d.label}
                          </span>
                        </button>
                      );
                    })}
                  </div>
                </div>

                <div>
                  <label className="block text-xs font-extrabold uppercase text-slate-700 tracking-wider mb-2">
                    Match Duration
                  </label>
                  <div className="grid grid-cols-3 gap-2">
                    {[
                      { mins: 10, label: '10 Mins', sub: 'Blitz' },
                      { mins: 15, label: '15 Mins', sub: 'Standard' },
                      { mins: 20, label: '20 Mins', sub: 'Endurance' },
                    ].map((t) => {
                      const active = durationMinutes === t.mins;
                      return (
                        <button
                          key={t.mins}
                          type="button"
                          onClick={() => setDurationMinutes(t.mins)}
                          className={`p-3 rounded-xl border text-center transition-all cursor-pointer ${
                            active
                              ? 'bg-violet-50 border-violet-500 shadow-xs'
                              : 'bg-white border-slate-200 hover:border-slate-300'
                          }`}
                        >
                          <Clock className={`w-4 h-4 mx-auto mb-1 ${active ? 'text-violet-600' : 'text-slate-400'}`} />
                          <span className={`text-xs font-extrabold block ${active ? 'text-violet-900' : 'text-slate-800'}`}>
                            {t.label}
                          </span>
                          <span className="text-[10px] text-slate-400 font-mono">{t.sub}</span>
                        </button>
                      );
                    })}
                  </div>
                </div>

                <Button
                  variant="glow"
                  size="lg"
                  className="w-full font-black text-sm bg-gradient-to-r from-violet-600 via-indigo-600 to-cyan-600 hover:from-violet-700 hover:to-cyan-700 text-white shadow-lg shadow-violet-500/25"
                  isLoading={creating}
                  onClick={handleCreateRoom}
                  leftIcon={<Swords className="w-4 h-4" />}
                >
                  CREATE DUEL ROOM
                </Button>
              </div>
            )}

            {/* CREATED ROOM STATE (WAITING FOR OPPONENT) */}
            {tab === 'create' && createdRoom && (
              <div className="space-y-6 text-center py-2">
                {/* Radar animation */}
                <div className="relative w-24 h-24 mx-auto flex items-center justify-center">
                  <div className="absolute inset-0 rounded-full bg-violet-500/10 animate-ping" />
                  <div className="absolute inset-2 rounded-full bg-violet-500/15 animate-pulse" />
                  <div className="relative z-10 w-16 h-16 rounded-2xl bg-gradient-to-tr from-violet-600 to-indigo-600 text-white flex items-center justify-center shadow-lg shadow-violet-500/30">
                    <Swords className="w-8 h-8 animate-bounce" />
                  </div>
                </div>

                <div>
                  <h4 className="text-lg font-black text-slate-900">Room Ready for Combat!</h4>
                  <p className="text-xs text-slate-500 mt-1 max-w-sm mx-auto">
                    Share the 6-digit room code with your opponent. The match starts instantly once they enter.
                  </p>
                </div>

                {/* Room Code Card */}
                <div className="p-4 rounded-2xl bg-gradient-to-r from-violet-50/80 via-indigo-50/50 to-white border-2 border-violet-200/90 shadow-sm flex items-center justify-between gap-3">
                  <div className="text-left">
                    <span className="text-[10px] font-mono text-slate-400 block font-bold">ROOM CODE</span>
                    <span className="text-2xl font-black font-mono tracking-widest text-violet-900">
                      {createdRoom.roomCode}
                    </span>
                  </div>

                  <Button
                    size="sm"
                    variant="outline"
                    className="font-bold text-xs bg-white text-violet-700 border-violet-200 hover:bg-violet-50"
                    onClick={handleCopyCode}
                    leftIcon={copiedCode ? <Check className="w-3.5 h-3.5 text-emerald-600" /> : <Copy className="w-3.5 h-3.5" />}
                  >
                    {copiedCode ? 'COPIED!' : 'COPY CODE'}
                  </Button>
                </div>

                {/* Room Parameters */}
                <div className="grid grid-cols-2 gap-2 text-left text-xs bg-slate-50 p-3 rounded-xl border border-slate-100">
                  <div>
                    <span className="text-slate-400 text-[10px] block font-mono">CHALLENGE</span>
                    <span className="font-bold text-slate-800 truncate block">{createdRoom.challengeTitle}</span>
                  </div>
                  <div>
                    <span className="text-slate-400 text-[10px] block font-mono">TIME LIMIT</span>
                    <span className="font-bold text-slate-800">{createdRoom.durationSeconds / 60} Minutes</span>
                  </div>
                </div>

                <div className="flex items-center justify-center gap-2 text-xs text-slate-500 font-mono">
                  <Loader2 className="w-3.5 h-3.5 animate-spin text-violet-600" />
                  <span>Waiting for challenger to enter room...</span>
                </div>

                <Button
                  variant="outline"
                  size="sm"
                  className="w-full text-slate-500 border-slate-200 hover:bg-slate-100"
                  onClick={handleCancelCreatedRoom}
                >
                  Cancel Room
                </Button>
              </div>
            )}

            {/* TAB 2: JOIN VIA CODE */}
            {tab === 'join' && (
              <form onSubmit={handleJoinRoom} className="space-y-5">
                <div>
                  <label className="block text-xs font-extrabold uppercase text-slate-700 tracking-wider mb-2">
                    Enter 6-Digit Room Code
                  </label>
                  <Input
                    placeholder="e.g. 748291"
                    value={joinCode}
                    onChange={(e) => setJoinCode(e.target.value.toUpperCase())}
                    className="text-center font-mono font-black text-xl tracking-widest uppercase h-14 bg-slate-50 border-slate-200 focus:bg-white"
                    maxLength={10}
                    autoFocus
                  />
                  <p className="text-[11px] text-slate-400 mt-1.5 text-center font-mono">
                    Ask your friend or opponent for their custom duel code
                  </p>
                </div>

                <Button
                  type="submit"
                  variant="glow"
                  size="lg"
                  className="w-full font-black text-sm bg-gradient-to-r from-violet-600 via-indigo-600 to-cyan-600 hover:from-violet-700 hover:to-cyan-700 text-white shadow-lg shadow-violet-500/25"
                  isLoading={joining}
                  disabled={!joinCode.trim()}
                  leftIcon={<Swords className="w-4 h-4" />}
                >
                  JOIN DUEL & ENTER ARENA
                </Button>
              </form>
            )}
          </div>
        </motion.div>
      </div>
    </AnimatePresence>
  );
};
