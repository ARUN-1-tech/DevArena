import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { arenaService } from '../services/arenaService';
import { questService } from '../services/questService';
import { ArenaHomeData, DailyQuest } from '../types/arena';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Badge } from '../components/ui/Badge';
import { LevelUpModal } from '../components/player/LevelUpModal';
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
  Gift,
  Check,
  Loader2,
  RefreshCw,
  LogIn,
} from 'lucide-react';
import { motion } from 'framer-motion';

export const ArenaHomePage: React.FC = () => {
  const navigate = useNavigate();

  const [homeData, setHomeData] = useState<ArenaHomeData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [errorStatus, setErrorStatus] = useState<number | null>(null);
  const [claimingQuestId, setClaimingQuestId] = useState<string | null>(null);

  // Level-up modal state
  const [levelUpData, setLevelUpData] = useState<{
    show: boolean;
    level: number;
    prevLevel: number;
    xp: number;
  }>({
    show: false,
    level: 1,
    prevLevel: 1,
    xp: 0,
  });

  // Dynamic greeting based on current time
  const getGreeting = () => {
    const hour = new Date().getHours();
    if (hour < 12) return 'Good morning';
    if (hour < 18) return 'Good afternoon';
    return 'Good evening';
  };

  const loadData = async () => {
    try {
      setLoading(true);
      setError(null);
      setErrorStatus(null);
      const data = await arenaService.getHomeData();
      setHomeData(data);
    } catch (err: any) {
      console.error('Failed to load arena home data', err);
      const status = err?.status || err?.response?.status || null;
      setErrorStatus(status);
      const message =
        err?.message ||
        err?.response?.data?.message ||
        (status === 401
          ? 'Your session has expired or is invalid. Please log in again.'
          : 'Could not connect to the DevArena game hub.');
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleClaimQuest = async (quest: DailyQuest) => {
    if (!quest.completed || quest.claimed || claimingQuestId) return;

    try {
      setClaimingQuestId(quest.id);
      const res = await questService.claimQuest(quest.id);

      // Optimistically update local quest status
      setHomeData((prev) => {
        if (!prev) return null;
        const updatedQuests = prev.dailyQuests.map((q) =>
          q.id === quest.id ? { ...q, status: 'CLAIMED' as const, claimed: true } : q
        );
        const updatedProgression = {
          ...prev.progression,
          currentXp: res.xpResult.newXp,
          level: res.xpResult.newLevel,
          totalXp: res.xpResult.totalXp,
          xpToNextLevel: res.xpResult.xpToNextLevel,
          questsCompleted: prev.progression.questsCompleted + 1,
          xpPercentage: Math.min(
            100,
            Math.round((res.xpResult.newXp / Math.max(1, res.xpResult.xpToNextLevel)) * 100)
          ),
        };
        return {
          ...prev,
          dailyQuests: updatedQuests,
          progression: updatedProgression,
        };
      });

      // Check level-up celebration
      if (res.xpResult.leveledUp) {
        setLevelUpData({
          show: true,
          level: res.xpResult.newLevel,
          prevLevel: res.xpResult.previousLevel,
          xp: res.xpEarned,
        });
      }
    } catch (err: any) {
      console.error('Failed to claim quest', err);
    } finally {
      setClaimingQuestId(null);
    }
  };

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[500px] space-y-4">
        <Loader2 className="w-10 h-10 animate-spin text-cyan-600" />
        <p className="text-sm font-mono text-slate-500">Connecting to Arena HQ...</p>
      </div>
    );
  }

  if (error || !homeData) {
    const isAuthError = errorStatus === 401 || errorStatus === 403;
    return (
      <Card className="p-8 text-center max-w-md mx-auto space-y-4">
        <p className="text-rose-500 font-bold">Arena Connection Notice</p>
        <p className="text-sm text-slate-600">{error || 'Unable to load player progression data.'}</p>
        <div className="flex flex-col sm:flex-row items-center justify-center gap-3 pt-2">
          {isAuthError ? (
            <Button variant="primary" onClick={() => navigate('/login')} leftIcon={<LogIn className="w-4 h-4" />}>
              LOG IN TO ARENA
            </Button>
          ) : (
            <Button variant="glow" onClick={loadData} leftIcon={<RefreshCw className="w-4 h-4" />}>
              RETRY CONNECTION
            </Button>
          )}
        </div>
      </Card>
    );
  }

  const { player, progression, stats, dailyQuests, recommendedChallenges, recentActivity, nextMilestone } = homeData;
  const displayName = player.displayName || player.username || 'Player';

  // Rank Tier title
  const getRankTier = (rating: number) => {
    if (rating >= 2400) return 'Grandmaster';
    if (rating >= 2000) return 'Master';
    if (rating >= 1600) return 'Diamond';
    if (rating >= 1400) return 'Platinum';
    if (rating >= 1200) return 'Gold';
    if (rating >= 1000) return 'Silver I';
    return 'Bronze';
  };

  return (
    <div className="space-y-8 max-w-6xl mx-auto">
      {/* ========================================================= */}
      {/* TOP: Dynamic Greeting & Quick Status                      */}
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
            Your combat station is live. Complete daily quests, hone algorithms, and climb the ranks.
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
              {stats.rating}{' '}
              <span className="text-xs text-cyan-600 font-mono font-bold">
                {getRankTier(stats.rating)}
              </span>
            </p>
          </div>
        </div>
      </div>

      {/* ========================================================= */}
      {/* PLAYER BANNER & COMBAT STATS CARD                         */}
      {/* ========================================================= */}
      <Card className="p-6 sm:p-8 bg-gradient-to-r from-slate-900 via-slate-900 to-slate-800 text-white border-slate-800 shadow-xl rounded-2xl relative overflow-hidden">
        {/* Decorative subtle ambient glow */}
        <div className="absolute top-0 right-0 w-96 h-96 bg-gradient-to-bl from-cyan-500/20 via-violet-500/10 to-transparent blur-3xl pointer-events-none" />

        <div className="relative z-10 grid grid-cols-1 md:grid-cols-12 gap-6 items-center">
          {/* Avatar & Player Title */}
          <div className="md:col-span-6 flex items-center gap-4">
            <div className="w-16 h-16 sm:w-20 sm:h-20 rounded-2xl bg-gradient-to-tr from-cyan-500 to-violet-600 flex items-center justify-center text-white text-2xl font-black shadow-lg border-2 border-white/20 shrink-0">
              {displayName.slice(0, 2).toUpperCase()}
            </div>
            <div>
              <div className="flex items-center gap-2 flex-wrap">
                <h2 className="text-xl sm:text-2xl font-black text-white">
                  {displayName}
                </h2>
                <Badge variant="cyan" size="sm" className="bg-cyan-500/20 text-cyan-300 border-cyan-400/30">
                  Lvl {progression.level} Warrior
                </Badge>
              </div>
              <p className="text-xs font-mono text-slate-400 mt-1">
                @{player.username} • {progression.totalXp} Total XP Earned
              </p>
              {player.bio && (
                <p className="text-xs text-slate-300 italic mt-1 line-clamp-1">
                  "{player.bio}"
                </p>
              )}
            </div>
          </div>

          {/* Real Animated XP Progression Bar */}
          <div className="md:col-span-6 space-y-2 bg-slate-800/60 p-4 rounded-xl border border-slate-700/60">
            <div className="flex justify-between text-xs font-mono">
              <span className="text-slate-300 font-bold flex items-center gap-1">
                <Flame className="w-3.5 h-3.5 text-amber-400" />
                Level {progression.level} Progression
              </span>
              <span className="text-cyan-400 font-semibold">
                {progression.currentXp} / {progression.xpToNextLevel} XP ({progression.xpPercentage}%)
              </span>
            </div>
            <div className="w-full h-3 bg-slate-700/80 rounded-full overflow-hidden p-0.5">
              <motion.div
                initial={{ width: 0 }}
                animate={{ width: `${Math.max(5, progression.xpPercentage)}%` }}
                transition={{ duration: 0.8, ease: 'easeOut' }}
                className="h-full bg-gradient-to-r from-cyan-400 to-blue-500 rounded-full"
              />
            </div>
            <div className="flex justify-between text-[10px] font-mono text-slate-400">
              <span>Current: Level {progression.level}</span>
              <span>Next: Level {progression.level + 1} Champion</span>
            </div>
          </div>
        </div>

        {/* Real Stats Badges */}
        <div className="mt-6 pt-6 border-t border-slate-800 grid grid-cols-2 sm:grid-cols-4 gap-3 text-center font-mono">
          <div className="p-3 bg-slate-800/40 rounded-xl border border-slate-700/40">
            <p className="text-[10px] text-slate-400 uppercase">Competitive MMR</p>
            <p className="text-lg font-black text-cyan-400">{stats.rating}</p>
          </div>
          <div className="p-3 bg-slate-800/40 rounded-xl border border-slate-700/40">
            <p className="text-[10px] text-slate-400 uppercase">Challenges Solved</p>
            <p className="text-lg font-black text-emerald-400">{progression.challengesSolved}</p>
          </div>
          <div className="p-3 bg-slate-800/40 rounded-xl border border-slate-700/40">
            <p className="text-[10px] text-slate-400 uppercase">Active Streak</p>
            <p className="text-lg font-black text-amber-400 flex items-center justify-center gap-1">
              <Flame className="w-4 h-4 text-amber-400 fill-amber-400" />
              {progression.currentStreak} Days
            </p>
          </div>
          <div className="p-3 bg-slate-800/40 rounded-xl border border-slate-700/40">
            <p className="text-[10px] text-slate-400 uppercase">Quests Completed</p>
            <p className="text-lg font-black text-violet-400">{progression.questsCompleted}</p>
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
                <span className="text-[11px] font-mono font-bold bg-cyan-50 text-cyan-800 px-2.5 py-1 rounded-full border border-cyan-200 flex items-center gap-1">
                  <span className="w-2 h-2 rounded-full bg-cyan-500 animate-ping" />
                  MATCHMAKING READY
                </span>
              </div>

              <div>
                <h4 className="text-xl font-extrabold text-slate-900">
                  ⚔️ 1v1 Arena Duels
                </h4>
                <p className="text-xs text-slate-600 mt-1.5 leading-relaxed">
                  Match with an opponent in your skill bracket for real-time algorithmic speed battles.
                </p>
              </div>

              <div className="space-y-1 text-[11px] font-mono text-slate-500 pt-2 border-t border-slate-100">
                <div className="flex justify-between">
                  <span>Match Stakes</span>
                  <span className="font-semibold text-cyan-600">±25 MMR • +150 XP</span>
                </div>
              </div>
            </div>

            <Button
              variant="glow"
              size="md"
              className="w-full mt-6"
              onClick={() => navigate('/matchmaking')}
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
                <span className="text-[11px] font-mono font-bold bg-violet-50 text-violet-800 px-2.5 py-1 rounded-full border border-violet-200">
                  KATA VAULT
                </span>
              </div>

              <div>
                <h4 className="text-xl font-extrabold text-slate-900">
                  🧩 Practice Dojo
                </h4>
                <p className="text-xs text-slate-600 mt-1.5 leading-relaxed">
                  Sharpen data structure patterns, dynamic programming, and graphs at your own pace.
                </p>
              </div>

              <div className="space-y-1 text-[11px] font-mono text-slate-500 pt-2 border-t border-slate-100">
                <div className="flex justify-between">
                  <span>Library Status</span>
                  <span className="font-semibold text-violet-600">15 Challenges Ready</span>
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

          {/* Card 3: Featured Challenge */}
          <Card className="p-6 bg-white border-slate-200/90 shadow-md hover:shadow-xl hover:border-amber-400 transition-all rounded-2xl flex flex-col justify-between group">
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <div className="w-12 h-12 rounded-xl bg-gradient-to-tr from-amber-500 to-orange-600 text-white flex items-center justify-center shadow-md group-hover:scale-105 transition-transform">
                  <Target className="w-6 h-6" />
                </div>
                <span className="text-[11px] font-mono font-bold bg-amber-50 text-amber-800 px-2.5 py-1 rounded-full border border-amber-200 flex items-center gap-1">
                  <Clock className="w-3 h-3" />
                  TODAY'S PICK
                </span>
              </div>

              <div>
                <h4 className="text-xl font-extrabold text-slate-900">
                  🎯 Featured Kata
                </h4>
                <p className="text-xs text-slate-600 mt-1.5 leading-relaxed">
                  Solve today's featured algorithmic problem to keep your activity streak active.
                </p>
              </div>

              <div className="space-y-1 text-[11px] font-mono text-slate-500 pt-2 border-t border-slate-100">
                <div className="flex justify-between">
                  <span>Selected</span>
                  <span className="font-semibold text-slate-800 truncate max-w-[140px]">
                    {recommendedChallenges[0]?.title || 'Two Sum'}
                  </span>
                </div>
              </div>
            </div>

            <Button
              variant="outline"
              size="md"
              className="w-full mt-6 hover:bg-amber-50 hover:text-amber-700 hover:border-amber-300"
              onClick={() => {
                if (recommendedChallenges[0]) {
                  navigate(`/challenges/${recommendedChallenges[0].id}`);
                } else {
                  navigate('/challenges');
                }
              }}
              rightIcon={<ArrowRight className="w-4 h-4" />}
            >
              START PROBLEM
            </Button>
          </Card>
        </div>
      </div>

      {/* ========================================================= */}
      {/* DAILY QUESTS INTERACTIVE SECTION                          */}
      {/* ========================================================= */}
      <div>
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-lg font-extrabold text-slate-900 tracking-tight flex items-center gap-2">
            <Gift className="w-5 h-5 text-amber-500" />
            Daily Quests ({dailyQuests.filter((q) => q.claimed).length}/{dailyQuests.length} Claimed)
          </h3>
          <span className="text-xs font-mono text-slate-400">Resets daily at 00:00 UTC</span>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {dailyQuests.map((quest) => {
            const isCompleted = quest.completed || quest.currentCount >= quest.targetCount;
            const isClaimed = quest.claimed;
            const isClaimable = isCompleted && !isClaimed;
            const isClaiming = claimingQuestId === quest.id;
            const pct = Math.min(100, Math.round((quest.currentCount / Math.max(1, quest.targetCount)) * 100));

            return (
              <Card
                key={quest.id}
                className={`p-5 bg-white border rounded-2xl transition-all ${
                  isClaimed
                    ? 'border-slate-200/80 bg-slate-50/50 opacity-80'
                    : isClaimable
                    ? 'border-amber-400 shadow-md ring-1 ring-amber-300/40'
                    : 'border-slate-200/90 shadow-sm'
                }`}
              >
                <div className="flex items-start justify-between gap-3 mb-2">
                  <div className="space-y-1">
                    <h5 className="text-sm font-black text-slate-900">{quest.title}</h5>
                    <p className="text-xs text-slate-500 leading-snug">{quest.description}</p>
                  </div>
                  <span className="text-xs font-mono font-bold text-amber-600 bg-amber-50 border border-amber-200 px-2 py-0.5 rounded-full shrink-0">
                    +{quest.xpReward} XP
                  </span>
                </div>

                {/* Progress Bar */}
                <div className="my-3 space-y-1">
                  <div className="flex justify-between text-[11px] font-mono text-slate-500">
                    <span>Progress</span>
                    <span>
                      {quest.currentCount} / {quest.targetCount}
                    </span>
                  </div>
                  <div className="w-full h-2 bg-slate-100 rounded-full overflow-hidden">
                    <div
                      className={`h-full rounded-full transition-all duration-500 ${
                        isClaimed
                          ? 'bg-slate-400'
                          : isCompleted
                          ? 'bg-emerald-500'
                          : 'bg-gradient-to-r from-amber-400 to-orange-500'
                      }`}
                      style={{ width: `${pct}%` }}
                    />
                  </div>
                </div>

                {/* Action Button */}
                <div className="pt-2">
                  {isClaimed ? (
                    <div className="w-full py-2 rounded-xl bg-slate-100 text-slate-500 font-mono text-xs font-bold flex items-center justify-center gap-1">
                      <Check className="w-3.5 h-3.5" />
                      CLAIMED
                    </div>
                  ) : isClaimable ? (
                    <Button
                      variant="glow"
                      size="sm"
                      className="w-full bg-gradient-to-r from-amber-500 to-orange-600 hover:from-amber-600 hover:to-orange-700 text-white font-bold"
                      onClick={() => handleClaimQuest(quest)}
                      disabled={isClaiming}
                    >
                      {isClaiming ? (
                        <Loader2 className="w-3.5 h-3.5 animate-spin mx-auto" />
                      ) : (
                        'CLAIM REWARD'
                      )}
                    </Button>
                  ) : (
                    <Button
                      variant="ghost"
                      size="sm"
                      className="w-full text-slate-500 hover:text-slate-800 bg-slate-50 font-mono text-xs"
                      onClick={() => navigate('/challenges')}
                    >
                      GO TO KATAS →
                    </Button>
                  )}
                </div>
              </Card>
            );
          })}
        </div>
      </div>

      {/* ========================================================= */}
      {/* RECOMMENDED CHALLENGES & RECENT ACTIVITY                  */}
      {/* ========================================================= */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Recommended Challenges (7 cols) */}
        <div className="lg:col-span-7 space-y-4">
          <div className="flex items-center justify-between">
            <h3 className="text-lg font-extrabold text-slate-900 tracking-tight flex items-center gap-2">
              <Code2 className="w-5 h-5 text-cyan-600" />
              Recommended for Level {progression.level}
            </h3>
            <button
              onClick={() => navigate('/challenges')}
              className="text-xs font-mono font-bold text-cyan-600 hover:text-cyan-700 flex items-center gap-1"
            >
              VIEW ALL <ArrowRight className="w-3.5 h-3.5" />
            </button>
          </div>

          <div className="space-y-3">
            {recommendedChallenges.map((ch) => (
              <Card
                key={ch.id}
                className="p-4 bg-white border-slate-200/90 shadow-sm hover:shadow-md hover:border-cyan-400 transition-all rounded-2xl cursor-pointer flex items-center justify-between gap-4 group"
                onClick={() => navigate(`/challenges/${ch.id}`)}
              >
                <div className="space-y-1 flex-1">
                  <div className="flex items-center gap-2 flex-wrap">
                    <span className="text-sm font-extrabold text-slate-900 group-hover:text-cyan-600 transition-colors">
                      {ch.title}
                    </span>
                    <span
                      className={`text-[9px] font-mono px-1.5 py-0.5 rounded font-bold uppercase ${
                        ch.difficulty === 'EASY'
                          ? 'bg-emerald-50 text-emerald-700 border border-emerald-200'
                          : ch.difficulty === 'MEDIUM'
                          ? 'bg-amber-50 text-amber-700 border border-amber-200'
                          : 'bg-rose-50 text-rose-700 border border-rose-200'
                      }`}
                    >
                      {ch.difficulty}
                    </span>
                  </div>
                  <p className="text-xs font-mono text-slate-500">
                    {ch.category.replace('_', ' ')} • {ch.estimatedMinutes} mins
                  </p>
                </div>

                <div className="flex items-center gap-3 shrink-0 font-mono">
                  <span className="text-xs font-bold text-cyan-600">+{ch.xpReward} XP</span>
                  <div className="w-8 h-8 rounded-lg bg-slate-100 group-hover:bg-cyan-50 group-hover:text-cyan-600 text-slate-400 flex items-center justify-center transition-colors">
                    <ArrowRight className="w-3.5 h-3.5 group-hover:translate-x-0.5 transition-transform" />
                  </div>
                </div>
              </Card>
            ))}
          </div>
        </div>

        {/* Recent Activity Feed & Milestone (5 cols) */}
        <div className="lg:col-span-5 space-y-6">
          {/* Milestone Card */}
          <Card className="p-6 bg-gradient-to-br from-violet-900 to-slate-900 text-white border-violet-800/80 shadow-md rounded-2xl">
            <div className="flex items-center gap-2 text-violet-300 font-mono text-xs uppercase font-bold mb-2">
              <Shield className="w-4 h-4 text-violet-400" />
              UPCOMING RANK MILESTONE
            </div>
            <h4 className="text-lg font-black text-white">{nextMilestone.title}</h4>
            <p className="text-xs text-slate-300 mt-1 leading-relaxed">
              {nextMilestone.description}
            </p>
            <div className="mt-4 pt-4 border-t border-white/10 flex items-center justify-between text-xs font-mono">
              <span className="text-slate-400">Required: Level {nextMilestone.requiredLevel}</span>
              <span className="text-violet-300 font-semibold">
                {nextMilestone.unlocked ? 'Unlocked' : `In Progress`}
              </span>
            </div>
          </Card>

          {/* Activity Feed */}
          <Card className="p-5 bg-white border-slate-200/90 shadow-sm rounded-2xl space-y-3">
            <h4 className="text-xs font-mono uppercase font-bold text-slate-400 tracking-wider">
              RECENT PROGRESSION EVENTS
            </h4>

            {recentActivity.length === 0 ? (
              <p className="text-xs font-mono text-slate-400 py-3 text-center">
                No recent activity recorded yet. Start your first challenge!
              </p>
            ) : (
              <div className="divide-y divide-slate-100">
                {recentActivity.slice(0, 4).map((act) => (
                  <div key={act.id} className="py-2.5 flex items-center justify-between text-xs font-mono">
                    <div className="space-y-0.5">
                      <p className="font-bold text-slate-800">{act.title}</p>
                      <p className="text-[10px] text-slate-400">
                        {new Date(act.createdAt).toLocaleDateString()} • {act.description}
                      </p>
                    </div>
                    {act.xpEarned > 0 && (
                      <span className="text-xs font-bold text-emerald-600 bg-emerald-50 px-2 py-0.5 rounded-full border border-emerald-200 shrink-0 ml-2">
                        +{act.xpEarned} XP
                      </span>
                    )}
                  </div>
                ))}
              </div>
            )}
          </Card>
        </div>
      </div>

      {/* Level-Up Celebration Modal */}
      <LevelUpModal
        isOpen={levelUpData.show}
        level={levelUpData.level}
        prevLevel={levelUpData.prevLevel}
        xpEarned={levelUpData.xp}
        onClose={() => setLevelUpData((prev) => ({ ...prev, show: false }))}
      />
    </div>
  );
};
