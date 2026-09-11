import React from 'react';
import { Card } from '../../components/ui/Card';
import { Button } from '../../components/ui/Button';
import { Badge } from '../../components/ui/Badge';
import { Swords, Zap, Trophy } from 'lucide-react';

export const ArenaPage: React.FC = () => {
  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <Badge variant="cyan" size="sm" className="mb-2">
            <Swords className="w-3.5 h-3.5 mr-1" />
            LIVE COMBAT QUEUE
          </Badge>
          <h1 className="text-3xl font-black text-slate-900 tracking-tight">
            THE BATTLE ARENA
          </h1>
          <p className="text-sm text-slate-600 mt-1">
            Real-time 1v1 algorithmic duels. Battle engine launching in Module 04.
          </p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Card className="p-8 bg-white border-slate-200/90 shadow-md rounded-2xl space-y-4">
          <div className="w-12 h-12 rounded-xl bg-cyan-50 text-cyan-600 flex items-center justify-center">
            <Zap className="w-6 h-6" />
          </div>
          <h3 className="text-xl font-bold text-slate-900">Ranked Matchmaking</h3>
          <p className="text-sm text-slate-600 leading-relaxed">
            Match with challengers in your MMR rating window (1,000 ± 150). Code real-time in the battle arena with live test runner and anti-cheat validation.
          </p>
          <Button variant="glow" size="md" disabled>
            MATCHMAKING QUEUE (MODULE 04)
          </Button>
        </Card>

        <Card className="p-8 bg-white border-slate-200/90 shadow-md rounded-2xl space-y-4">
          <div className="w-12 h-12 rounded-xl bg-violet-50 text-violet-600 flex items-center justify-center">
            <Trophy className="w-6 h-6" />
          </div>
          <h3 className="text-xl font-bold text-slate-900">Private Custom Duel</h3>
          <p className="text-sm text-slate-600 leading-relaxed">
            Invite friends or teammates to a custom room with custom language choices, time limits, and test difficulty.
          </p>
          <Button variant="outline" size="md" disabled>
            CREATE CUSTOM LOBBY (MODULE 04)
          </Button>
        </Card>
      </div>
    </div>
  );
};
