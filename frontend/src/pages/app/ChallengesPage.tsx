import React from 'react';
import { Card } from '../../components/ui/Card';
import { Badge } from '../../components/ui/Badge';
import { Code2, Search } from 'lucide-react';
import { Input } from '../../components/ui/Input';

const SAMPLE_CHALLENGES = [
  { id: 1, title: 'Two Sum', difficulty: 'Easy', category: 'Arrays & Hashing', xp: 50, completionRate: '94%' },
  { id: 2, title: 'Longest Substring Without Repeating Characters', difficulty: 'Medium', category: 'Sliding Window', xp: 120, completionRate: '72%' },
  { id: 3, title: 'Median of Two Sorted Arrays', difficulty: 'Hard', category: 'Binary Search', xp: 250, completionRate: '41%' },
  { id: 4, title: 'Valid Parentheses', difficulty: 'Easy', category: 'Stack', xp: 50, completionRate: '89%' },
  { id: 5, title: 'Merge k Sorted Lists', difficulty: 'Hard', category: 'Heap / Priority Queue', xp: 250, completionRate: '48%' },
];

export const ChallengesPage: React.FC = () => {
  return (
    <div className="space-y-6">
      <div>
        <Badge variant="cyan" size="sm" className="mb-2">
          <Code2 className="w-3.5 h-3.5 mr-1" />
          KATA ARCHIVE
        </Badge>
        <h1 className="text-3xl font-black text-slate-900 tracking-tight">
          PRACTICE CHALLENGES
        </h1>
        <p className="text-sm text-slate-600 mt-1">
          Explore algorithmic katas across data structures and patterns.
        </p>
      </div>

      <div className="flex flex-col sm:flex-row gap-3">
        <div className="flex-1">
          <Input
            placeholder="Search problems by name, tag, or concept..."
            leftIcon={<Search className="w-4 h-4" />}
          />
        </div>
      </div>

      <Card className="p-0 border-slate-200/90 shadow-sm overflow-hidden bg-white rounded-2xl">
        <div className="divide-y divide-slate-100">
          {SAMPLE_CHALLENGES.map((ch) => (
            <div
              key={ch.id}
              className="p-4 sm:p-5 flex items-center justify-between hover:bg-slate-50 transition-colors"
            >
              <div className="space-y-1">
                <div className="flex items-center gap-2.5">
                  <span className="text-sm font-bold text-slate-900">{ch.title}</span>
                  <span
                    className={`text-[10px] font-mono px-2 py-0.5 rounded font-semibold ${
                      ch.difficulty === 'Easy'
                        ? 'bg-emerald-50 text-emerald-700 border border-emerald-200'
                        : ch.difficulty === 'Medium'
                        ? 'bg-amber-50 text-amber-700 border border-amber-200'
                        : 'bg-rose-50 text-rose-700 border border-rose-200'
                    }`}
                  >
                    {ch.difficulty}
                  </span>
                </div>
                <p className="text-xs font-mono text-slate-500">{ch.category}</p>
              </div>

              <div className="text-right font-mono">
                <span className="text-xs font-bold text-cyan-600 block">+{ch.xp} XP</span>
                <span className="text-[11px] text-slate-400">{ch.completionRate} pass</span>
              </div>
            </div>
          ))}
        </div>
      </Card>
    </div>
  );
};
