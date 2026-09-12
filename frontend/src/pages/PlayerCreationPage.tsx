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
    bgGradient: 'from-sandwich-600 to-sandwich-800',
    glowColor: 'shadow-sandwich-500/30',
    description: 'Strikes bugs with lethal, single-pass algorithmic precision.',
  },
  {
    id: 'byte_sorcerer',
    name: 'Byte Sorcerer',
    archetype: 'Logic Weaver',
    icon: Sparkles,
    bgGradient: 'from-sandwich-500 to-sandwich-700',
    glowColor: 'shadow-sandwich-400/30',
    description: 'Masters dynamic programming and recursive spellcraft.',
  },
  {
    id: 'bug_hunter',
    name: 'Bug Hunter',
    archetype: 'Defensive Sentinel',
    icon: Bug,
    bgGradient: 'from-sandwich-600 to-sandwich-850',
    glowColor: 'shadow-sandwich-500/30',
    description: 'Tracks memory leaks and edge cases through the deepest stacks.',
  },
  {
    id: 'pixel_knight',
    name: 'Pixel Knight',
    archetype: 'Core Champion',
    icon: Shield,
    bgGradient: 'from-sandwich-500 to-sandwich-800',
    glowColor: 'shadow-sandwich-400/30',
    description: 'Steadfast architecture, rock-solid tests, unbreakable code.',
  },
  {
    id: 'neon_ninja',
    name: 'Neon Ninja',
    archetype: 'Speed Demon',
    icon: Zap,
    bgGradient: 'from-sandwich-400 to-sandwich-700',
    glowColor: 'shadow-sandwich-300/30',
    description: 'Fastest keystrokes in the queue. Submits before you blink.',
  },
  {
    id: 'algo_sage',
    name: 'Algo Sage',
    archetype: 'System Architect',
    icon: Cpu,
    bgGradient: 'from-sandwich-600 to-sandwich-900',
    glowColor: 'shadow-sandwich-400/30',
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
        <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full text-xs font-mono font-bold uppercase tracking-wider bg-sandwich-900 border border-sandwich-700 text-sandwich-200">
          <Flame className="w-3.5 h-3.5 text-sandwich-100 animate-pulse" />
          PLAYER SETUP • STEP 1 OF 2
        </div>
        <h1 className="text-3xl sm:text-4xl font-black text-sandwich-50 tracking-tight">
          FORGE YOUR ARENA IDENTITY
        </h1>
        <p className="text-sm text-sandwich-300 max-w-md mx-auto">
          Choose your avatar class and set how fellow challengers will recognize you on the battlefields.
        </p>
      </motion.div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
        {/* LEFT / CENTER: Avatar picker & profile form */}
        <motion.div
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          className="lg:col-span-7 space-y-6"
        >
          <Card className="p-6 sm:p-8 bg-sandwich-900/90 border-sandwich-700/80 shadow-luxury-card rounded-2xl backdrop-blur-md">
            <h2 className="text-lg font-bold text-sandwich-100 mb-4 flex items-center gap-2">
              <Sparkles className="w-5 h-5 text-sandwich-200" />
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
                        ? `border-sandwich-400 bg-sandwich-800 shadow-glow-silver`
                        : 'border-sandwich-800 hover:border-sandwich-600 bg-sandwich-950/70'
                    }`}
                  >
                    {isSelected && (
                      <div className="absolute top-2 right-2 w-5 h-5 rounded-full bg-sandwich-50 text-sandwich-950 flex items-center justify-center shadow-sm">
                        <Check className="w-3 h-3 stroke-[3]" />
                      </div>
                    )}
                    <div
                      className={`w-12 h-12 rounded-xl bg-gradient-to-tr ${opt.bgGradient} text-sandwich-50 border border-sandwich-600 flex items-center justify-center shadow group-hover:scale-105 transition-transform`}
                    >
                      <IconComponent className="w-6 h-6" />
                    </div>
                    <div className="text-center">
                      <p className="text-xs font-bold text-sandwich-100 leading-tight">
                        {opt.name}
                      </p>
                      <p className="text-[10px] font-mono text-sandwich-400 mt-0.5">
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
                <label className="block text-xs font-mono font-medium text-sandwich-300 mb-1.5 uppercase tracking-wide">
                  Arena Bio / Battle Cry
                </label>
                <textarea
                  rows={2}
                  maxLength={160}
                  value={bio}
                  onChange={(e) => setBio(e.target.value)}
                  placeholder="Tell rivals your coding strategy or battle motto..."
                  className="w-full px-3.5 py-2.5 rounded-lg border border-sandwich-700 bg-sandwich-950 text-sandwich-100 text-sm focus:outline-none focus:ring-2 focus:ring-sandwich-400 focus:border-sandwich-400 transition-all placeholder:text-sandwich-500 resize-none font-sans"
                />
                <p className="text-[11px] text-sandwich-500 font-mono text-right mt-1">
                  {bio.length}/160
                </p>
              </div>

              {error && (
                <p className="text-xs text-red-300 bg-red-950/40 p-2.5 rounded-lg border border-red-800/80">
                  {error}
                </p>
              )}

              <Button
                type="submit"
                variant="primary"
                size="lg"
                className="w-full mt-2"
                isLoading={isSaving}
                rightIcon={<ArrowRight className="w-4 h-4 text-sandwich-950" />}
              >
                SAVE & CONTINUE TO ARENA
              </Button>
            </form>
          </Card>
        </motion.div>

        {/* RIGHT: Live Reactive Player Card Preview */}
        <motion.div
          initial={{ opacity: 0, x: 20 }}
          animate={{ opacity: 1, x: 0 }}
          className="lg:col-span-5 space-y-4"
        >
          <div className="flex items-center justify-between px-1">
            <span className="text-xs font-mono font-bold uppercase tracking-wider text-sandwich-400 flex items-center gap-1.5">
              <UserCheck className="w-4 h-4 text-sandwich-200" />
              LIVE BATTLE CARD PREVIEW
            </span>
            <span className="text-[11px] font-mono text-sandwich-200 bg-sandwich-900 px-2 py-0.5 rounded border border-sandwich-700 font-semibold">
              ● Active Live
            </span>
          </div>

          <Card className="p-0 border-sandwich-700/80 shadow-luxury-card overflow-hidden bg-sandwich-900/90">
            {/* Card Banner */}
            <div className={`h-24 bg-gradient-to-r ${selectedAvatar.bgGradient} relative p-4 flex justify-end`}>
              <Badge variant="default" size="sm" className="bg-sandwich-950/90 text-sandwich-100 border border-sandwich-700 shadow">
                NOVICE LEAGUE
              </Badge>
            </div>

            {/* Avatar & Identifiers */}
            <div className="px-6 pb-6 pt-0 relative">
              <div className="flex items-end justify-between -mt-10 mb-4">
                <div
                  className={`w-20 h-20 rounded-2xl bg-gradient-to-tr ${selectedAvatar.bgGradient} text-white flex items-center justify-center shadow-lg border-4 border-sandwich-900 ${selectedAvatar.glowColor}`}
                >
                  <selectedAvatar.icon className="w-10 h-10" />
                </div>
                <div className="text-right font-mono">
                  <span className="text-xs text-sandwich-400 font-medium block">INITIAL RATING</span>
                  <span className="text-xl font-extrabold text-sandwich-100">1,000 MMR</span>
                </div>
              </div>

              {/* Names */}
              <div>
                <h3 className="text-xl font-extrabold text-sandwich-50 leading-tight">
                  {displayName || 'Anonymous Challenger'}
                </h3>
                <p className="text-xs font-mono text-sandwich-400 mt-0.5">
                  @{user?.username || 'challenger'} •{' '}
                  <span className="text-sandwich-200 font-semibold">{selectedAvatar.archetype}</span>
                </p>
              </div>

              {/* Bio snippet */}
              <div className="mt-3 p-3 rounded-xl bg-sandwich-950 border border-sandwich-800 text-xs text-sandwich-300 italic">
                "{bio || selectedAvatar.description}"
              </div>

              {/* Level & XP Gauge */}
              <div className="mt-4 pt-4 border-t border-sandwich-800 space-y-1.5">
                <div className="flex items-center justify-between text-xs font-mono font-medium">
                  <span className="text-sandwich-200 font-bold">LEVEL 1 NOVICE</span>
                  <span className="text-sandwich-400">0 / 1,000 XP</span>
                </div>
                <div className="w-full h-2.5 bg-sandwich-950 border border-sandwich-800 rounded-full overflow-hidden">
                  <div className="h-full bg-gradient-to-r from-sandwich-400 to-sandwich-50 w-1/12 rounded-full shadow-glow-silver" />
                </div>
              </div>

              {/* Quick combat stats */}
              <div className="grid grid-cols-3 gap-2 mt-4 pt-4 border-t border-sandwich-800 text-center font-mono">
                <div className="p-2 rounded-lg bg-sandwich-950 border border-sandwich-850">
                  <p className="text-[10px] text-sandwich-400 uppercase">Battles</p>
                  <p className="text-sm font-extrabold text-sandwich-100">0</p>
                </div>
                <div className="p-2 rounded-lg bg-sandwich-950 border border-sandwich-850">
                  <p className="text-[10px] text-sandwich-400 uppercase">Win Rate</p>
                  <p className="text-sm font-extrabold text-sandwich-100">-%</p>
                </div>
                <div className="p-2 rounded-lg bg-sandwich-950 border border-sandwich-850">
                  <p className="text-[10px] text-sandwich-400 uppercase">Rank</p>
                  <p className="text-sm font-extrabold text-sandwich-100">Bronze I</p>
                </div>
              </div>
            </div>
          </Card>
        </motion.div>
      </div>
    </div>
  );
};
