import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Badge } from '../components/ui/Badge';
import {
  Swords,
  Sparkles,
  Shield,
  Zap,
  Cpu,
  Flame,
  Bug,
  Check,
  UserCheck,
  ArrowRight,
} from 'lucide-react';
import { motion } from 'framer-motion';

interface AvatarOption {
  id: string;
  name: string;
  archetype: string;
  icon: React.ComponentType<{ className?: string }>;
  bgGradient: string;
  glowColor: string;
  description: string;
}

const AVATAR_OPTIONS: AvatarOption[] = [
  {
    id: 'cyber_samurai',
    name: 'Cyber Samurai',
    archetype: 'Blade Coder',
    icon: Swords,
    bgGradient: 'from-cyan-500 to-blue-600',
    glowColor: 'shadow-cyan-500/30',
    description: 'Strikes bugs with lethal, single-pass algorithmic precision.',
  },
  {
    id: 'byte_sorcerer',
    name: 'Byte Sorcerer',
    archetype: 'Logic Weaver',
    icon: Sparkles,
    bgGradient: 'from-violet-600 to-purple-600',
    glowColor: 'shadow-violet-500/30',
    description: 'Masters dynamic programming and recursive spellcraft.',
  },
  {
    id: 'bug_hunter',
    name: 'Bug Hunter',
    archetype: 'Defensive Sentinel',
    icon: Bug,
    bgGradient: 'from-emerald-500 to-teal-600',
    glowColor: 'shadow-emerald-500/30',
    description: 'Tracks memory leaks and edge cases through the deepest stacks.',
  },
  {
    id: 'pixel_knight',
    name: 'Pixel Knight',
    archetype: 'Core Champion',
    icon: Shield,
    bgGradient: 'from-amber-500 to-orange-600',
    glowColor: 'shadow-amber-500/30',
    description: 'Steadfast architecture, rock-solid tests, unbreakable code.',
  },
  {
    id: 'neon_ninja',
    name: 'Neon Ninja',
    archetype: 'Speed Demon',
    icon: Zap,
    bgGradient: 'from-fuchsia-500 to-pink-600',
    glowColor: 'shadow-pink-500/30',
    description: 'Fastest keystrokes in the queue. Submits before you blink.',
  },
  {
    id: 'algo_sage',
    name: 'Algo Sage',
    archetype: 'System Architect',
    icon: Cpu,
    bgGradient: 'from-blue-600 to-indigo-700',
    glowColor: 'shadow-blue-500/30',
    description: 'Calculates Big-O complexity in their sleep with effortless ease.',
  },
];

