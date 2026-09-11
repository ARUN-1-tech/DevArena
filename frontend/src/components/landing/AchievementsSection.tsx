import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { ACHIEVEMENTS_CATALOG, AchievementItem } from '../../data/landingData';
import { Card } from '../ui/Card';
import { Badge } from '../ui/Badge';
import { Swords, Zap, Cpu, Flame, Shield, CheckCircle, Sparkles, Lock } from 'lucide-react';

export const AchievementsSection: React.FC = () => {
  const [filter, setFilter] = useState<'ALL' | 'UNLOCKED' | 'EPIC_LEGENDARY'>('ALL');

  const filteredAchievements = ACHIEVEMENTS_CATALOG.filter((item) => {
    if (filter === 'UNLOCKED') return item.unlocked;
    if (filter === 'EPIC_LEGENDARY') return item.rarity === 'EPIC' || item.rarity === 'LEGENDARY';
    return true;
  });

  const iconMap: Record<string, React.ReactNode> = {
    Swords: <Swords className="w-5 h-5" />,
    Zap: <Zap className="w-5 h-5" />,
    Cpu: <Cpu className="w-5 h-5" />,
    Flame: <Flame className="w-5 h-5" />,
    Shield: <Shield className="w-5 h-5" />,
    CheckCircle: <CheckCircle className="w-5 h-5" />,
  };

  const rarityBadgeVariant = {
    COMMON: 'default',
    RARE: 'info',
    EPIC: 'purple',
    LEGENDARY: 'warning',
  } as const;

  return (
    <section id="achievements" className="py-20 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      {/* Section Header */}
      <div className="flex flex-col md:flex-row md:items-end justify-between mb-12 gap-6">
        <div className="space-y-2">
          <Badge variant="purple">GAMIFIED MILESTONES</Badge>
          <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight text-slate-900">
            UNLOCK MEDALS & GLORY
          </h2>
          <p className="text-sm sm:text-base text-slate-600">
            Over 80 unique achievements celebrating speed, persistence, streak dominance, and
            algorithmic mastery.
          </p>
        </div>

        {/* Filter Pills */}
        <div className="flex items-center gap-2 p-1 bg-slate-100 rounded-xl">
          <button
            onClick={() => setFilter('ALL')}
            className={`px-3 py-1.5 text-xs font-mono font-semibold rounded-lg transition-all ${
              filter === 'ALL'
                ? 'bg-white text-slate-900 shadow-sm'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            All (6)
          </button>
          <button
            onClick={() => setFilter('UNLOCKED')}
            className={`px-3 py-1.5 text-xs font-mono font-semibold rounded-lg transition-all ${
              filter === 'UNLOCKED'
                ? 'bg-white text-slate-900 shadow-sm'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            Unlocked (3)
          </button>
          <button
            onClick={() => setFilter('EPIC_LEGENDARY')}
            className={`px-3 py-1.5 text-xs font-mono font-semibold rounded-lg transition-all ${
              filter === 'EPIC_LEGENDARY'
                ? 'bg-white text-slate-900 shadow-sm'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            Epic & Legendary
          </button>
        </div>
      </div>

      {/* Achievement Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {filteredAchievements.map((achievement: AchievementItem) => {
          const percent = Math.min(
            100,
            Math.round((achievement.currentProgress / achievement.maxProgress) * 100)
          );

          return (
            <motion.div
              key={achievement.id}
              whileHover={{ y: -3 }}
              transition={{ duration: 0.2 }}
            >
              <Card
                className={`h-full flex flex-col justify-between p-6 transition-all ${
                  achievement.unlocked
                    ? 'border-emerald-200/90 bg-white'
                    : 'border-slate-200/80 bg-slate-50/50'
                }`}
              >
                <div>
                  <div className="flex items-start justify-between mb-4">
                    <div
                      className={`w-11 h-11 rounded-xl flex items-center justify-center border shadow-sm ${
                        achievement.unlocked
                          ? 'bg-emerald-50 text-emerald-600 border-emerald-200'
                          : 'bg-slate-100 text-slate-400 border-slate-200'
                      }`}
                    >
                      {achievement.unlocked ? (
                        iconMap[achievement.iconName] || <Sparkles className="w-5 h-5" />
                      ) : (
                        <Lock className="w-5 h-5 text-slate-400" />
                      )}
                    </div>

                    <div className="flex items-center gap-1.5">
                      <Badge variant={rarityBadgeVariant[achievement.rarity]} size="sm">
                        {achievement.rarity}
                      </Badge>
                      <span className="text-xs font-mono font-bold text-cyan-600">
                        +{achievement.xpReward} XP
                      </span>
                    </div>
                  </div>

                  <h3 className="text-base font-bold text-slate-900 mb-1">{achievement.title}</h3>
                  <p className="text-xs text-slate-600 leading-relaxed mb-4">
                    {achievement.description}
                  </p>
                </div>

                {/* Progress Bar & Status */}
                <div className="pt-3 border-t border-slate-100 space-y-1.5">
                  <div className="flex justify-between text-xs font-mono text-slate-500">
                    <span>
                      {achievement.unlocked ? (
                        <strong className="text-emerald-600">Unlocked & Claimed</strong>
                      ) : (
                        'Progress'
                      )}
                    </span>
                    <span className="font-semibold text-slate-800">
                      {achievement.currentProgress} / {achievement.maxProgress} ({percent}%)
                    </span>
                  </div>

                  <div className="w-full h-2 bg-slate-200 rounded-full overflow-hidden">
                    <div
                      className={`h-full rounded-full transition-all duration-500 ${
                        achievement.unlocked ? 'bg-emerald-500' : 'bg-cyan-500'
                      }`}
                      style={{ width: `${percent}%` }}
                    />
                  </div>
                </div>
              </Card>
            </motion.div>
          );
        })}
      </div>
    </section>
  );
};
