import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { SKILL_TREE_DOMAINS, SkillNode } from '../../data/landingData';
import { Card } from '../ui/Card';
import { Badge } from '../ui/Badge';
import { Button } from '../ui/Button';
import { Network, Lock, CheckCircle2, Zap, ArrowUpRight } from 'lucide-react';

export const SkillTreeSection: React.FC = () => {
  const [selectedNodeId, setSelectedNodeId] = useState<string>('dp');

  const selectedNode =
    SKILL_TREE_DOMAINS.find((n) => n.id === selectedNodeId) || SKILL_TREE_DOMAINS[0];

  const statusVariant = {
    MASTERED: 'success',
    IN_PROGRESS: 'info',
    LOCKED: 'default',
  } as const;

  return (
    <section id="skills" className="py-20 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      {/* Section Header */}
      <div className="text-center max-w-2xl mx-auto mb-14 space-y-3">
        <Badge variant="info">SKILL TREE PROGRESSION</Badge>
        <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight text-slate-900">
          INTERACTIVE DEVELOPER SKILL TREE
        </h2>
        <p className="text-base text-slate-600">
          Build a specialized developer archetype. Invest skill points earned from battles into
          algorithmic specializations, memory optimization, and distributed system design.
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
        {/* Interactive Skill Graph Grid (Left / Top) */}
        <div className="lg:col-span-7 grid grid-cols-1 sm:grid-cols-2 gap-4">
          {SKILL_TREE_DOMAINS.map((node: SkillNode) => {
            const isSelected = selectedNodeId === node.id;
            const isMastered = node.status === 'MASTERED';
            const isLocked = node.status === 'LOCKED';

            return (
              <motion.div
                key={node.id}
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
                onClick={() => setSelectedNodeId(node.id)}
              >
                <Card
                  className={`cursor-pointer transition-all p-5 h-full flex flex-col justify-between ${
                    isSelected
                      ? 'border-cyan-500 ring-2 ring-cyan-500/20 shadow-glow-cyan bg-cyan-50/20'
                      : isLocked
                      ? 'opacity-70 bg-slate-50/80 border-slate-200'
                      : 'border-slate-200/90 hover:border-slate-300'
                  }`}
                >
                  <div>
                    <div className="flex items-center justify-between mb-3">
                      <span className="text-[10px] font-mono font-bold uppercase tracking-wider text-slate-400">
                        {node.category}
                      </span>
                      <Badge variant={statusVariant[node.status]} size="sm">
                        {isLocked ? (
                          <span className="flex items-center gap-1">
                            <Lock className="w-2.5 h-2.5" /> LOCKED
                          </span>
                        ) : isMastered ? (
                          <span className="flex items-center gap-1">
                            <CheckCircle2 className="w-2.5 h-2.5" /> MASTERED
                          </span>
                        ) : (
                          'ACTIVE'
                        )}
                      </Badge>
                    </div>

                    <h3 className="text-base font-bold text-slate-900 mb-1">{node.name}</h3>
                    <p className="text-xs font-mono text-cyan-600 font-medium">
                      Tier Level {node.level} / {node.maxLevel}
                    </p>
                  </div>

                  {/* Progress Bar */}
                  <div className="mt-4 pt-3 border-t border-slate-100 space-y-1.5">
                    <div className="flex justify-between text-[11px] font-mono text-slate-500">
                      <span>Mastery</span>
                      <span className="font-semibold text-slate-800">{node.masteryPercent}%</span>
                    </div>
                    <div className="w-full h-2 bg-slate-100 rounded-full overflow-hidden">
                      <div
                        className={`h-full rounded-full transition-all duration-500 ${
                          isMastered
                            ? 'bg-emerald-500'
                            : isLocked
                            ? 'bg-slate-300'
                            : 'bg-cyan-500'
                        }`}
                        style={{ width: `${node.masteryPercent}%` }}
                      />
                    </div>
                  </div>
                </Card>
              </motion.div>
            );
          })}
        </div>

        {/* Selected Skill Detail Inspector (Right Side) */}
        <div className="lg:col-span-5">
          <Card className="p-6 sm:p-7 bg-white border-slate-200 shadow-md sticky top-24">
            <div className="flex items-center justify-between pb-4 border-b border-slate-100">
              <div className="flex items-center gap-2 text-cyan-600">
                <Network className="w-5 h-5" />
                <span className="text-xs font-mono uppercase tracking-widest font-bold text-slate-700">
                  NODE TELEMETRY
                </span>
              </div>
              <Badge variant={statusVariant[selectedNode.status]}>
                {selectedNode.status}
              </Badge>
            </div>

            <div className="mt-5 space-y-4">
              <div>
                <h3 className="text-2xl font-extrabold text-slate-900">{selectedNode.name}</h3>
                <p className="text-xs font-mono text-slate-500 mt-0.5">
                  Category: {selectedNode.category} · Specialization Branch
                </p>
              </div>

              <p className="text-sm text-slate-600 leading-relaxed">
                {selectedNode.description}
              </p>

              {/* Passive XP Buff */}
              <div className="p-3.5 rounded-xl bg-cyan-50/70 border border-cyan-200/80 flex items-center gap-3">
                <div className="w-8 h-8 rounded-lg bg-cyan-100 text-cyan-700 flex items-center justify-center shrink-0">
                  <Zap className="w-4 h-4" />
                </div>
                <div>
                  <span className="text-xs font-bold text-cyan-900 block">Passive Arena Bonus</span>
                  <span className="text-xs font-mono text-cyan-700 font-semibold">
                    {selectedNode.xpBonus}
                  </span>
                </div>
              </div>

              {/* Prerequisites */}
              <div className="space-y-1.5 pt-2">
                <span className="text-xs font-mono uppercase tracking-wider text-slate-500 font-semibold block">
                  Prerequisites Required:
                </span>
                <div className="flex flex-wrap gap-2">
                  {selectedNode.prerequisites.map((prereq) => (
                    <span
                      key={prereq}
                      className="text-xs font-mono px-2.5 py-1 rounded bg-slate-100 text-slate-700 border border-slate-200"
                    >
                      {prereq}
                    </span>
                  ))}
                </div>
              </div>

              <div className="pt-4 border-t border-slate-100 space-y-2">
                <Button
                  variant="glow"
                  className="w-full"
                  rightIcon={<ArrowUpRight className="w-4 h-4" />}
                  onClick={() => alert(`Entering practice challenges for: ${selectedNode.name}`)}
                >
                  Practice {selectedNode.name}
                </Button>
                <p className="text-[11px] font-mono text-slate-400 text-center">
                  +1 Skill Point awarded every 1,000 XP gained
                </p>
              </div>
            </div>
          </Card>
        </div>
      </div>
    </section>
  );
};
