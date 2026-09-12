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
        <Card className="p-8 sm:p-12 bg-sandwich-900/90 border-sandwich-700/80 shadow-luxury-card rounded-3xl text-center relative overflow-hidden backdrop-blur-md">
          {/* Top subtle decorative gradient glow */}
          <div className="absolute top-0 left-1/2 -translate-x-1/2 w-72 h-32 bg-gradient-to-r from-sandwich-700/20 via-sandwich-500/25 to-sandwich-800/20 blur-2xl pointer-events-none rounded-full" />

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
            className="w-20 h-20 rounded-3xl bg-sandwich-800 border border-sandwich-600 text-sandwich-50 flex items-center justify-center mx-auto shadow-glow-silver mb-6"
          >
            <Swords className="w-10 h-10 text-sandwich-100" />
          </motion.div>

          {/* Header */}
          <div className="space-y-2 mb-8">
            <Badge variant="default" size="md" className="mx-auto bg-sandwich-800 border-sandwich-600 text-sandwich-100">
              <Sparkles className="w-3.5 h-3.5 mr-1 text-sandwich-200" />
              CHALLENGER REGISTERED
            </Badge>
            <h1 className="text-3xl sm:text-4xl font-black text-sandwich-50 tracking-tight">
              WELCOME TO DEVARENA,
              <br />
              <span className="bg-gradient-to-r from-sandwich-50 via-sandwich-200 to-sandwich-400 bg-clip-text text-transparent">
                {displayName.toUpperCase()}
              </span>
            </h1>
            <p className="text-base text-sandwich-300">
              Your credentials are confirmed. Your journey starts now.
            </p>
          </div>

          {/* Starter Status Grid */}
          <div className="grid grid-cols-3 gap-3 p-4 rounded-2xl bg-sandwich-950 border border-sandwich-800 mb-8 font-mono">
            <div className="p-3 bg-sandwich-900 rounded-xl shadow-sm border border-sandwich-750">
              <div className="flex items-center justify-center text-sandwich-200 mb-1">
                <Zap className="w-4 h-4" />
              </div>
              <p className="text-[10px] text-sandwich-400 uppercase">Rank Tier</p>
              <p className="text-sm font-black text-sandwich-100">Level {level}</p>
              <p className="text-[10px] text-sandwich-300 font-semibold">Novice</p>
            </div>

            <div className="p-3 bg-sandwich-900 rounded-xl shadow-sm border border-sandwich-750">
              <div className="flex items-center justify-center text-sandwich-200 mb-1">
                <Trophy className="w-4 h-4" />
              </div>
              <p className="text-[10px] text-sandwich-400 uppercase">Starting MMR</p>
              <p className="text-sm font-black text-sandwich-100">{rating}</p>
              <p className="text-[10px] text-sandwich-300 font-semibold">Bronze I</p>
            </div>

            <div className="p-3 bg-sandwich-900 rounded-xl shadow-sm border border-sandwich-750">
              <div className="flex items-center justify-center text-sandwich-200 mb-1">
                <Target className="w-4 h-4" />
              </div>
              <p className="text-[10px] text-sandwich-400 uppercase">Progression</p>
              <p className="text-sm font-black text-sandwich-100">{currentXp} XP</p>
              <p className="text-[10px] text-sandwich-300 font-semibold">Tier 1</p>
            </div>
          </div>

          {/* Main Action */}
          <Button
            variant="primary"
            size="lg"
            className="w-full text-base py-4"
            onClick={() => navigate('/home')}
            rightIcon={<ArrowRight className="w-5 h-5 text-sandwich-950" />}
          >
            ENTER THE ARENA
          </Button>

          <p className="text-xs text-sandwich-400 font-mono mt-4">
            Press to open the Challenger HQ and find your first match
          </p>
        </Card>
      </motion.div>
    </div>
  );
};
