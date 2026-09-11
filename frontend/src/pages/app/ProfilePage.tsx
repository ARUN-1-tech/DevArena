import React, { useState } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { Card } from '../../components/ui/Card';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { Badge } from '../../components/ui/Badge';
import { User, Check, Save } from 'lucide-react';

export const ProfilePage: React.FC = () => {
  const { user, updateProfile } = useAuth();

  const [displayName, setDisplayName] = useState(user?.displayName || '');
  const [bio, setBio] = useState(user?.bio || '');
  const [isSaving, setIsSaving] = useState(false);
  const [saveSuccess, setSaveSuccess] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setIsSaving(true);
      setError(null);
      await updateProfile({
        displayName: displayName.trim(),
        bio: bio.trim(),
      });
      setSaveSuccess(true);
      setTimeout(() => setSaveSuccess(false), 3000);
    } catch (err: unknown) {
      const msg =
        err && typeof err === 'object' && 'message' in err
          ? (err as { message: string }).message
          : 'Failed to update profile.';
      setError(msg);
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <div className="space-y-6 max-w-3xl">
      <div>
        <Badge variant="cyan" size="sm" className="mb-2">
          <User className="w-3.5 h-3.5 mr-1" />
          CHALLENGER DOSSIER
        </Badge>
        <h1 className="text-3xl font-black text-slate-900 tracking-tight">
          PLAYER PROFILE
        </h1>
        <p className="text-sm text-slate-600 mt-1">
          Manage your arena public identity, bio, and player settings.
        </p>
      </div>

      <Card className="p-6 sm:p-8 bg-white border-slate-200/90 shadow-sm rounded-2xl">
        <form onSubmit={handleSave} className="space-y-5">
          <div className="flex items-center gap-4 pb-6 border-b border-slate-100">
            <div className="w-16 h-16 rounded-2xl bg-gradient-to-tr from-cyan-600 to-violet-600 text-white flex items-center justify-center text-xl font-black shadow-md">
              {(displayName || user?.username || 'P').slice(0, 2).toUpperCase()}
            </div>
            <div>
              <h3 className="text-lg font-bold text-slate-900">
                {displayName || user?.username}
              </h3>
              <p className="text-xs font-mono text-slate-500">
                @{user?.username} • {user?.email}
              </p>
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Input
              label="Username (Permanent ID)"
              value={user?.username || ''}
              disabled
            />
            <Input
              label="Display Name"
              value={displayName}
              onChange={(e) => setDisplayName(e.target.value)}
              required
            />
          </div>

          <div>
            <label className="block text-xs font-mono font-medium text-slate-700 mb-1.5 uppercase tracking-wide">
              Arena Bio / Motto
            </label>
            <textarea
              rows={3}
              maxLength={160}
              value={bio}
              onChange={(e) => setBio(e.target.value)}
              placeholder="Write a brief motto for your arena challenger card..."
              className="w-full px-3.5 py-2.5 rounded-lg border border-slate-300 bg-white text-slate-900 text-sm focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:border-cyan-500 transition-all resize-none"
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

          {saveSuccess && (
            <p className="text-xs text-emerald-700 bg-emerald-50 p-2.5 rounded-lg border border-emerald-200 flex items-center gap-1.5">
              <Check className="w-4 h-4" /> Profile updated successfully!
            </p>
          )}

          <div className="pt-2">
            <Button
              type="submit"
              variant="glow"
              size="md"
              isLoading={isSaving}
              leftIcon={<Save className="w-4 h-4" />}
            >
              SAVE CHANGES
            </Button>
          </div>
        </form>
      </Card>
    </div>
  );
};
