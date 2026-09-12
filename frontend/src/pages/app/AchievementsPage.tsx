import React, { useEffect, useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Trophy,
  Award,
  CheckCircle2,
  Lock,
  Flame,
  Swords,
  Zap,
  Sparkles,
  Shield,
  Loader2,
} from 'lucide-react';
import { Card } from '../../components/ui/Card';
import { Badge } from '../../components/ui/Badge';
import { Button } from '../../components/ui/Button';
import { achievementService } from '../../services/achievementService';
import { PlayerAchievement, AchievementRarity } from '../../types/progression';

const ICON_MAP: Record<string, any> = {
  Swords,
  Zap,
  Trophy,
  Flame,
  Sparkles,
  Shield,
  Award,
};

export const AchievementsPage: React.FC = () => {
  const [achievements, setAchievements] = useState<PlayerAchievement[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filter, setFilter] = useState<'ALL' | 'UNLOCKED' | 'LOCKED'>('ALL');
  const [categoryFilter, setCategoryFilter] = useState<string>('ALL');

  const loadAchievements = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await achievementService.getMyAchievements();
      setAchievements(data);
    } catch (err: any) {
      setError(err?.message || 'Failed to load achievements.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAchievements();
  }, []);

  const totalCount = achievements.length;
  const unlockedCount = achievements.filter((a) => a.unlocked).length;
  const totalXpEarned = achievements
    .filter((a) => a.unlocked)
    .reduce((sum, a) => sum + a.xpReward, 0);

  const filtered = achievements.filter((a) => {
    if (filter === 'UNLOCKED' && !a.unlocked) return false;
    if (filter === 'LOCKED' && a.unlocked) return false;
    if (categoryFilter !== 'ALL' && a.category !== categoryFilter) return false;
    return true;
  });

  const getRarityBadgeVariant = (rarity: AchievementRarity): "default" | "cyan" | "purple" | "warning" | "success" => {
    switch (rarity) {
      case 'LEGENDARY': return 'warning';
      case 'EPIC': return 'purple';
      case 'RARE': return 'cyan';
      default: return 'default';
    }
  };

  const getRarityBorder = (rarity: AchievementRarity, unlocked: boolean) => {
    if (!unlocked) return 'border-slate-200/80 bg-slate-50/50 opacity-75';
    switch (rarity) {
      case 'LEGENDARY': return 'border-amber-400 bg-gradient-to-b from-amber-500/10 via-amber-50/30 to-white shadow-md shadow-amber-500/10';
      case 'EPIC': return 'border-purple-400 bg-gradient-to-b from-purple-500/10 via-purple-50/30 to-white shadow-sm';
      case 'RARE': return 'border-cyan-400 bg-gradient-to-b from-cyan-500/10 via-cyan-50/30 to-white shadow-sm';
      default: return 'border-emerald-300 bg-white shadow-sm';
    }
  };

  return (
    <div className="space-y-8 max-w-5xl mx-auto pb-16">
      {/* Header & Stats Bar */}
      <div className="flex flex-col sm:flex-row sm:items-end justify-between gap-4">
        <div>
          <Badge variant="cyan" size="sm" className="mb-2">
            <Award className="w-3.5 h-3.5 mr-1" />
            HONOR HALL
          </Badge>
          <h1 className="text-3xl font-black text-slate-900 tracking-tight">
            ARENA ACHIEVEMENTS
          </h1>
          <p className="text-sm text-slate-600 mt-1">
            Collect trophies of coding mastery, speed feats, and competitive duel triumphs.
          </p>
        </div>

        {/* Global Progress Summary Card */}
        <div className="flex items-center gap-4 bg-white p-3.5 px-5 rounded-2xl border border-slate-200 shadow-sm">
          <div className="text-center">
            <span className="text-[10px] uppercase font-mono text-slate-400 block font-bold">Unlocked</span>
            <span className="text-lg font-black font-mono text-slate-900">
              {unlockedCount} / {totalCount}
            </span>
          </div>
          <div className="h-8 w-px bg-slate-200" />
          <div className="text-center">
            <span className="text-[10px] uppercase font-mono text-slate-400 block font-bold">Bonus XP</span>
            <span className="text-lg font-black font-mono text-amber-600">
              +{totalXpEarned} XP
            </span>
          </div>
        </div>
      </div>

      {/* Filter Tabs */}
      <div className="flex flex-wrap items-center justify-between gap-3 pt-2">
        <div className="flex items-center gap-1.5 bg-slate-100 p-1 rounded-xl border border-slate-200/80">
          {(['ALL', 'UNLOCKED', 'LOCKED'] as const).map((f) => (
            <button
              key={f}
              onClick={() => setFilter(f)}
              className={`px-3 py-1.5 text-xs font-bold font-mono uppercase tracking-wider rounded-lg transition-all ${
                filter === f
                  ? 'bg-white text-slate-900 shadow-sm'
                  : 'text-slate-500 hover:text-slate-800'
              }`}
            >
              {f}
            </button>
          ))}
        </div>

        <div className="flex items-center gap-1.5">
          {['ALL', 'CHALLENGE', 'BATTLE', 'STREAK', 'MASTERY'].map((cat) => (
            <button
              key={cat}
              onClick={() => setCategoryFilter(cat)}
              className={`px-2.5 py-1 text-xs font-bold rounded-lg transition-colors ${
                categoryFilter === cat
                  ? 'bg-slate-900 text-white'
                  : 'bg-white border border-slate-200 text-slate-600 hover:bg-slate-50'
              }`}
            >
              {cat}
            </button>
          ))}
        </div>
      </div>

      {loading ? (
        <div className="py-24 flex flex-col items-center justify-center text-slate-400 gap-3">
          <Loader2 className="w-8 h-8 animate-spin text-cyan-600" />
          <span className="text-sm font-medium">Loading honor trophies...</span>
        </div>
      ) : error ? (
        <Card className="p-8 text-center text-rose-600 bg-rose-50/50 border-rose-200 rounded-2xl">
          <p className="text-sm font-medium">{error}</p>
          <Button variant="secondary" size="sm" onClick={loadAchievements} className="mt-4">
            Retry
          </Button>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
          <AnimatePresence>
            {filtered.map((a) => {
              const IconComp = ICON_MAP[a.icon] || Award;
              return (
                <motion.div
                  key={a.id}
                  layout
                  initial={{ opacity: 0, scale: 0.95 }}
                  animate={{ opacity: 1, scale: 1 }}
                  exit={{ opacity: 0, scale: 0.95 }}
                  transition={{ duration: 0.2 }}
                >
                  <Card
                    className={`p-6 rounded-2xl border transition-all relative overflow-hidden flex flex-col justify-between h-full ${getRarityBorder(
                      a.rarity,
                      a.unlocked
                    )}`}
                  >
                    <div className="flex items-start gap-4">
                      {/* Badge Icon */}
                      <div
                        className={`w-14 h-14 rounded-2xl flex items-center justify-center shrink-0 shadow-sm ${
                          a.unlocked
                            ? a.rarity === 'LEGENDARY'
                              ? 'bg-gradient-to-tr from-amber-500 to-yellow-400 text-slate-950 shadow-amber-500/20'
                              : a.rarity === 'EPIC'
                              ? 'bg-gradient-to-tr from-purple-600 to-violet-500 text-white shadow-purple-500/20'
                              : a.rarity === 'RARE'
                              ? 'bg-gradient-to-tr from-cyan-600 to-blue-500 text-white shadow-cyan-500/20'
                              : 'bg-emerald-500 text-white'
                            : 'bg-slate-200 text-slate-400'
                        }`}
                      >
                        <IconComp className="w-7 h-7" />
                      </div>

                      {/* Content */}
                      <div className="flex-1 min-w-0">
                        <div className="flex items-center justify-between gap-2">
                          <h4 className="text-base font-black text-slate-900 truncate">
                            {a.name}
                          </h4>
                          <span className="text-xs font-mono font-black text-amber-600 shrink-0">
                            +{a.xpReward} XP
                          </span>
                        </div>
                        <p className="text-xs text-slate-600 mt-1 leading-relaxed">
                          {a.description}
                        </p>

                        <div className="flex items-center gap-2 mt-3">
                          <Badge variant={getRarityBadgeVariant(a.rarity)} size="sm">
                            {a.rarity}
                          </Badge>
                          <span className="text-[10px] font-mono text-slate-400 uppercase font-semibold">
                            {a.category}
                          </span>
                        </div>
                      </div>
                    </div>

                    {/* Footer: Progress Bar or Unlock Timestamp */}
                    <div className="mt-5 pt-4 border-t border-slate-100">
                      {a.unlocked ? (
                        <div className="flex items-center justify-between text-xs font-mono">
                          <span className="inline-flex items-center gap-1.5 text-emerald-600 font-bold">
                            <CheckCircle2 className="w-4 h-4" /> Unlocked
                          </span>
                          {a.unlockedAt && (
                            <span className="text-slate-400 text-[11px]">
                              {new Date(a.unlockedAt).toLocaleDateString()}
                            </span>
                          )}
                        </div>
                      ) : (
                        <div>
                          <div className="flex items-center justify-between text-[11px] font-mono text-slate-500 mb-1.5">
                            <span className="flex items-center gap-1">
                              <Lock className="w-3.5 h-3.5 text-slate-400" /> Progress
                            </span>
                            <span className="font-bold">
                              {a.currentProgress} / {a.targetProgress} ({a.progressPercentage}%)
                            </span>
                          </div>
                          <div className="w-full bg-slate-200 rounded-full h-1.5 overflow-hidden">
                            <div
                              className="bg-cyan-600 h-1.5 rounded-full transition-all duration-500"
                              style={{ width: `${a.progressPercentage}%` }}
                            />
                          </div>
                        </div>
                      )}
                    </div>
                  </Card>
                </motion.div>
              );
            })}
          </AnimatePresence>
        </div>
      )}
    </div>
  );
};
