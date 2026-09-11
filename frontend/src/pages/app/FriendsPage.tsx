import React from 'react';
import { Card } from '../../components/ui/Card';
import { Badge } from '../../components/ui/Badge';
import { Button } from '../../components/ui/Button';
import { Users, UserPlus } from 'lucide-react';
import { Input } from '../../components/ui/Input';

export const FriendsPage: React.FC = () => {
  return (
    <div className="space-y-6">
      <div>
        <Badge variant="cyan" size="sm" className="mb-2">
          <Users className="w-3.5 h-3.5 mr-1" />
          CHALLENGER GUILD
        </Badge>
        <h1 className="text-3xl font-black text-slate-900 tracking-tight">
          FRIENDS & RIVALS
        </h1>
        <p className="text-sm text-slate-600 mt-1">
          Challenge friends to friendly 1v1 scrims and compare stats.
        </p>
      </div>

      <div className="flex gap-3">
        <Input placeholder="Search rivals by username or handle..." />
        <Button variant="glow" leftIcon={<UserPlus className="w-4 h-4" />}>
          ADD
        </Button>
      </div>

      <Card className="p-12 text-center bg-white border-slate-200/90 shadow-sm rounded-2xl space-y-3">
        <div className="w-14 h-14 rounded-2xl bg-cyan-50 text-cyan-600 flex items-center justify-center mx-auto">
          <Users className="w-7 h-7" />
        </div>
        <h3 className="text-lg font-bold text-slate-900">No Rivalries Yet</h3>
        <p className="text-xs text-slate-500 max-w-sm mx-auto">
          Add friends or battle random opponents in the Arena to populate your rival list.
        </p>
      </Card>
    </div>
  );
};
