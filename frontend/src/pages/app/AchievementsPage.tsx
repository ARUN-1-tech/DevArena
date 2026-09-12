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

  const getRarityBorder = (rarity: AchievementRarity, unlocked: boolean) => {
    if (!unlocked) return 'border-sandwich-900 bg-sandwich-950/60 opacity-55';
    switch (rarity) {
      case 'LEGENDARY': return 'border-sandwich-100/90 bg-gradient-to-b from-sandwich-800/80 via-sandwich-900/90 to-sandwich-950 shadow-glow-silver';
      case 'EPIC': return 'border-sandwich-500 bg-gradient-to-b from-sandwich-800/50 to-sandwich-900/90 shadow-luxury';
      case 'RARE': return 'border-sandwich-700 bg-sandwich-900/90 shadow-luxury';
      default: return 'border-sandwich-800 bg-sandwich-900/80 shadow-sm';
    }
  };

  return (
    <div className="space-y-8 max-w-5xl mx-auto pb-16">
      {/* Header & Stats Bar */}
      <div className="flex flex-col sm:flex-row sm:items-end justify-between gap-4">
        <div>
          <Badge variant="neutral" size="sm" className="mb-2 bg-sandwich-800 text-sandwich-200 border-sandwich-700">
            <Award className="w-3.5 h-3.5 mr-1 text-sandwich-100" />
            HONOR HALL
          </Badge>
          <h1 className="text-3xl font-black text-sandwich-100 tracking-tight">
            ARENA ACHIEVEMENTS
          </h1>
          <p className="text-sm text-sandwich-400 mt-1">
            Collect trophies of coding mastery, speed feats, and competitive duel triumphs.
          </p>
        </div>

        {/* Global Progress Summary Card */}
        <div className="flex items-center gap-4 bg-sandwich-900/90 backdrop-blur-xl p-3.5 px-5 rounded-2xl border border-sandwich-800 shadow-luxury">
          <div className="text-center">
            <span className="text-[10px] uppercase font-mono text-sandwich-500 block font-bold">Unlocked</span>
            <span className="text-lg font-black font-mono text-sandwich-100">
              {unlockedCount} / {totalCount}
            </span>
          </div>
          <div className="h-8 w-px bg-sandwich-800" />
          <div className="text-center">
            <span className="text-[10px] uppercase font-mono text-sandwich-500 block font-bold">Bonus XP</span>
            <span className="text-lg font-black font-mono text-sandwich-100">
              +{totalXpEarned} XP
            </span>
          </div>
        </div>
      </div>

      {/* Filter Tabs */}
      <div className="flex flex-wrap items-center justify-between gap-3 pt-2">
        <div className="flex items-center gap-1.5 bg-sandwich-900 p-1 rounded-xl border border-sandwich-800">
          {(['ALL', 'UNLOCKED', 'LOCKED'] as const).map((f) => (
            <button
              key={f}
              onClick={() => setFilter(f)}
              className={`px-3 py-1.5 text-xs font-bold font-mono uppercase tracking-wider rounded-lg transition-all ${
                filter === f
                  ? 'bg-sandwich-50 text-sandwich-950 shadow-glow-white'
                  : 'text-sandwich-400 hover:text-sandwich-200'
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
                  ? 'bg-sandwich-50 text-sandwich-950'
                  : 'bg-sandwich-900/90 border border-sandwich-800 text-sandwich-400 hover:text-sandwich-200 hover:border-sandwich-700'
              }`}
            >
              {cat}
            </button>
          ))}
        </div>
      </div>

      {loading ? (
        <div className="py-24 flex flex-col items-center justify-center text-sandwich-400 gap-3">
          <Loader2 className="w-8 h-8 animate-spin text-sandwich-200" />
          <span className="text-sm font-medium">Loading honor trophies...</span>
        </div>
      ) : error ? (
        <Card className="p-8 text-center text-rose-400 bg-rose-950/20 border-rose-900/50 rounded-2xl">
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
                        className={`w-14 h-14 rounded-2xl flex items-center justify-center shrink-0 shadow-sm border ${
                          a.unlocked
                            ? a.rarity === 'LEGENDARY'
                              ? 'bg-sandwich-100 text-sandwich-950 border-white shadow-glow-white'
                              : a.rarity === 'EPIC'
                              ? 'bg-sandwich-700 text-sandwich-100 border-sandwich-500'
                              : a.rarity === 'RARE'
                              ? 'bg-sandwich-800 text-sandwich-200 border-sandwich-600'
                              : 'bg-sandwich-800 text-sandwich-200 border-sandwich-700'
                            : 'bg-sandwich-950 border-sandwich-900 text-sandwich-600'
                        }`}
                      >
                        <IconComp className="w-7 h-7" />
                      </div>

                      {/* Content */}
                      <div className="flex-1 min-w-0">
                        <div className="flex items-center justify-between gap-2">
                          <h4 className="text-base font-black text-sandwich-100 truncate">
                            {a.name}
                          </h4>
                          <span className="text-xs font-mono font-black text-sandwich-100 shrink-0">
                            +{a.xpReward} XP
                          </span>
                        </div>
                        <p className="text-xs text-sandwich-400 mt-1 leading-relaxed">
                          {a.description}
                        </p>

                        <div className="flex items-center gap-2 mt-3">
                          <Badge variant="neutral" size="sm" className="bg-sandwich-800 text-sandwich-300 border-sandwich-700">
                            {a.rarity}
                          </Badge>
                          <span className="text-[10px] font-mono text-sandwich-500 uppercase font-semibold">
                            {a.category}
                          </span>
                        </div>
                      </div>
                    </div>

                    {/* Footer: Progress Bar or Unlock Timestamp */}
                    <div className="mt-5 pt-4 border-t border-sandwich-800/80">
                      {a.unlocked ? (
                        <div className="flex items-center justify-between text-xs font-mono">
                          <span className="inline-flex items-center gap-1.5 text-emerald-400 font-bold">
                            <CheckCircle2 className="w-4 h-4" /> Unlocked
                          </span>
                          {a.unlockedAt && (
                            <span className="text-sandwich-500 text-[11px]">
                              {new Date(a.unlockedAt).toLocaleDateString()}
                            </span>
                          )}
                        </div>
                      ) : (
                        <div>
                          <div className="flex items-center justify-between text-[11px] font-mono text-sandwich-400 mb-1.5">
                            <span className="flex items-center gap-1">
                              <Lock className="w-3.5 h-3.5 text-sandwich-500" /> Progress
                            </span>
                            <span className="font-bold text-sandwich-200">
                              {a.currentProgress} / {a.targetProgress} ({a.progressPercentage}%)
                            </span>
                          </div>
                          <div className="w-full bg-sandwich-950 rounded-full h-1.5 overflow-hidden border border-sandwich-800/50">
                            <div
                              className="bg-sandwich-200 h-1.5 rounded-full transition-all duration-500"
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
