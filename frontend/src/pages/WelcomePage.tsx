import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Badge } from '../components/ui/Badge';
import {
  Swords,
  Sparkles,
  Trophy,
  Zap,
  Target,
  ArrowRight,
} from 'lucide-react';
import { motion, useReducedMotion } from 'framer-motion';

export const WelcomePage: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const shouldReduceMotion = useReducedMotion();

  const displayName = user?.displayName || user?.username || 'Player';
  const rating = user?.stats?.rating || 1000;
  const level = user?.progression?.level || 1;
  const currentXp = user?.progression?.currentXp || 0;

  return (
    <div className="min-h-[85vh] flex items-center justify-center px-4 py-12">
      <motion.div
        initial={{ opacity: 0, scale: 0.92, y: 20 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        transition={{ duration: 0.5, ease: 'easeOut' }}
        className="max-w-xl w-full"
      >
        <Card className="p-8 sm:p-12 bg-white border-slate-200/90 shadow-2xl rounded-3xl text-center relative overflow-hidden">
          {/* Top subtle decorative gradient glow */}
          <div className="absolute top-0 left-1/2 -translate-x-1/2 w-72 h-32 bg-gradient-to-r from-cyan-400/20 via-violet-400/20 to-emerald-400/20 blur-2xl pointer-events-none rounded-full" />

          {/* Animated Icon Emblem */}
          <motion.div
            animate={
              shouldReduceMotion
                ? {}
                : {
                    scale: [1, 1.06, 1],
                    rotate: [0, 2, -2, 0],
                  }
            }
            transition={{ duration: 4, repeat: Infinity, ease: 'easeInOut' }}
            className="w-20 h-20 rounded-3xl bg-gradient-to-tr from-cyan-500 via-blue-600 to-violet-600 text-white flex items-center justify-center mx-auto shadow-xl shadow-cyan-500/20 mb-6"
          >
            <Swords className="w-10 h-10" />
          </motion.div>

          {/* Header */}
          <div className="space-y-2 mb-8">
            <Badge variant="cyan" size="md" className="mx-auto">
              <Sparkles className="w-3.5 h-3.5 mr-1" />
              CHALLENGER REGISTERED
            </Badge>
            <h1 className="text-3xl sm:text-4xl font-black text-slate-900 tracking-tight">
              WELCOME TO DEVARENA,
              <br />
              <span className="bg-gradient-to-r from-cyan-600 to-violet-600 bg-clip-text text-transparent">
                {displayName.toUpperCase()}
              </span>
            </h1>
            <p className="text-base text-slate-600">
              Your credentials are confirmed. Your journey starts now.
            </p>
          </div>

          {/* Starter Status Grid */}
          <div className="grid grid-cols-3 gap-3 p-4 rounded-2xl bg-slate-50 border border-slate-200/80 mb-8 font-mono">
            <div className="p-3 bg-white rounded-xl shadow-sm border border-slate-100">
              <div className="flex items-center justify-center text-cyan-600 mb-1">
                <Zap className="w-4 h-4" />
              </div>
              <p className="text-[10px] text-slate-400 uppercase">Rank Tier</p>
              <p className="text-sm font-black text-slate-900">Level {level}</p>
              <p className="text-[10px] text-cyan-600 font-semibold">Novice</p>
            </div>

            <div className="p-3 bg-white rounded-xl shadow-sm border border-slate-100">
              <div className="flex items-center justify-center text-violet-600 mb-1">
                <Trophy className="w-4 h-4" />
              </div>
              <p className="text-[10px] text-slate-400 uppercase">Starting MMR</p>
              <p className="text-sm font-black text-slate-900">{rating}</p>
              <p className="text-[10px] text-violet-600 font-semibold">Bronze I</p>
            </div>

            <div className="p-3 bg-white rounded-xl shadow-sm border border-slate-100">
              <div className="flex items-center justify-center text-emerald-600 mb-1">
                <Target className="w-4 h-4" />
              </div>
              <p className="text-[10px] text-slate-400 uppercase">Progression</p>
              <p className="text-sm font-black text-slate-900">{currentXp} XP</p>
              <p className="text-[10px] text-emerald-600 font-semibold">Tier 1</p>
            </div>
          </div>

          {/* Main Action */}
          <Button
            variant="glow"
            size="lg"
            className="w-full text-base py-4"
            onClick={() => navigate('/home')}
            rightIcon={<ArrowRight className="w-5 h-5" />}
          >
            ENTER THE ARENA
          </Button>

          <p className="text-xs text-slate-400 font-mono mt-4">
            Press to open the Challenger HQ and find your first match
          </p>
        </Card>
      </motion.div>
    </div>
  );
};
