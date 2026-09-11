import React from 'react';
import { Card } from '../ui/Card';
import { Badge } from '../ui/Badge';
import { Code2, ArrowRight } from 'lucide-react';
import { ChallengeDifficulty } from '../../types/common';

export interface ChallengeCardStubProps {
  title: string;
  category: string;
  difficulty: ChallengeDifficulty;
  xpReward: number;
}

export const ChallengeCardStub: React.FC<ChallengeCardStubProps> = ({
  title,
  category,
  difficulty,
  xpReward,
}) => {
  const difficultyVariant = {
    EASY: 'success',
    MEDIUM: 'warning',
    HARD: 'danger',
    EXPERT: 'purple',
  } as const;

  return (
    <Card hoverEffect className="group">
      <div className="flex items-start justify-between gap-3">
        <div className="flex items-center gap-2 text-cyan-600">
          <Code2 className="w-5 h-5" />
          <span className="text-xs font-mono uppercase tracking-wider text-slate-500 font-semibold">
            {category}
          </span>
        </div>
        <Badge variant={difficultyVariant[difficulty]} size="sm">
          {difficulty}
        </Badge>
      </div>
      <h3 className="text-base font-semibold text-slate-800 mt-2.5 group-hover:text-cyan-600 transition-colors">
        {title}
      </h3>
      <div className="flex items-center justify-between mt-4 pt-3 border-t border-slate-100 text-xs text-slate-500">
        <span className="font-mono text-emerald-600 font-semibold">+{xpReward} XP</span>
        <span className="flex items-center gap-1 font-medium group-hover:translate-x-1 transition-transform text-slate-400 group-hover:text-slate-700">
          Preview <ArrowRight className="w-3.5 h-3.5" />
        </span>
      </div>
    </Card>
  );
};
