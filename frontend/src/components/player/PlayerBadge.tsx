import React from 'react';
import { PlayerSummary } from '../../types/common';
import { Shield, Zap } from 'lucide-react';
import { Badge } from '../ui/Badge';

export interface PlayerBadgeProps {
  player: PlayerSummary;
}

export const PlayerBadge: React.FC<PlayerBadgeProps> = ({ player }) => {
  return (
    <div className="flex items-center gap-3 p-2.5 rounded-lg border border-slate-200 bg-white/70 backdrop-blur-sm">
      <div className="w-10 h-10 rounded-full bg-gradient-to-tr from-cyan-500 to-violet-600 flex items-center justify-center text-white font-bold text-sm shadow-sm">
        {player.username.slice(0, 2).toUpperCase()}
      </div>
      <div>
        <div className="flex items-center gap-2">
          <span className="font-semibold text-slate-800 text-sm">{player.username}</span>
          <Badge variant="purple" size="sm">
            LVL {player.level}
          </Badge>
        </div>
        <div className="flex items-center gap-3 text-xs text-slate-500 mt-0.5 font-mono">
          <span className="flex items-center gap-1 text-amber-600 font-medium">
            <Shield className="w-3.5 h-3.5" />
            {player.rating} MMR
          </span>
          <span className="flex items-center gap-1 text-cyan-600 font-medium">
            <Zap className="w-3.5 h-3.5" />
            {player.xp} XP
          </span>
        </div>
      </div>
    </div>
  );
};
