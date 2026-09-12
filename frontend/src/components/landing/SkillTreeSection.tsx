import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { SKILL_NODES, SkillNodeItem } from '../../data/landingData';
import { Card } from '../ui/Card';
import { Button } from '../ui/Button';
import { ArrowRight, Zap } from 'lucide-react';

export const SkillTreeSection: React.FC = () => {
  const [selectedId, setSelectedId] = useState<string>('dp');

  const activeSkill =
    SKILL_NODES.find((s) => s.id === selectedId) || SKILL_NODES[0];

  return (
    <section id="skills" className="py-20 max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
      {/* Section Header */}
      <div className="text-center max-w-xl mx-auto mb-12 space-y-2">
        <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight text-slate-900">
          EXPAND YOUR SKILL TREE
        </h2>
        <p className="text-sm sm:text-base text-slate-600">
          Unlock and master core algorithmic specializations as you level up.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-12 gap-8 items-center">
        {/* Left: Interactive Minimalist Node Graph (5 nodes) */}
        <div className="md:col-span-6 flex flex-col items-center justify-center p-6 bg-slate-50 rounded-2xl border border-slate-200/80">
          {/* Root node: DSA */}
          <div className="w-14 h-14 rounded-2xl bg-slate-900 text-white flex flex-col items-center justify-center shadow-md mb-6">
            <span className="text-lg">🧠</span>
            <span className="text-[10px] font-mono font-bold">DSA</span>
          </div>

          {/* Child node connections */}
          <div className="grid grid-cols-2 sm:grid-cols-2 gap-4 w-full">
            {SKILL_NODES.map((node: SkillNodeItem) => {
              const isSelected = node.id === selectedId;

              return (
                <motion.button
                  key={node.id}
                  whileHover={{ scale: 1.04 }}
                  whileTap={{ scale: 0.96 }}
                  onClick={() => setSelectedId(node.id)}
                  className={`p-3.5 rounded-xl border text-center transition-all flex flex-col items-center ${
                    isSelected
                      ? 'bg-white border-cyan-500 shadow-md ring-2 ring-cyan-500/20'
                      : 'bg-white/80 border-slate-200 hover:border-slate-300'
                  }`}
                >
                  <span className="text-xl mb-1">{node.icon}</span>
                  <span className="text-xs font-bold text-slate-800">{node.label}</span>
                  <span className="text-[10px] font-mono text-cyan-600 font-semibold mt-0.5">
                    {node.mastery}%
                  </span>
                </motion.button>
              );
            })}
          </div>
          <p className="text-[11px] font-mono text-slate-400 mt-4">Click a skill to inspect</p>
        </div>

        {/* Right: ONE Selected Skill Card */}
        <div className="md:col-span-6">
          <Card className="p-7 bg-white border-slate-200 shadow-md space-y-4">
            <div className="flex items-start justify-between">
              <div className="flex items-center gap-3">
                <span className="text-3xl p-2 rounded-xl bg-slate-100">{activeSkill.icon}</span>
                <div>
                  <h3 className="text-xl font-bold text-slate-900">{activeSkill.label}</h3>
                  <span className="text-xs font-mono text-slate-500">{activeSkill.category}</span>
                </div>
              </div>

              <span className="text-xs font-mono font-bold text-cyan-700 bg-cyan-50 px-2.5 py-1 rounded-full border border-cyan-200 flex items-center gap-1">
                <Zap className="w-3 h-3 text-cyan-600" />
                {activeSkill.bonus}
              </span>
            </div>

            <p className="text-sm text-slate-600 leading-relaxed">
              {activeSkill.description}
            </p>

            {/* Mastery Meter */}
            <div className="space-y-1.5 pt-2">
              <div className="flex justify-between text-xs font-mono">
                <span className="text-slate-500">Mastery Level</span>
                <span className="font-bold text-slate-900">{activeSkill.mastery}%</span>
              </div>
              <div className="w-full h-2.5 bg-slate-100 rounded-full overflow-hidden">
                <div
                  className="h-full bg-cyan-500 rounded-full transition-all duration-300"
                  style={{ width: `${activeSkill.mastery}%` }}
                />
              </div>
            </div>

            <div className="pt-3">
              <Button
                variant="glow"
                size="sm"
                className="w-full"
                rightIcon={<ArrowRight className="w-3.5 h-3.5" />}
                onClick={() => alert(`Practice mode for ${activeSkill.label} will open in Module 04!`)}
              >
                PRACTICE {activeSkill.label.toUpperCase()}
              </Button>
            </div>
          </Card>
        </div>
      </div>
    </section>
  );
};
