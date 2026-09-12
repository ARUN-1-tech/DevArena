import React, { useEffect, useState } from 'react';
import {
  Users,
  Search,
  ShieldAlert,
  UserCheck,
  UserX,
  RefreshCw,
  AlertTriangle,
} from 'lucide-react';
import { adminService } from '../../services/adminService';
import { AdminPlayerDto } from '../../types/admin';

export const AdminPlayersPage: React.FC = () => {
  const [players, setPlayers] = useState<AdminPlayerDto[]>([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Suspension modal state
  const [selectedPlayer, setSelectedPlayer] = useState<AdminPlayerDto | null>(null);
  const [suspendReason, setSuspendReason] = useState('');
  const [actionLoading, setActionLoading] = useState(false);

  const fetchPlayers = async (searchQuery = '') => {
    setLoading(true);
    setError(null);
    try {
      const data = await adminService.getPlayers(searchQuery);
      setPlayers(data.content);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to fetch players');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPlayers();
  }, []);

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    fetchPlayers(search);
  };

  const handleSuspend = async () => {
    if (!selectedPlayer || !suspendReason.trim()) return;
    setActionLoading(true);
    try {
      const updated = await adminService.suspendPlayer(selectedPlayer.id, suspendReason.trim());
      setPlayers((prev) => prev.map((p) => (p.id === updated.id ? updated : p)));
      setSelectedPlayer(null);
      setSuspendReason('');
    } catch (err: unknown) {
      alert(err instanceof Error ? err.message : 'Failed to suspend player');
    } finally {
      setActionLoading(false);
    }
  };

  const handleRestore = async (player: AdminPlayerDto) => {
    if (!confirm(`Are you sure you want to restore access for ${player.username}?`)) return;
    try {
      const updated = await adminService.restorePlayer(player.id);
      setPlayers((prev) => prev.map((p) => (p.id === updated.id ? updated : p)));
    } catch (err: unknown) {
      alert(err instanceof Error ? err.message : 'Failed to restore player');
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold text-sandwich-100 tracking-tight flex items-center gap-2">
            <Users className="w-5 h-5 text-sandwich-200" />
            <span>Player Registry & Accounts</span>
          </h2>
          <p className="text-xs text-sandwich-400 mt-1">
            Search users, monitor anti-cheat risk scores, and manage access privileges.
          </p>
        </div>

        {/* Search Bar */}
        <form onSubmit={handleSearchSubmit} className="flex items-center gap-2 max-w-sm w-full">
          <div className="relative flex-1">
            <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-sandwich-500" />
            <input
              type="text"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search by handle or email..."
              className="w-full bg-sandwich-950 border border-sandwich-800 rounded-xl pl-9 pr-3 py-2 text-xs text-sandwich-100 placeholder-sandwich-500 focus:outline-none focus:border-sandwich-400 transition"
            />
          </div>
          <button
            type="submit"
            className="px-3.5 py-2 rounded-xl bg-sandwich-50 hover:bg-white text-sandwich-950 text-xs font-bold transition shadow-glow-white"
          >
            Search
          </button>
        </form>
      </div>

      {error && (
        <div className="p-4 rounded-xl bg-rose-950/20 border border-rose-900/50 text-rose-400 text-xs flex items-center gap-2">
          <AlertTriangle className="w-4 h-4" />
          <span>{error}</span>
        </div>
      )}

      {/* Players Table */}
      <div className="border border-sandwich-800 rounded-2xl bg-sandwich-900/90 overflow-hidden shadow-luxury backdrop-blur-xl">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-sandwich-200">
            <thead className="bg-sandwich-950/80 text-sandwich-400 uppercase text-[10px] tracking-wider border-b border-sandwich-800 font-mono">
              <tr>
                <th className="px-5 py-3.5 font-semibold">Player</th>
                <th className="px-5 py-3.5 font-semibold">Email</th>
                <th className="px-5 py-3.5 font-semibold">Roles</th>
                <th className="px-5 py-3.5 font-semibold">Progression</th>
                <th className="px-5 py-3.5 font-semibold">Risk Score</th>
                <th className="px-5 py-3.5 font-semibold">Status</th>
                <th className="px-5 py-3.5 font-semibold text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-sandwich-800/60">
              {loading ? (
                <tr>
                  <td colSpan={7} className="px-5 py-12 text-center text-sandwich-500 font-mono">
                    <RefreshCw className="w-5 h-5 animate-spin mx-auto mb-2 text-sandwich-200" />
                    <span>Loading player accounts...</span>
                  </td>
                </tr>
              ) : players.length === 0 ? (
                <tr>
                  <td colSpan={7} className="px-5 py-12 text-center text-sandwich-500 font-mono">
                    No players found matching current filter.
                  </td>
                </tr>
              ) : (
                players.map((player) => {
                  const isSuspended = !player.accountNonLocked;
                  const isHighRisk = player.riskScore >= 40;

                  return (
                    <tr key={player.id} className="hover:bg-sandwich-800/40 transition">
                      <td className="px-5 py-3.5 font-medium text-sandwich-50">
                        {player.username}
                      </td>
                      <td className="px-5 py-3.5 text-sandwich-400 font-mono">
                        {player.email}
                      </td>
                      <td className="px-5 py-3.5">
                        <div className="flex flex-wrap gap-1">
                          {player.roles.map((r, i) => (
                            <span
                              key={i}
                              className={`px-1.5 py-0.5 rounded text-[10px] font-mono font-semibold ${
                                r === 'ROLE_ADMIN'
                                  ? 'bg-rose-950/40 text-rose-300 border border-rose-900/60'
                                  : 'bg-sandwich-800 text-sandwich-300 border border-sandwich-700'
                              }`}
                            >
                              {r.replace('ROLE_', '')}
                            </span>
                          ))}
                        </div>
                      </td>
                      <td className="px-5 py-3.5 text-sandwich-300 font-mono">
                        <div className="flex items-center gap-2 text-[11px]">
                          <span>Lvl {player.level}</span>
                          <span className="text-sandwich-700">•</span>
                          <span>{player.xp} XP</span>
                          <span className="text-sandwich-700">•</span>
                          <span className="text-sandwich-100 font-bold">{player.mmr} MMR</span>
                        </div>
                      </td>
                      <td className="px-5 py-3.5 font-mono">
                        <span
                          className={`inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[11px] font-medium ${
                            isHighRisk
                              ? 'bg-rose-950/40 text-rose-400 border border-rose-900/60 font-bold'
                              : player.riskScore > 0
                              ? 'bg-sandwich-800 text-sandwich-300 border border-sandwich-700'
                              : 'bg-sandwich-950 text-emerald-400 border border-sandwich-800'
                          }`}
                        >
                          {isHighRisk && <ShieldAlert className="w-3 h-3 text-rose-400" />}
                          {player.riskScore}
                        </span>
                      </td>
                      <td className="px-5 py-3.5">
                        <span
                          className={`inline-flex items-center px-2 py-0.5 rounded-full text-[10px] font-mono font-semibold ${
                            isSuspended
                              ? 'bg-rose-950/40 text-rose-400 border border-rose-900/60'
                              : 'bg-sandwich-800 text-emerald-400 border border-sandwich-700'
                          }`}
                        >
                          {isSuspended ? 'Suspended' : 'Active'}
                        </span>
                      </td>
                      <td className="px-5 py-3.5 text-right">
                        {isSuspended ? (
                          <button
                            onClick={() => handleRestore(player)}
                            className="inline-flex items-center gap-1 px-2.5 py-1 rounded-lg bg-sandwich-800 hover:bg-sandwich-700 text-emerald-400 border border-sandwich-700 text-xs font-medium transition"
                          >
                            <UserCheck className="w-3.5 h-3.5" />
                            <span>Restore</span>
                          </button>
                        ) : (
                          <button
                            onClick={() => setSelectedPlayer(player)}
                            className="inline-flex items-center gap-1 px-2.5 py-1 rounded-lg bg-rose-950/30 hover:bg-rose-950/50 text-rose-400 border border-rose-900/60 text-xs font-medium transition"
                          >
                            <UserX className="w-3.5 h-3.5" />
                            <span>Suspend</span>
                          </button>
                        )}
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Suspend Player Dialog */}
      {selectedPlayer && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-sandwich-950/80 backdrop-blur-md">
          <div className="w-full max-w-md bg-sandwich-900 border border-sandwich-700 rounded-2xl p-6 shadow-luxury space-y-4">
            <div className="flex items-center gap-3 text-rose-400">
              <UserX className="w-6 h-6" />
              <div>
                <h3 className="text-base font-semibold text-sandwich-100">Suspend Player Account</h3>
                <p className="text-xs text-sandwich-400 font-mono">Player: @{selectedPlayer.username}</p>
              </div>
            </div>

            <p className="text-xs text-sandwich-400 leading-relaxed">
              Suspending this player will immediately lock their credentials and terminate all matchmaking and battle queues.
            </p>

            <div>
              <label className="block text-xs font-medium text-sandwich-300 mb-1.5 font-mono">
                Suspension Reason (recorded in audit logs)
              </label>
              <textarea
                value={suspendReason}
                onChange={(e) => setSuspendReason(e.target.value)}
                placeholder="e.g. Unrealistic solve timing exploit confirmed by moderation review..."
                rows={3}
                className="w-full bg-sandwich-950 border border-sandwich-700 rounded-xl p-3 text-xs text-sandwich-100 placeholder-sandwich-500 focus:outline-none focus:border-sandwich-400 transition resize-none"
              />
            </div>

            <div className="flex gap-3 pt-2">
              <button
                type="button"
                onClick={() => { setSelectedPlayer(null); setSuspendReason(''); }}
                className="flex-1 px-4 py-2 rounded-xl border border-sandwich-700 text-sandwich-300 hover:bg-sandwich-800 text-xs font-medium transition"
              >
                Cancel
              </button>
              <button
                type="button"
                disabled={actionLoading || !suspendReason.trim()}
                onClick={handleSuspend}
                className="flex-1 px-4 py-2 rounded-xl bg-rose-600 hover:bg-rose-500 text-white text-xs font-bold transition disabled:opacity-50 shadow-sm"
              >
                {actionLoading ? 'Suspending...' : 'Confirm Suspension'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
