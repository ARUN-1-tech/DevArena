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
          <h2 className="text-2xl font-black text-slate-900 tracking-tight flex items-center gap-2">
            <Users className="w-5 h-5 text-indigo-600" />
            <span>Player Registry & Accounts</span>
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Search users, monitor anti-cheat risk scores, and manage access privileges.
          </p>
        </div>

        {/* Search Bar */}
        <form onSubmit={handleSearchSubmit} className="flex items-center gap-2 max-w-sm w-full">
          <div className="relative flex-1">
            <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
            <input
              type="text"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search by handle or email..."
              className="w-full bg-white border border-slate-200 rounded-xl pl-9 pr-3 py-2 text-xs text-slate-800 placeholder-slate-400 focus:outline-none focus:border-indigo-600 shadow-2xs transition"
            />
          </div>
          <button
            type="submit"
            className="px-4 py-2 rounded-xl bg-gradient-to-r from-indigo-600 to-cyan-600 text-white text-xs font-bold shadow-sm shadow-indigo-500/25 transition"
          >
            Search
          </button>
        </form>
      </div>

      {error && (
        <div className="p-4 rounded-xl bg-rose-50 border border-rose-200 text-rose-700 text-xs flex items-center gap-2">
          <AlertTriangle className="w-4 h-4" />
          <span>{error}</span>
        </div>
      )}

      {/* Players Table */}
      <div className="border border-slate-200/90 rounded-2xl bg-white overflow-hidden shadow-2xs">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-700">
            <thead className="bg-slate-50/90 text-slate-500 uppercase text-[10px] tracking-wider border-b border-slate-200/80 font-mono">
              <tr>
                <th className="px-5 py-3.5 font-bold">Player</th>
                <th className="px-5 py-3.5 font-bold">Email</th>
                <th className="px-5 py-3.5 font-bold">Roles</th>
                <th className="px-5 py-3.5 font-bold">Progression</th>
                <th className="px-5 py-3.5 font-bold">Risk Score</th>
                <th className="px-5 py-3.5 font-bold">Status</th>
                <th className="px-5 py-3.5 font-bold text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan={7} className="px-5 py-12 text-center text-slate-500">
                    <RefreshCw className="w-5 h-5 animate-spin mx-auto mb-2 text-indigo-600" />
                    <span>Loading player accounts...</span>
                  </td>
                </tr>
              ) : players.length === 0 ? (
                <tr>
                  <td colSpan={7} className="px-5 py-12 text-center text-slate-400">
                    No players found matching current filter.
                  </td>
                </tr>
              ) : (
                players.map((player) => {
                  const isSuspended = !player.accountNonLocked;
                  const isHighRisk = player.riskScore >= 40;

                  return (
                    <tr key={player.id} className="hover:bg-slate-50/80 transition">
                      <td className="px-5 py-3.5 font-bold text-slate-900">
                        {player.username}
                      </td>
                      <td className="px-5 py-3.5 text-slate-500">
                        {player.email}
                      </td>
                      <td className="px-5 py-3.5">
                        <div className="flex flex-wrap gap-1">
                          {player.roles.map((r, i) => (
                            <span
                              key={i}
                              className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${
                                r === 'ROLE_ADMIN'
                                  ? 'bg-rose-50 text-rose-700 border border-rose-200'
                                  : 'bg-slate-100 text-slate-700'
                              }`}
                            >
                              {r.replace('ROLE_', '')}
                            </span>
                          ))}
                        </div>
                      </td>
                      <td className="px-5 py-3.5 text-slate-700 font-mono">
                        <div className="flex items-center gap-2 text-[11px]">
                          <span className="font-bold">Lvl {player.level}</span>
                          <span className="text-slate-300">•</span>
                          <span>{player.xp} XP</span>
                          <span className="text-slate-300">•</span>
                          <span className="text-amber-600 font-bold">{player.mmr} MMR</span>
                        </div>
                      </td>
                      <td className="px-5 py-3.5">
                        <span
                          className={`inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[11px] font-bold font-mono ${
                            isHighRisk
                              ? 'bg-rose-50 text-rose-700 border border-rose-200'
                              : player.riskScore > 0
                              ? 'bg-amber-50 text-amber-800 border border-amber-200'
                              : 'bg-emerald-50 text-emerald-800 border border-emerald-200'
                          }`}
                        >
                          {isHighRisk && <ShieldAlert className="w-3 h-3 text-rose-600" />}
                          {player.riskScore}
                        </span>
                      </td>
                      <td className="px-5 py-3.5">
                        <span
                          className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-[10px] font-bold ${
                            isSuspended
                              ? 'bg-rose-50 text-rose-700 border border-rose-200'
                              : 'bg-emerald-50 text-emerald-800 border border-emerald-200'
                          }`}
                        >
                          {isSuspended ? 'Suspended' : 'Active'}
                        </span>
                      </td>
                      <td className="px-5 py-3.5 text-right">
                        {isSuspended ? (
                          <button
                            onClick={() => handleRestore(player)}
                            className="inline-flex items-center gap-1 px-3 py-1 rounded-xl bg-emerald-50 hover:bg-emerald-100 text-emerald-700 border border-emerald-200 text-xs font-bold transition shadow-2xs"
                          >
                            <UserCheck className="w-3.5 h-3.5" />
                            <span>Restore</span>
                          </button>
                        ) : (
                          <button
                            onClick={() => setSelectedPlayer(player)}
                            className="inline-flex items-center gap-1 px-3 py-1 rounded-xl bg-rose-50 hover:bg-rose-100 text-rose-700 border border-rose-200 text-xs font-bold transition shadow-2xs"
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
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-sm">
          <div className="w-full max-w-md bg-white border border-slate-200 rounded-3xl p-6 shadow-premium-hover space-y-4">
            <div className="flex items-center gap-3 text-rose-600">
              <UserX className="w-6 h-6" />
              <div>
                <h3 className="text-base font-bold text-slate-900">Suspend Player Account</h3>
                <p className="text-xs text-slate-500">Player: @{selectedPlayer.username}</p>
              </div>
            </div>

            <p className="text-xs text-slate-600 leading-relaxed">
              Suspending this player will immediately lock their credentials and terminate all matchmaking and battle queues.
            </p>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1.5">
                Suspension Reason (recorded in audit logs)
              </label>
              <textarea
                value={suspendReason}
                onChange={(e) => setSuspendReason(e.target.value)}
                placeholder="e.g. Unrealistic solve timing exploit confirmed by moderation review..."
                rows={3}
                className="w-full bg-slate-50 border border-slate-200 rounded-xl p-3 text-xs text-slate-800 placeholder-slate-400 focus:outline-none focus:border-rose-500 transition resize-none"
              />
            </div>

            <div className="flex gap-3 pt-2">
              <button
                type="button"
                onClick={() => { setSelectedPlayer(null); setSuspendReason(''); }}
                className="flex-1 px-4 py-2 rounded-xl border border-slate-200 text-slate-700 hover:bg-slate-50 text-xs font-bold transition shadow-2xs"
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
