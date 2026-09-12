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
          <Badge variant="cyan" size="sm" className="mb-2">
            <Zap className="w-3.5 h-3.5 mr-1" />
            MASTERY MATRIX
          </Badge>
          <h1 className="text-3xl font-black text-slate-900 tracking-tight">
            DEVELOPER SKILL TREE
          </h1>
          <p className="text-sm text-slate-600 mt-1">
            Advance node levels and increase mastery percentages by solving challenges and winning duels.
          </p>
        </div>

        {/* Global Mastery Card */}
        <div className="bg-white p-4 px-6 rounded-2xl border border-slate-200 shadow-sm flex items-center gap-5">
          <div className="text-center">
            <span className="text-[10px] font-mono uppercase text-slate-400 font-bold block">
              Discipline Mastery
            </span>
            <span className="text-2xl font-black font-mono text-cyan-600">
              {overallMastery}%
            </span>
          </div>
          <div className="h-9 w-px bg-slate-200" />
          <div className="text-center">
            <span className="text-[10px] font-mono uppercase text-slate-400 font-bold block">
              Active Nodes
            </span>
            <span className="text-2xl font-black font-mono text-slate-900">
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
                ? 'bg-slate-900 text-white shadow-sm'
                : 'bg-white border border-slate-200 text-slate-600 hover:bg-slate-50'
            }`}
          >
            {c.label}
          </button>
        ))}
      </div>

      {loading ? (
        <div className="py-24 flex flex-col items-center justify-center text-slate-400 gap-3">
          <Loader2 className="w-8 h-8 animate-spin text-cyan-600" />
          <span className="text-sm font-medium">Constructing skill hierarchy...</span>
        </div>
      ) : error ? (
        <Card className="p-8 text-center text-rose-600 bg-rose-50/50 border-rose-200 rounded-2xl">
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
                        ? 'border-cyan-500 bg-cyan-50/20 shadow-md ring-2 ring-cyan-400/30'
                        : s.unlocked
                        ? 'border-slate-200 hover:border-slate-300 bg-white shadow-sm'
                        : 'border-slate-200/70 bg-slate-50/60 opacity-65'
                    }`}
                  >
                    <div className="flex items-center justify-between mb-3">
                      <div
                        className={`w-11 h-11 rounded-xl flex items-center justify-center ${
                          s.unlocked
                            ? 'bg-gradient-to-tr from-cyan-600 to-blue-500 text-white shadow-sm'
                            : 'bg-slate-200 text-slate-400'
                        }`}
                      >
                        <IconComp className="w-5 h-5" />
                      </div>

                      {s.unlocked ? (
                        <Badge variant="cyan" size="sm">
                          Lvl {s.currentLevel}/{s.maxLevel}
                        </Badge>
                      ) : (
                        <span className="inline-flex items-center gap-1 text-[11px] font-mono text-slate-400 font-medium">
                          <Lock className="w-3 h-3" /> Locked
                        </span>
                      )}
                    </div>

                    <h3 className="text-sm font-black text-slate-900 truncate">
                      {s.name}
                    </h3>
                    <p className="text-xs text-slate-500 mt-1 line-clamp-2">
                      {s.description}
                    </p>

                    {/* Prerequisite Tag or Mastery Bar */}
                    <div className="mt-4 pt-3 border-t border-slate-100">
                      {s.unlocked ? (
                        <div>
                          <div className="flex justify-between text-[10px] font-mono text-slate-500 mb-1">
                            <span>Mastery</span>
                            <span className="font-bold">{s.masteryPercentage}%</span>
                          </div>
                          <div className="w-full bg-slate-100 rounded-full h-1.5 overflow-hidden">
                            <div
                              className="bg-gradient-to-r from-cyan-500 to-blue-500 h-1.5 rounded-full"
                              style={{ width: `${s.masteryPercentage}%` }}
                            />
                          </div>
                        </div>
                      ) : (
                        <span className="text-[10px] font-mono text-amber-600 flex items-center gap-1 truncate">
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
              <Card className="p-6 bg-white border-2 border-cyan-400/80 rounded-2xl shadow-md">
                <div className="flex items-start justify-between">
                  <Badge variant="purple" size="sm">
                    {selectedSkill.category.replace('_', ' ')}
                  </Badge>
                  {selectedSkill.unlocked ? (
                    <span className="text-xs font-mono font-bold text-emerald-600 flex items-center gap-1">
                      <CheckCircle2 className="w-3.5 h-3.5" /> Unlocked
                    </span>
                  ) : (
                    <span className="text-xs font-mono font-bold text-slate-400 flex items-center gap-1">
                      <Lock className="w-3.5 h-3.5" /> Locked
                    </span>
                  )}
                </div>

                <div className="mt-4">
                  <h3 className="text-xl font-black text-slate-900">
                    {selectedSkill.name}
                  </h3>
                  <p className="text-xs text-slate-600 mt-2 leading-relaxed">
                    {selectedSkill.description}
                  </p>
                </div>

                {/* Level & XP Progression */}
                <div className="mt-5 p-4 rounded-xl bg-slate-50 border border-slate-100 space-y-3">
                  <div className="flex items-center justify-between text-xs font-mono">
                    <span className="text-slate-500 font-medium">Rank Level</span>
                    <span className="font-black text-slate-900 text-sm">
                      Level {selectedSkill.currentLevel} of {selectedSkill.maxLevel}
                    </span>
                  </div>

                  <div className="space-y-1">
                    <div className="flex justify-between text-[11px] font-mono text-slate-500">
                      <span>XP to Next Level</span>
                      <span className="font-bold">
                        {selectedSkill.currentXp} / {selectedSkill.xpToNextLevel} XP
                      </span>
                    </div>
                    <div className="w-full bg-slate-200 rounded-full h-2 overflow-hidden">
                      <div
                        className="bg-cyan-600 h-2 rounded-full transition-all"
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
                    <span className="text-slate-500 font-medium">Mastery Quotient</span>
                    <span className="font-black text-cyan-700 text-sm">
                      {selectedSkill.masteryPercentage}%
                    </span>
                  </div>
                </div>

                {/* Prerequisite Information */}
                {selectedSkill.prerequisiteName && (
                  <div className="mt-4 p-3 rounded-xl bg-amber-50/60 border border-amber-200 text-xs">
                    <span className="font-bold text-amber-900 block mb-1">
                      Dependency Constraint
                    </span>
                    <p className="text-[11px] text-amber-800 flex items-center gap-1.5">
                      <ArrowRight className="w-3.5 h-3.5 shrink-0" />
                      Must unlock & level up <strong>{selectedSkill.prerequisiteName}</strong> to proceed.
                    </p>
                  </div>
                )}

                <div className="mt-5 text-[11px] text-slate-400 text-center font-mono">
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