export const PlayerCreationPage: React.FC = () => {
  const { user, updateProfile } = useAuth();
  const navigate = useNavigate();

  const [selectedAvatarId, setSelectedAvatarId] = useState<string>(
    user?.avatar || AVATAR_OPTIONS[0].id
  );
  const [displayName, setDisplayName] = useState<string>(
    user?.displayName || user?.username || 'Challenger'
  );
  const [bio, setBio] = useState<string>(
    user?.bio || 'Ready to prove my skills in real-time battle.'
  );
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const selectedAvatar =
    AVATAR_OPTIONS.find((a) => a.id === selectedAvatarId) || AVATAR_OPTIONS[0];

  const handleConfirm = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!displayName.trim()) {
      setError('Display name cannot be empty.');
      return;
    }

    try {
      setIsSaving(true);
      setError(null);
      await updateProfile({
        displayName: displayName.trim(),
        avatar: selectedAvatarId,
        bio: bio.trim(),
      });
      // Navigate to the celebratory Welcome page
      navigate('/welcome');
    } catch (err: unknown) {
      const msg =
        err && typeof err === 'object' && 'message' in err
          ? (err as { message: string }).message
          : 'Failed to save player profile. Please try again.';
      setError(msg);
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <div className="min-h-[90vh] py-10 px-4 sm:px-6 lg:px-8 max-w-6xl mx-auto flex flex-col justify-center">
      {/* Page Title */}
      <motion.div
        initial={{ opacity: 0, y: -15 }}
        animate={{ opacity: 1, y: 0 }}
        className="text-center space-y-2 mb-10"
      >
        <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full text-xs font-mono font-bold uppercase tracking-wider bg-cyan-50 text-cyan-800 border border-cyan-200">
          <Flame className="w-3.5 h-3.5 text-cyan-600 animate-pulse" />
          PLAYER SETUP • STEP 1 OF 2
        </div>
        <h1 className="text-3xl sm:text-4xl font-black text-slate-900 tracking-tight">
          FORGE YOUR ARENA IDENTITY
        </h1>
        <p className="text-sm text-slate-600 max-w-md mx-auto">
          Choose your avatar class and set how fellow challengers will recognize you on the battlefields.
        </p>
      </motion.div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
        {/* ========================================================= */}
        {/* LEFT / CENTER: Avatar picker & profile form (7 cols)       */}
        {/* ========================================================= */}
        <motion.div
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          className="lg:col-span-7 space-y-6"
        >
          <Card className="p-6 sm:p-8 bg-white border-slate-200/90 shadow-lg rounded-2xl">
            <h2 className="text-lg font-bold text-slate-900 mb-4 flex items-center gap-2">
              <Sparkles className="w-5 h-5 text-cyan-600" />
              Choose Your Class Avatar
            </h2>

            {/* Avatar Selector Grid */}
            <div className="grid grid-cols-2 sm:grid-cols-3 gap-3 mb-6">
              {AVATAR_OPTIONS.map((opt) => {
                const isSelected = opt.id === selectedAvatarId;
                const IconComponent = opt.icon;
                return (
                  <button
                    key={opt.id}
                    type="button"
                    onClick={() => setSelectedAvatarId(opt.id)}
                    className={`relative p-3.5 rounded-xl text-left border-2 transition-all flex flex-col items-center justify-center gap-2 group ${
                      isSelected
                        ? `border-cyan-500 bg-cyan-50/50 shadow-md ${opt.glowColor}`
                        : 'border-slate-200 hover:border-slate-300 bg-slate-50/50'
                    }`}
                  >
                    {isSelected && (
                      <div className="absolute top-2 right-2 w-5 h-5 rounded-full bg-cyan-600 text-white flex items-center justify-center shadow-sm">
                        <Check className="w-3 h-3 stroke-[3]" />
                      </div>
                    )}
                    <div
                      className={`w-12 h-12 rounded-xl bg-gradient-to-tr ${opt.bgGradient} text-white flex items-center justify-center shadow group-hover:scale-105 transition-transform`}
                    >
                      <IconComponent className="w-6 h-6" />
                    </div>
                    <div className="text-center">
                      <p className="text-xs font-bold text-slate-900 leading-tight">
                        {opt.name}
                      </p>
                      <p className="text-[10px] font-mono text-slate-500 mt-0.5">
                        {opt.archetype}
                      </p>
                    </div>
                  </button>
                );
              })}
            </div>

            {/* Customization Inputs */}
            <form onSubmit={handleConfirm} className="space-y-4">
              <Input
                label="Player Display Name"
                type="text"
                placeholder="e.g. MasterCoder"
                value={displayName}
                onChange={(e) => setDisplayName(e.target.value)}
                autoComplete="name"
                required
              />

              <div>
                <label className="block text-xs font-mono font-medium text-slate-700 mb-1.5 uppercase tracking-wide">
                  Arena Bio / Battle Cry
                </label>
                <textarea
                  rows={2}
                  maxLength={160}
                  value={bio}
                  onChange={(e) => setBio(e.target.value)}
                  placeholder="Tell rivals your coding strategy or battle motto..."
                  className="w-full px-3.5 py-2.5 rounded-lg border border-slate-300 bg-white text-slate-900 text-sm focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:border-cyan-500 transition-all placeholder:text-slate-400 resize-none font-sans"
                />
                <p className="text-[11px] text-slate-400 font-mono text-right mt-1">
                  {bio.length}/160
                </p>
              </div>

              {error && (
                <p className="text-xs text-rose-600 bg-rose-50 p-2.5 rounded-lg border border-rose-200">
                  {error}
                </p>
              )}

              <Button
                type="submit"
                variant="premium"
                size="lg"
                className="w-full mt-2"
                isLoading={isSaving}
                rightIcon={<ArrowRight className="w-4 h-4" />}
              >
                SAVE & CONTINUE TO ARENA
              </Button>
            </form>
          </Card>
        </motion.div>

        {/* ========================================================= */}
        {/* RIGHT: Live Reactive Player Card Preview (5 cols)         */}
        {/* ========================================================= */}
        <motion.div
          initial={{ opacity: 0, x: 20 }}
          animate={{ opacity: 1, x: 0 }}
          className="lg:col-span-5 space-y-4"
        >
          <div className="flex items-center justify-between px-1">
            <span className="text-xs font-mono font-bold uppercase tracking-wider text-slate-500 flex items-center gap-1.5">
              <UserCheck className="w-4 h-4 text-cyan-600" />
              LIVE BATTLE CARD PREVIEW
            </span>
            <span className="text-[11px] font-mono text-emerald-600 bg-emerald-50 px-2 py-0.5 rounded border border-emerald-200 font-semibold">
              ● Active Live
            </span>
          </div>

          <Card className="p-0 border-slate-200/90 shadow-xl overflow-hidden bg-white">
            {/* Card Banner */}
            <div className={`h-24 bg-gradient-to-r ${selectedAvatar.bgGradient} relative p-4 flex justify-end`}>
              <Badge variant="cyan" size="sm" className="bg-slate-900/80 text-white border-0 shadow">
                NOVICE LEAGUE
              </Badge>
            </div>

            {/* Avatar & Identifiers */}
            <div className="px-6 pb-6 pt-0 relative">
              <div className="flex items-end justify-between -mt-10 mb-4">
                <div
                  className={`w-20 h-20 rounded-2xl bg-gradient-to-tr ${selectedAvatar.bgGradient} text-white flex items-center justify-center shadow-lg border-4 border-white ${selectedAvatar.glowColor}`}
                >
                  <selectedAvatar.icon className="w-10 h-10" />
                </div>
                <div className="text-right font-mono">
                  <span className="text-xs text-slate-400 font-medium block">INITIAL RATING</span>
                  <span className="text-xl font-extrabold text-cyan-700">1,000 MMR</span>
                </div>
              </div>

              {/* Names */}
              <div>
                <h3 className="text-xl font-extrabold text-slate-900 leading-tight">
                  {displayName || 'Anonymous Challenger'}
                </h3>
                <p className="text-xs font-mono text-slate-500 mt-0.5">
                  @{user?.username || 'challenger'} •{' '}
                  <span className="text-cyan-700 font-semibold">{selectedAvatar.archetype}</span>
                </p>
              </div>

              {/* Bio snippet */}
              <div className="mt-3 p-3 rounded-xl bg-slate-50 border border-slate-100 text-xs text-slate-600 italic">
                "{bio || selectedAvatar.description}"
              </div>

              {/* Level & XP Gauge */}
              <div className="mt-4 pt-4 border-t border-slate-100 space-y-1.5">
                <div className="flex items-center justify-between text-xs font-mono font-medium">
                  <span className="text-slate-800 font-bold">LEVEL 1 NOVICE</span>
                  <span className="text-slate-500">0 / 1,000 XP</span>
                </div>
                <div className="w-full h-2.5 bg-slate-100 rounded-full overflow-hidden">
                  <div className="h-full bg-gradient-to-r from-cyan-500 to-blue-600 w-1/12 rounded-full" />
                </div>
              </div>

              {/* Quick combat stats */}
              <div className="grid grid-cols-3 gap-2 mt-4 pt-4 border-t border-slate-100 text-center font-mono">
                <div className="p-2 rounded-lg bg-slate-50">
                  <p className="text-[10px] text-slate-400 uppercase">Battles</p>
                  <p className="text-sm font-extrabold text-slate-900">0</p>
                </div>
                <div className="p-2 rounded-lg bg-slate-50">
                  <p className="text-[10px] text-slate-400 uppercase">Win Rate</p>
                  <p className="text-sm font-extrabold text-slate-900">-%</p>
                </div>
                <div className="p-2 rounded-lg bg-slate-50">
                  <p className="text-[10px] text-slate-400 uppercase">Rank</p>
                  <p className="text-sm font-extrabold text-cyan-600">Bronze I</p>
                </div>
              </div>
            </div>
          </Card>
        </motion.div>
      </div>
    </div>
  );
};
