import React from 'react';
import { Card } from '../../components/ui/Card';
import { Badge } from '../../components/ui/Badge';
import { Award, CheckCircle2, Lock, Flame, Swords, Zap } from 'lucide-react';

const ACHIEVEMENTS = [
  { id: 1, title: 'First Blood', desc: 'Win your first 1v1 battle duel.', xp: 100, unlocked: false, icon: Swords },
  { id: 2, title: 'Speed Demon', desc: 'Submit a 100% passing solution in under 90 seconds.', xp: 250, unlocked: false, icon: Zap },
  { id: 3, title: 'Streak Master', desc: 'Maintain a 5-match victory streak in ranked arena.', xp: 500, unlocked: false, icon: Flame },
  { id: 4, title: 'Challenger Initiation', desc: 'Complete player account creation and avatar forging.', xp: 50, unlocked: true, icon: Award },
];

export const AchievementsPage: React.FC = () => {
  return (
    <div className="space-y-6">
      <div>
        <Badge variant="cyan" size="sm" className="mb-2">
          <Award className="w-3.5 h-3.5 mr-1" />
          HONOR HALL
        </Badge>
        <h1 className="text-3xl font-black text-slate-900 tracking-tight">
          ARENA ACHIEVEMENTS
        </h1>
        <p className="text-sm text-slate-600 mt-1">
          Feats of coding prowess, speed records, and battle trophies.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {ACHIEVEMENTS.map((a) => {
          const IconComp = a.icon;
          return (
            <Card
              key={a.id}
              className={`p-6 rounded-2xl border flex items-start gap-4 ${
                a.unlocked
                  ? 'bg-white border-emerald-300 shadow-sm'
                  : 'bg-slate-50/70 border-slate-200/80 opacity-75'
              }`}
            >
              <div
                className={`w-12 h-12 rounded-xl flex items-center justify-center shrink-0 ${
                  a.unlocked
                    ? 'bg-emerald-500 text-white shadow-sm'
                    : 'bg-slate-200 text-slate-400'
                }`}
              >
                <IconComp className="w-6 h-6" />
              </div>

              <div className="flex-1 min-w-0">
                <div className="flex items-center justify-between">
                  <h4 className="text-base font-bold text-slate-900">{a.title}</h4>
                  <span className="text-xs font-mono font-bold text-cyan-600">+{a.xp} XP</span>
                </div>
                <p className="text-xs text-slate-600 mt-1">{a.desc}</p>
                <div className="mt-3">
                  {a.unlocked ? (
                    <span className="inline-flex items-center gap-1 text-[11px] font-mono text-emerald-600 font-bold">
                      <CheckCircle2 className="w-3.5 h-3.5" /> Unlocked
                    </span>
                  ) : (
                    <span className="inline-flex items-center gap-1 text-[11px] font-mono text-slate-400">
                      <Lock className="w-3.5 h-3.5" /> In Progress
                    </span>
                  )}
                </div>
              </div>
            </Card>
          );
        })}
      </div>
    </div>
  );
};
