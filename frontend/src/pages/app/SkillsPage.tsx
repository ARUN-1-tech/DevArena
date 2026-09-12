import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import {
  Zap,
  Layers,
  Search,
  Database,
  Globe,
  ArrowUpDown,
  GitBranch,
  Workflow,
  Cpu,
  Share2,
  Lock,
  CheckCircle2,
  ArrowRight,
  Loader2,
} from 'lucide-react';
import { Card } from '../../components/ui/Card';
import { Badge } from '../../components/ui/Badge';
import { Button } from '../../components/ui/Button';
import { skillService } from '../../services/skillService';
import { PlayerSkill } from '../../types/progression';

const ICON_MAP: Record<string, any> = {
  Layers,
  Search,
  Database,
  Globe,
  ArrowUpDown,
  GitBranch,
  Workflow,
  Cpu,
  Share2,
};

export const SkillsPage: React.FC = () => {
  const [skills, setSkills] = useState<PlayerSkill[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedSkill, setSelectedSkill] = useState<PlayerSkill | null>(null);
  const [activeCategory, setActiveCategory] = useState<string>('ALL');

  const loadSkills = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await skillService.getMySkills();
      setSkills(data);
      if (data.length > 0 && !selectedSkill) {
        setSelectedSkill(data[0]);
      }
    } catch (err: any) {
      setError(err?.message || 'Failed to load skill matrix.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadSkills();
  }, []);

  const filteredSkills = skills.filter((s) => {
    if (activeCategory === 'ALL') return true;
    return s.category === activeCategory;
  });

  const categories: { label: string; value: string }[] = [
    { label: 'All Disciplines', value: 'ALL' },
    { label: 'Data Structures', value: 'DATA_STRUCTURES' },
    { label: 'Algorithms', value: 'ALGORITHMS' },
    { label: 'Database', value: 'DATABASE' },
    { label: 'Web Dev', value: 'WEB_DEVELOPMENT' },
  ];

  const overallMastery = skills.length > 0
    ? Math.round(skills.reduce((sum, s) => sum + s.masteryPercentage, 0) / skills.length)
    : 0;

  return (
    <div className="space-y-8 max-w-5xl mx-auto pb-16">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-end justify-between gap-4">
        <div>
          <Badge variant="neutral" size="sm" className="mb-2 bg-sandwich-800 text-sandwich-200 border-sandwich-700">
            <Zap className="w-3.5 h-3.5 mr-1 text-sandwich-100" />
            MASTERY MATRIX
          </Badge>
          <h1 className="text-3xl font-black text-sandwich-100 tracking-tight">
            DEVELOPER SKILL TREE
          </h1>
          <p className="text-sm text-sandwich-400 mt-1">
            Advance node levels and increase mastery percentages by solving challenges and winning duels.
          </p>
        </div>

        {/* Global Mastery Card */}
        <div className="bg-sandwich-900/90 backdrop-blur-xl p-4 px-6 rounded-2xl border border-sandwich-800 shadow-luxury flex items-center gap-5">
          <div className="text-center">
            <span className="text-[10px] font-mono uppercase text-sandwich-500 font-bold block">
              Discipline Mastery
            </span>
            <span className="text-2xl font-black font-mono text-sandwich-100">
              {overallMastery}%
            </span>
          </div>
          <div className="h-9 w-px bg-sandwich-800" />
          <div className="text-center">
            <span className="text-[10px] font-mono uppercase text-sandwich-500 font-bold block">
              Active Nodes
            </span>
            <span className="text-2xl font-black font-mono text-sandwich-100">
              {skills.filter((s) => s.unlocked).length} / {skills.length}
            </span>
          </div>
        </div>
      </div>

      {/* Category Filter Tabs */}
      <div className="flex flex-wrap items-center gap-2">
        {categories.map((c) => (
          <button
            key={c.value}
            onClick={() => setActiveCategory(c.value)}
            className={`px-3.5 py-1.5 rounded-xl text-xs font-bold transition-all ${
              activeCategory === c.value
                ? 'bg-sandwich-50 text-sandwich-950 shadow-glow-white'
                : 'bg-sandwich-900/90 border border-sandwich-800 text-sandwich-400 hover:text-sandwich-200 hover:border-sandwich-700'
            }`}
          >
            {c.label}
          </button>
        ))}
      </div>

      {loading ? (
        <div className="py-24 flex flex-col items-center justify-center text-sandwich-400 gap-3">
          <Loader2 className="w-8 h-8 animate-spin text-sandwich-200" />
          <span className="text-sm font-medium">Constructing skill hierarchy...</span>
        </div>
      ) : error ? (
        <Card className="p-8 text-center text-rose-400 bg-rose-950/20 border-rose-900/50 rounded-2xl">
          <p className="text-sm font-medium">{error}</p>
          <Button variant="secondary" size="sm" onClick={loadSkills} className="mt-4">
            Retry
          </Button>
        </Card>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 items-start">
          {/* SKILL NODES GRID (2 COLUMNS ON LG) */}
          <div className="lg:col-span-2 grid grid-cols-1 sm:grid-cols-2 gap-4">
            {filteredSkills.map((s) => {
              const IconComp = ICON_MAP[s.icon] || Zap;
              const isSelected = selectedSkill?.skillId === s.skillId;

              return (
                <motion.div
                  key={s.skillId}
                  whileHover={{ scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                >
                  <Card
                    onClick={() => setSelectedSkill(s)}
                    className={`p-5 rounded-2xl border cursor-pointer transition-all ${
                      isSelected
                        ? 'border-sandwich-200 bg-sandwich-850 shadow-glow-silver ring-1 ring-sandwich-200/50'
                        : s.unlocked
                        ? 'border-sandwich-800 hover:border-sandwich-700 bg-sandwich-900/90 shadow-luxury'
                        : 'border-sandwich-900 bg-sandwich-950/60 opacity-50'
                    }`}
                  >
                    <div className="flex items-center justify-between mb-3">
                      <div
                        className={`w-11 h-11 rounded-xl flex items-center justify-center border ${
                          s.unlocked
                            ? 'bg-sandwich-800 border-sandwich-700 text-sandwich-100 shadow-sm'
                            : 'bg-sandwich-950 border-sandwich-900 text-sandwich-600'
                        }`}
                      >
                        <IconComp className="w-5 h-5" />
                      </div>

                      {s.unlocked ? (
                        <Badge variant="neutral" size="sm" className="bg-sandwich-800 text-sandwich-200 border-sandwich-700">
                          Lvl {s.currentLevel}/{s.maxLevel}
                        </Badge>
                      ) : (
                        <span className="inline-flex items-center gap-1 text-[11px] font-mono text-sandwich-500 font-medium">
                          <Lock className="w-3 h-3" /> Locked
                        </span>
                      )}
                    </div>

                    <h3 className="text-sm font-black text-sandwich-100 truncate">
                      {s.name}
                    </h3>
                    <p className="text-xs text-sandwich-400 mt-1 line-clamp-2">
                      {s.description}
                    </p>

                    {/* Prerequisite Tag or Mastery Bar */}
                    <div className="mt-4 pt-3 border-t border-sandwich-800/80">
                      {s.unlocked ? (
                        <div>
                          <div className="flex justify-between text-[10px] font-mono text-sandwich-400 mb-1">
                            <span>Mastery</span>
                            <span className="font-bold text-sandwich-200">{s.masteryPercentage}%</span>
                          </div>
                          <div className="w-full bg-sandwich-950 rounded-full h-1.5 overflow-hidden border border-sandwich-800/50">
                            <div
                              className="bg-gradient-to-r from-sandwich-400 to-sandwich-100 h-1.5 rounded-full"
                              style={{ width: `${s.masteryPercentage}%` }}
                            />
                          </div>
                        </div>
                      ) : (
                        <span className="text-[10px] font-mono text-sandwich-400 flex items-center gap-1 truncate">
                          Requires: {s.prerequisiteName || 'Prior Node'}
                        </span>
                      )}
                    </div>
                  </Card>
                </motion.div>
              );
            })}
          </div>

          {/* INSPECTOR PANEL (1 COLUMN ON LG) */}
          {selectedSkill && (
            <div className="lg:col-span-1 sticky top-6">
              <Card className="p-6 bg-sandwich-900/95 border border-sandwich-700 rounded-2xl shadow-luxury backdrop-blur-xl">
                <div className="flex items-start justify-between">
                  <Badge variant="neutral" size="sm" className="bg-sandwich-800 text-sandwich-200 border-sandwich-700">
                    {selectedSkill.category.replace('_', ' ')}
                  </Badge>
                  {selectedSkill.unlocked ? (
                    <span className="text-xs font-mono font-bold text-emerald-400 flex items-center gap-1">
                      <CheckCircle2 className="w-3.5 h-3.5" /> Unlocked
                    </span>
                  ) : (
                    <span className="text-xs font-mono font-bold text-sandwich-500 flex items-center gap-1">
                      <Lock className="w-3.5 h-3.5" /> Locked
                    </span>
                  )}
                </div>

                <div className="mt-4">
                  <h3 className="text-xl font-black text-sandwich-100">
                    {selectedSkill.name}
                  </h3>
                  <p className="text-xs text-sandwich-300 mt-2 leading-relaxed">
                    {selectedSkill.description}
                  </p>
                </div>

                {/* Level & XP Progression */}
                <div className="mt-5 p-4 rounded-xl bg-sandwich-950/80 border border-sandwich-800 space-y-3">
                  <div className="flex items-center justify-between text-xs font-mono">
                    <span className="text-sandwich-400 font-medium">Rank Level</span>
                    <span className="font-black text-sandwich-100 text-sm">
                      Level {selectedSkill.currentLevel} of {selectedSkill.maxLevel}
                    </span>
                  </div>

                  <div className="space-y-1">
                    <div className="flex justify-between text-[11px] font-mono text-sandwich-400">
                      <span>XP to Next Level</span>
                      <span className="font-bold text-sandwich-200">
                        {selectedSkill.currentXp} / {selectedSkill.xpToNextLevel} XP
                      </span>
                    </div>
                    <div className="w-full bg-sandwich-800 rounded-full h-2 overflow-hidden">
                      <div
                        className="bg-sandwich-100 h-2 rounded-full transition-all shadow-glow-white"
                        style={{
                          width: `${Math.min(
                            100,
                            (selectedSkill.currentXp / selectedSkill.xpToNextLevel) * 100
                          )}%`,
                        }}
                      />
                    </div>
                  </div>

                  <div className="flex items-center justify-between text-xs font-mono pt-1">
                    <span className="text-sandwich-400 font-medium">Mastery Quotient</span>
                    <span className="font-black text-sandwich-100 text-sm">
                      {selectedSkill.masteryPercentage}%
                    </span>
                  </div>
                </div>

                {/* Prerequisite Information */}
                {selectedSkill.prerequisiteName && (
                  <div className="mt-4 p-3 rounded-xl bg-sandwich-800/60 border border-sandwich-700 text-xs">
                    <span className="font-bold text-sandwich-200 block mb-1">
                      Dependency Constraint
                    </span>
                    <p className="text-[11px] text-sandwich-400 flex items-center gap-1.5">
                      <ArrowRight className="w-3.5 h-3.5 shrink-0 text-sandwich-300" />
                      Must unlock & level up <strong className="text-sandwich-200">{selectedSkill.prerequisiteName}</strong> to proceed.
                    </p>
                  </div>
                )}

                <div className="mt-5 text-[11px] text-sandwich-500 text-center font-mono">
                  Solve katas categorized under this archetype to raise skill XP automatically.
                </div>
              </Card>
            </div>
          )}
        </div>
      )}
    </div>
  );
};
