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
        <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight text-sandwich-50">
          EXPAND YOUR SKILL TREE
        </h2>
        <p className="text-sm sm:text-base text-sandwich-300">
          Unlock and master core algorithmic specializations as you level up.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-12 gap-8 items-center">
        {/* Left: Interactive Minimalist Node Graph (5 nodes) */}
        <div className="md:col-span-6 flex flex-col items-center justify-center p-6 bg-sandwich-900/90 rounded-2xl border border-sandwich-700/80 shadow-luxury-card">
          {/* Root node: DSA */}
          <div className="w-14 h-14 rounded-2xl bg-sandwich-800 text-sandwich-50 border border-sandwich-600 flex flex-col items-center justify-center shadow-md mb-6">
            <span className="text-lg">🧠</span>
            <span className="text-[10px] font-mono font-bold text-sandwich-200">DSA</span>
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
                      ? 'bg-sandwich-800 border-sandwich-400 shadow-glow-silver ring-2 ring-sandwich-400/20 text-sandwich-50'
                      : 'bg-sandwich-950/70 border-sandwich-800 hover:border-sandwich-600 text-sandwich-300'
                  }`}
                >
                  <span className="text-xl mb-1">{node.icon}</span>
                  <span className="text-xs font-bold text-sandwich-100">{node.label}</span>
                  <span className="text-[10px] font-mono text-sandwich-300 font-semibold mt-0.5">
                    {node.mastery}%
                  </span>
                </motion.button>
              );
            })}
          </div>
          <p className="text-[11px] font-mono text-sandwich-400 mt-4">Click a skill to inspect</p>
        </div>

        {/* Right: ONE Selected Skill Card */}
        <div className="md:col-span-6">
          <Card className="p-7 bg-sandwich-900/90 border-sandwich-700/80 shadow-luxury-card space-y-4">
            <div className="flex items-start justify-between">
              <div className="flex items-center gap-3">
                <span className="text-3xl p-2 rounded-xl bg-sandwich-800 border border-sandwich-700">{activeSkill.icon}</span>
                <div>
                  <h3 className="text-xl font-bold text-sandwich-100">{activeSkill.label}</h3>
                  <span className="text-xs font-mono text-sandwich-400">{activeSkill.category}</span>
                </div>
              </div>

              <span className="text-xs font-mono font-bold text-sandwich-200 bg-sandwich-800 px-2.5 py-1 rounded-full border border-sandwich-700 flex items-center gap-1">
                <Zap className="w-3 h-3 text-sandwich-300" />
                {activeSkill.bonus}
              </span>
            </div>

            <p className="text-sm text-sandwich-300 leading-relaxed">
              {activeSkill.description}
            </p>

            {/* Mastery Meter */}
            <div className="space-y-1.5 pt-2">
              <div className="flex justify-between text-xs font-mono">
                <span className="text-sandwich-400">Mastery Level</span>
                <span className="font-bold text-sandwich-100">{activeSkill.mastery}%</span>
              </div>
              <div className="w-full h-2.5 bg-sandwich-950 border border-sandwich-800 rounded-full overflow-hidden">
                <div
                  className="h-full bg-sandwich-50 rounded-full shadow-glow-white transition-all duration-300"
                  style={{ width: `${activeSkill.mastery}%` }}
                />
              </div>
            </div>

            <div className="pt-3">
              <Button
                variant="primary"
                size="sm"
                className="w-full"
                rightIcon={<ArrowRight className="w-3.5 h-3.5 text-sandwich-950" />}
                onClick={() => {
                  window.location.href = '/skills';
                }}
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
