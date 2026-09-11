import React from 'react';
import { Card } from '../../components/ui/Card';
import { Badge } from '../../components/ui/Badge';
import { Zap, GitBranch, Terminal, Shield, Cpu, Lock } from 'lucide-react';

const SKILL_NODES = [
  { name: 'Sliding Window', level: 1, maxLevel: 5, category: 'Algorithms', icon: Zap, unlocked: true },
  { name: 'Two Pointers', level: 2, maxLevel: 5, category: 'Algorithms', icon: GitBranch, unlocked: true },
  { name: 'Binary Tree Traversal', level: 0, maxLevel: 5, category: 'Data Structures', icon: Cpu, unlocked: false },
  { name: 'Dynamic Programming', level: 0, maxLevel: 5, category: 'Mastery', icon: Shield, unlocked: false },
  { name: 'Graph BFS / DFS', level: 0, maxLevel: 5, category: 'Advanced', icon: Terminal, unlocked: false },
];

export const SkillsPage: React.FC = () => {
  return (
    <div className="space-y-6">
      <div>
        <Badge variant="cyan" size="sm" className="mb-2">
          <Zap className="w-3.5 h-3.5 mr-1" />
          MASTERY MATRIX
        </Badge>
        <h1 className="text-3xl font-black text-slate-900 tracking-tight">
          PLAYER SKILL TREE
        </h1>
        <p className="text-sm text-slate-600 mt-1">
          Unlock nodes by winning matches with specific algorithm archetypes.
        </p>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        {SKILL_NODES.map((node) => {
          const IconComp = node.icon;
          return (
            <Card
              key={node.name}
              className={`p-6 rounded-2xl border transition-all ${
                node.unlocked
                  ? 'bg-white border-cyan-300 shadow-md'
                  : 'bg-slate-50/70 border-slate-200/80 opacity-70'
              }`}
            >
              <div className="flex items-center justify-between mb-4">
                <div
                  className={`w-12 h-12 rounded-xl flex items-center justify-center ${
                    node.unlocked
                      ? 'bg-cyan-500 text-white shadow-sm'
                      : 'bg-slate-200 text-slate-400'
                  }`}
                >
                  <IconComp className="w-6 h-6" />
                </div>
                {node.unlocked ? (
                  <Badge variant="cyan" size="sm">
                    Lvl {node.level}/{node.maxLevel}
                  </Badge>
                ) : (
                  <span className="flex items-center gap-1 text-[11px] font-mono text-slate-400">
                    <Lock className="w-3.5 h-3.5" /> Locked
                  </span>
                )}
              </div>

              <h4 className="text-base font-bold text-slate-900">{node.name}</h4>
              <p className="text-xs font-mono text-slate-500 mt-0.5">{node.category}</p>

              <div className="mt-4 pt-4 border-t border-slate-100 flex gap-1">
                {Array.from({ length: node.maxLevel }).map((_, idx) => (
                  <div
                    key={idx}
                    className={`h-2 flex-1 rounded-full ${
                      idx < node.level ? 'bg-cyan-500' : 'bg-slate-200'
                    }`}
                  />
                ))}
              </div>
            </Card>
          );
        })}
      </div>
    </div>
  );
};
