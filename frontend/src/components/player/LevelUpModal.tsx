import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Trophy, Sparkles, Flame, ArrowRight } from 'lucide-react';
import { Button } from '../ui/Button';

interface LevelUpModalProps {
  isOpen: boolean;
  level: number;
  prevLevel: number;
  xpEarned: number;
  onClose: () => void;
}

export const LevelUpModal: React.FC<LevelUpModalProps> = ({
  isOpen,
  level,
  prevLevel,
  xpEarned,
  onClose,
}) => {
  return (
    <AnimatePresence>
      {isOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
          {/* Backdrop */}
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-slate-950/75 backdrop-blur-md"
            onClick={onClose}
          />

          {/* Modal Container */}
          <motion.div
            initial={{ opacity: 0, scale: 0.85, y: 20 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.9, y: 10 }}
            transition={{ type: 'spring', damping: 20, stiffness: 300 }}
            className="relative z-10 w-full max-w-md bg-gradient-to-b from-slate-900 via-slate-900 to-slate-950 border border-cyan-500/40 rounded-3xl p-8 text-center text-white shadow-2xl overflow-hidden"
          >
            {/* Ambient Background Glow */}
            <div className="absolute -top-24 -left-24 w-64 h-64 bg-cyan-500/25 rounded-full blur-3xl pointer-events-none" />
            <div className="absolute -bottom-24 -right-24 w-64 h-64 bg-violet-500/25 rounded-full blur-3xl pointer-events-none" />

            {/* Icon Banner */}
            <div className="relative mx-auto w-24 h-24 mb-6">
              <motion.div
                animate={{ rotate: 360 }}
                transition={{ duration: 12, repeat: Infinity, ease: 'linear' }}
                className="absolute inset-0 rounded-full bg-gradient-to-tr from-cyan-500 via-violet-500 to-amber-400 opacity-30 blur-md"
              />
              <div className="relative w-full h-full rounded-2xl bg-gradient-to-tr from-cyan-600 to-violet-600 border border-white/20 flex items-center justify-center shadow-xl">
                <Trophy className="w-12 h-12 text-amber-300 animate-bounce" />
              </div>
            </div>

            {/* Title */}
            <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-mono font-bold uppercase tracking-widest bg-amber-400/15 text-amber-300 border border-amber-400/30 mb-3">
              <Sparkles className="w-3.5 h-3.5 text-amber-400" />
              RANK ADVANCEMENT
            </div>

            <h2 className="text-3xl font-black tracking-tight text-white mb-2">
              LEVEL UP!
            </h2>

            <p className="text-sm text-slate-300 mb-6">
              Your algorithmic prowess has elevated you to the next tier of competitors.
            </p>

            {/* Level Transition Pill */}
            <div className="p-4 bg-slate-800/80 border border-slate-700/80 rounded-2xl mb-6 flex items-center justify-around font-mono">
              <div>
                <p className="text-[10px] text-slate-400 uppercase font-semibold">PREVIOUS</p>
                <p className="text-xl font-black text-slate-400">Lvl {prevLevel}</p>
              </div>

              <div className="w-8 h-8 rounded-full bg-cyan-500/20 text-cyan-400 flex items-center justify-center">
                <ArrowRight className="w-4 h-4" />
              </div>

              <div>
                <p className="text-[10px] text-cyan-300 uppercase font-semibold">NEW RANK</p>
                <p className="text-2xl font-black text-cyan-400 flex items-center justify-center gap-1">
                  Lvl {level}
                  <Flame className="w-4 h-4 text-amber-400 fill-amber-400" />
                </p>
              </div>
            </div>

            {/* XP Bonus */}
            {xpEarned > 0 && (
              <p className="text-xs font-mono text-emerald-400 font-semibold mb-6">
                +{xpEarned} XP Awarded to Player Vault
              </p>
            )}

            {/* Action */}
            <Button
              variant="glow"
              size="lg"
              className="w-full"
              onClick={onClose}
            >
              CONTINUE IN THE ARENA
            </Button>
          </motion.div>
        </div>
      )}
    </AnimatePresence>
  );
};
