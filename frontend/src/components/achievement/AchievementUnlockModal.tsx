import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Trophy, Sparkles, X } from 'lucide-react';
import { Achievement } from '../../types/progression';

interface AchievementUnlockModalProps {
  achievement: Achievement | null;
  onClose: () => void;
}

export const AchievementUnlockModal: React.FC<AchievementUnlockModalProps> = ({
  achievement,
  onClose,
}) => {
  if (!achievement) return null;

  return (
    <AnimatePresence>
      <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/60 backdrop-blur-sm">
        <motion.div
          initial={{ opacity: 0, scale: 0.85, y: 20 }}
          animate={{ opacity: 1, scale: 1, y: 0 }}
          exit={{ opacity: 0, scale: 0.9, y: 10 }}
          transition={{ type: 'spring', damping: 20, stiffness: 300 }}
          className="relative max-w-sm w-full bg-gradient-to-b from-slate-900 via-slate-900 to-slate-950 border-2 border-amber-400/80 rounded-2xl p-6 shadow-2xl text-center overflow-hidden"
        >
          {/* Subtle gold glow banner */}
          <div className="absolute -top-12 left-1/2 -translate-x-1/2 w-48 h-48 bg-amber-500/20 rounded-full blur-3xl pointer-events-none" />

          <button
            onClick={onClose}
            className="absolute top-3 right-3 text-slate-400 hover:text-white p-1 rounded-lg hover:bg-slate-800 transition-colors"
          >
            <X className="w-4 h-4" />
          </button>

          <motion.div
            initial={{ rotate: -20, scale: 0 }}
            animate={{ rotate: 0, scale: 1 }}
            transition={{ delay: 0.15, type: 'spring', stiffness: 200 }}
            className="w-20 h-20 mx-auto mb-4 rounded-2xl bg-gradient-to-tr from-amber-500 to-yellow-400 flex items-center justify-center shadow-lg shadow-amber-500/30 text-slate-950"
          >
            <Trophy className="w-10 h-10" />
          </motion.div>

          <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[11px] font-mono font-bold uppercase tracking-wider bg-amber-400/20 text-amber-300 border border-amber-400/30 mb-2">
            <Sparkles className="w-3 h-3 mr-0.5" />
            ACHIEVEMENT UNLOCKED
          </span>

          <h3 className="text-xl font-black text-white tracking-tight mt-1">
            {achievement.name}
          </h3>

          <p className="text-xs text-slate-300 mt-2 leading-relaxed">
            {achievement.description}
          </p>

          <div className="mt-4 pt-4 border-t border-slate-800 flex items-center justify-center gap-2">
            <span className="text-xs font-mono font-bold text-amber-400">
              +{achievement.xpReward} XP REWARD
            </span>
            <span className="text-xs text-slate-500">•</span>
            <span className="text-[11px] font-mono font-bold uppercase text-slate-400">
              {achievement.rarity}
            </span>
          </div>

          <button
            onClick={onClose}
            className="mt-5 w-full py-2.5 px-4 rounded-xl font-bold text-xs uppercase tracking-wider bg-amber-400 hover:bg-amber-300 text-slate-950 transition-all shadow-md active:scale-98"
          >
            Claim & Continue
          </button>
        </motion.div>
      </div>
    </AnimatePresence>
  );
};
