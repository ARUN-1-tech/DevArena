import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Badge } from '../components/ui/Badge';
import {
  Swords,
  Code2,
  Target,
  Sparkles,
  Trophy,
  Flame,
  ArrowRight,
  TrendingUp,
  Shield,
  Clock,
  CheckCircle2,
} from 'lucide-react';
import { motion } from 'framer-motion';

export const ArenaHomePage: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  // Dynamic greeting based on current time
  const getGreeting = () => {
    const hour = new Date().getHours();
    if (hour < 12) return 'Good morning';
    if (hour < 18) return 'Good afternoon';
    return 'Good evening';
  };

  const displayName = user?.displayName || user?.username || 'Player';
  const rating = user?.stats?.rating || 1000;
  const level = user?.progression?.level || 1;
  const currentXp = user?.progression?.currentXp || 0;
  const xpToNext = user?.progression?.xpToNextLevel || 1000;
  const xpPct = Math.min(100, Math.round((currentXp / Math.max(1, xpToNext)) * 100));
  const wins = user?.stats?.wins || 0;
  const losses = user?.stats?.losses || 0;
  const totalBattles = wins + losses + (user?.stats?.draws || 0);
  const winRate = totalBattles > 0 ? Math.round((wins / totalBattles) * 100) : 0;

  return (
    <div className="space-y-8">
      {/* ========================================================= */}
      {/* TOP: Welcome Greeting & Quick Status                      */}
      {/* ========================================================= */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-mono font-bold uppercase tracking-wider bg-cyan-50 text-cyan-800 border border-cyan-200 mb-2">
            <Sparkles className="w-3.5 h-3.5 text-cyan-600" />
            ARENA COMBAT STATION
          </div>
          <h1 className="text-3xl sm:text-4xl font-black text-slate-900 tracking-tight">
            {getGreeting()}, {displayName} 👋
          </h1>
          <p className="text-sm text-slate-600 mt-1">
            The arena is active. Pick your battle mode or continue your daily quest.
          </p>
        </div>

        {/* Live MMR summary badge */}
        <div className="flex items-center gap-3 bg-white px-4 py-2.5 rounded-2xl border border-slate-200/90 shadow-sm shrink-0">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-cyan-600 to-violet-600 text-white flex items-center justify-center shadow-xs">
            <Trophy className="w-5 h-5" />
          </div>
          <div>
            <p className="text-[10px] font-mono text-slate-400 uppercase font-semibold">
              COMPETITIVE MMR
            </p>
            <p className="text-lg font-black text-slate-900 leading-tight">
              {rating}{' '}
              <span className="text-xs text-cyan-600 font-mono font-bold">
                Bronze I
              </span>
            </p>
          </div>
        </div>
      </div>

      {/* ========================================================= */}
      {/* PLAYER BANNER & COMBAT STATS CARD                         */}
      {/* ========================================================= */}
      <Card className="p-6 sm:p-8 bg-gradient-to-r from-slate-900 via-slate-900 to-slate-800 text-white border-slate-800 shadow-xl rounded-2xl relative overflow-hidden">
        {/* Subtle decorative glow */}
        <div className="absolute top-0 right-0 w-96 h-96 bg-gradient-to-bl from-cyan-500/20 via-violet-500/10 to-transparent blur-3xl pointer-events-none" />

        <div className="relative z-10 grid grid-cols-1 md:grid-cols-12 gap-6 items-center">
          {/* Avatar & Player Title */}
          <div className="md:col-span-6 flex items-center gap-4">
            <div className="w-16 h-16 sm:w-20 sm:h-20 rounded-2xl bg-gradient-to-tr from-cyan-500 to-violet-600 flex items-center justify-center text-white text-2xl font-black shadow-lg border-2 border-white/20 shrink-0">
              {displayName.slice(0, 2).toUpperCase()}
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h2 className="text-xl sm:text-2xl font-black text-white">
                  {displayName}
                </h2>
                <Badge variant="cyan" size="sm" className="bg-cyan-500/20 text-cyan-300 border-cyan-400/30">
                  Lvl {level} Novice
                </Badge>
              </div>
              <p className="text-xs font-mono text-slate-400 mt-1">
                @{user?.username || 'challenger'} • Ready for match
              </p>
              {user?.bio && (
                <p className="text-xs text-slate-300 italic mt-2 line-clamp-1">
                  "{user.bio}"
                </p>
              )}
            </div>
          </div>

          {/* XP Progression Bar */}
          <div className="md:col-span-6 space-y-2 bg-slate-800/60 p-4 rounded-xl border border-slate-700/60">
            <div className="flex justify-between text-xs font-mono">
              <span className="text-slate-300 font-bold flex items-center gap-1">
                <Flame className="w-3.5 h-3.5 text-amber-400" />
                Level {level} Progression
              </span>
              <span className="text-cyan-400 font-semibold">
                {currentXp} / {xpToNext} XP ({xpPct}%)
              </span>
            </div>
            <div className="w-full h-3 bg-slate-700/80 rounded-full overflow-hidden p-0.5">
              <motion.div
                initial={{ width: 0 }}
                animate={{ width: `${Math.max(5, xpPct)}%` }}
                transition={{ duration: 0.8, ease: 'easeOut' }}
                className="h-full bg-gradient-to-r from-cyan-400 to-blue-500 rounded-full"
              />
            </div>
            <div className="flex justify-between text-[10px] font-mono text-slate-400">
              <span>Tier 1: Novice Coder</span>
              <span>Next: Level {level + 1} Champion</span>
            </div>
          </div>
        </div>

        {/* Quick Combat Stat Badges */}
        <div className="mt-6 pt-6 border-t border-slate-800 grid grid-cols-2 sm:grid-cols-4 gap-3 text-center font-mono">
          <div className="p-3 bg-slate-800/40 rounded-xl border border-slate-700/40">
            <p className="text-[10px] text-slate-400 uppercase">Total Duels</p>
            <p className="text-lg font-black text-white">{totalBattles}</p>
          </div>
          <div className="p-3 bg-slate-800/40 rounded-xl border border-slate-700/40">
            <p className="text-[10px] text-slate-400 uppercase">Victories</p>
            <p className="text-lg font-black text-emerald-400">{wins}</p>
          </div>
          <div className="p-3 bg-slate-800/40 rounded-xl border border-slate-700/40">
            <p className="text-[10px] text-slate-400 uppercase">Win Rate</p>
            <p className="text-lg font-black text-cyan-400">{winRate}%</p>
          </div>
          <div className="p-3 bg-slate-800/40 rounded-xl border border-slate-700/40">
            <p className="text-[10px] text-slate-400 uppercase">Current Streak</p>
            <p className="text-lg font-black text-amber-400">
              {user?.stats?.winStreak || 0} 🔥
            </p>
          </div>
        </div>
      </Card>

      {/* ========================================================= */}
      {/* 3 LARGE PRIMARY ACTION CARDS                              */}
      {/* ========================================================= */}
      <div>
        <h3 className="text-lg font-extrabold text-slate-900 tracking-tight mb-4 flex items-center gap-2">
          <TrendingUp className="w-5 h-5 text-cyan-600" />
          Choose Your Combat Objective
        </h3>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {/* Card 1: Quick Battle */}
          <Card className="p-6 bg-white border-slate-200/90 shadow-md hover:shadow-xl hover:border-cyan-400 transition-all rounded-2xl flex flex-col justify-between group">
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <div className="w-12 h-12 rounded-xl bg-gradient-to-tr from-cyan-600 to-blue-600 text-white flex items-center justify-center shadow-md group-hover:scale-105 transition-transform">
                  <Swords className="w-6 h-6" />
                </div>
                <span className="text-[11px] font-mono font-bold bg-cyan-50 text-cyan-800 px-2 py-1 rounded-full border border-cyan-200 flex items-center gap-1">
                  <span className="w-2 h-2 rounded-full bg-cyan-500 animate-ping" />
                  QUEUE READY
                </span>
              </div>

              <div>
                <h4 className="text-xl font-extrabold text-slate-900">
                  ⚔️ Quick Battle
                </h4>
                <p className="text-xs text-slate-600 mt-1.5 leading-relaxed">
                  Find an opponent in your skill tier and compete in a real-time 1v1 algorithmic speed duel.
                </p>
              </div>

              <div className="space-y-1 text-[11px] font-mono text-slate-500 pt-2 border-t border-slate-100">
                <div className="flex justify-between">
                  <span>Match Format</span>
                  <span className="font-semibold text-slate-800">1v1 Speed Run</span>
                </div>
                <div className="flex justify-between">
                  <span>Stake</span>
                  <span className="font-semibold text-cyan-600">±25 MMR • +150 XP</span>
                </div>
              </div>
            </div>

            <Button
              variant="glow"
              size="md"
              className="w-full mt-6"
              onClick={() => navigate('/arena')}
              rightIcon={<ArrowRight className="w-4 h-4" />}
            >
              FIND OPPONENT
            </Button>
          </Card>

          {/* Card 2: Practice Katas */}
          <Card className="p-6 bg-white border-slate-200/90 shadow-md hover:shadow-xl hover:border-violet-400 transition-all rounded-2xl flex flex-col justify-between group">
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <div className="w-12 h-12 rounded-xl bg-gradient-to-tr from-violet-600 to-indigo-600 text-white flex items-center justify-center shadow-md group-hover:scale-105 transition-transform">
                  <Code2 className="w-6 h-6" />
                </div>
                <span className="text-[11px] font-mono font-bold bg-violet-50 text-violet-800 px-2 py-1 rounded-full border border-violet-200">
                  124 KATAS
                </span>
              </div>

              <div>
                <h4 className="text-xl font-extrabold text-slate-900">
                  🧩 Practice Dojo
                </h4>
                <p className="text-xs text-slate-600 mt-1.5 leading-relaxed">
                  Sharpen your problem-solving abilities without timer pressure. Test edge cases and explore solutions.
                </p>
              </div>

              <div className="space-y-1 text-[11px] font-mono text-slate-500 pt-2 border-t border-slate-100">
                <div className="flex justify-between">
                  <span>Topics</span>
                  <span className="font-semibold text-slate-800">Graphs, DP, Trees</span>
                </div>
                <div className="flex justify-between">
                  <span>Reward</span>
                  <span className="font-semibold text-violet-600">+50 XP per kata</span>
                </div>
              </div>
            </div>

            <Button
              variant="outline"
              size="md"
              className="w-full mt-6 hover:bg-violet-50 hover:text-violet-700 hover:border-violet-300"
              onClick={() => navigate('/challenges')}
              rightIcon={<ArrowRight className="w-4 h-4" />}
            >
              BROWSE CHALLENGES
            </Button>
          </Card>

          {/* Card 3: Daily Quest */}
          <Card className="p-6 bg-white border-slate-200/90 shadow-md hover:shadow-xl hover:border-amber-400 transition-all rounded-2xl flex flex-col justify-between group">
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <div className="w-12 h-12 rounded-xl bg-gradient-to-tr from-amber-500 to-orange-600 text-white flex items-center justify-center shadow-md group-hover:scale-105 transition-transform">
                  <Target className="w-6 h-6" />
                </div>
                <span className="text-[11px] font-mono font-bold bg-amber-50 text-amber-800 px-2 py-1 rounded-full border border-amber-200 flex items-center gap-1">
                  <Clock className="w-3 h-3" />
                  14h 22m LEFT
                </span>
              </div>

              <div>
                <h4 className="text-xl font-extrabold text-slate-900">
                  🎯 Daily Quest
                </h4>
                <p className="text-xs text-slate-600 mt-1.5 leading-relaxed">
                  Complete today's featured problem to maintain your login streak and claim the 2x XP multiplier.
                </p>
              </div>

              <div className="space-y-1 text-[11px] font-mono text-slate-500 pt-2 border-t border-slate-100">
                <div className="flex justify-between">
                  <span>Today's Kata</span>
                  <span className="font-semibold text-slate-800 truncate max-w-[140px]">
                    Sliding Window Max
                  </span>
                </div>
                <div className="flex justify-between">
                  <span>Bonus</span>
                  <span className="font-semibold text-amber-600">+250 XP + Daily Token</span>
                </div>
              </div>
            </div>

            <Button
              variant="outline"
              size="md"
              className="w-full mt-6 hover:bg-amber-50 hover:text-amber-700 hover:border-amber-300"
              onClick={() => navigate('/challenges')}
              rightIcon={<ArrowRight className="w-4 h-4" />}
            >
              START QUEST
            </Button>
          </Card>
        </div>
      </div>

      {/* ========================================================= */}
      {/* ARENA INTEL & NOTICE FEED                                 */}
      {/* ========================================================= */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Next Unlocks */}
        <Card className="p-6 bg-white border-slate-200/90 shadow-sm rounded-2xl">
          <h4 className="text-sm font-bold text-slate-900 flex items-center gap-2 mb-3">
            <Shield className="w-4 h-4 text-cyan-600" />
            Upcoming Rank Unlocks
          </h4>
          <div className="space-y-3">
            <div className="flex items-center justify-between p-3 rounded-xl bg-slate-50 border border-slate-100">
              <div className="flex items-center gap-2.5">
                <div className="w-8 h-8 rounded-lg bg-cyan-100 text-cyan-700 flex items-center justify-center font-mono font-bold text-xs">
                  L2
                </div>
                <div>
                  <p className="text-xs font-bold text-slate-800">Ranked Duels Queue</p>
                  <p className="text-[11px] text-slate-500">Unlocks at Level 2 (1,000 XP)</p>
                </div>
              </div>
              <span className="text-xs font-mono text-cyan-600 font-semibold">
                {1000 - currentXp} XP left
              </span>
            </div>

            <div className="flex items-center justify-between p-3 rounded-xl bg-slate-50 border border-slate-100">
              <div className="flex items-center gap-2.5">
                <div className="w-8 h-8 rounded-lg bg-violet-100 text-violet-700 flex items-center justify-center font-mono font-bold text-xs">
                  L3
                </div>
                <div>
                  <p className="text-xs font-bold text-slate-800">Skill Tree Specializations</p>
                  <p className="text-[11px] text-slate-500">Unlocks at Level 3</p>
                </div>
              </div>
              <span className="text-xs font-mono text-slate-400">Locked</span>
            </div>
          </div>
        </Card>

        {/* Global Arena Intel */}
        <Card className="p-6 bg-white border-slate-200/90 shadow-sm rounded-2xl">
          <h4 className="text-sm font-bold text-slate-900 flex items-center gap-2 mb-3">
            <CheckCircle2 className="w-4 h-4 text-emerald-600" />
            Active Season Status
          </h4>
          <div className="space-y-2.5 text-xs text-slate-600">
            <p className="flex justify-between pb-2 border-b border-slate-100">
              <span className="font-mono text-slate-500">Current Season</span>
              <span className="font-bold text-slate-900">Season 01: Genesis Arena</span>
            </p>
            <p className="flex justify-between pb-2 border-b border-slate-100">
              <span className="font-mono text-slate-500">Active Challengers Online</span>
              <span className="font-bold text-emerald-600 flex items-center gap-1.5">
                <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
                1,420 Players
              </span>
            </p>
            <p className="flex justify-between">
              <span className="font-mono text-slate-500">Battle Engine Status</span>
              <span className="font-bold text-cyan-600">Operational (WebSockets Ready)</span>
            </p>
          </div>
        </Card>
      </div>
    </div>
  );
};
