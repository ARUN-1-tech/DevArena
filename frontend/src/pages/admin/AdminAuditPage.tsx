import React, { useEffect, useState } from 'react';
import {
  History,
  RefreshCw,
  AlertTriangle,
  UserCheck,
  UserX,
  Code2,
  Flag,
} from 'lucide-react';
import { adminService } from '../../services/adminService';
import { AdminAuditDto } from '../../types/admin';

export const AdminAuditPage: React.FC = () => {
  const [logs, setLogs] = useState<AdminAuditDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchAuditLogs = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await adminService.getAuditLogs();
      setLogs(data.content);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to load audit logs');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAuditLogs();
  }, []);

  const getActionIcon = (action: string) => {
    if (action.includes('SUSPEND')) return <UserX className="w-3.5 h-3.5 text-rose-400" />;
    if (action.includes('RESTORE')) return <UserCheck className="w-3.5 h-3.5 text-emerald-400" />;
    if (action.includes('CHALLENGE')) return <Code2 className="w-3.5 h-3.5 text-cyan-400" />;
    if (action.includes('REPORT')) return <Flag className="w-3.5 h-3.5 text-amber-400" />;
    return <History className="w-3.5 h-3.5 text-slate-400" />;
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-black text-slate-900 tracking-tight flex items-center gap-2">
            <History className="w-5 h-5 text-indigo-600" />
            <span>Immutable Admin Audit Trail</span>
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Permanent record of all governance decisions, suspensions, challenge publications, and report resolutions.
          </p>
        </div>

        <button
          onClick={fetchAuditLogs}
          className="flex items-center gap-2 px-3.5 py-1.5 rounded-xl border border-slate-200 bg-white hover:bg-slate-50 text-slate-700 text-xs font-bold shadow-2xs transition self-start sm:self-auto"
        >
          <RefreshCw className="w-3.5 h-3.5 text-indigo-600" />
          <span>Refresh Logs</span>
        </button>
      </div>

      {error && (
        <div className="p-4 rounded-xl bg-rose-50 border border-rose-200 text-rose-700 text-xs flex items-center gap-2">
          <AlertTriangle className="w-4 h-4" />
          <span>{error}</span>
        </div>
      )}

      {/* Logs Table */}
      <div className="border border-slate-200/90 rounded-2xl bg-white overflow-hidden shadow-2xs">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-700">
            <thead className="bg-slate-50/90 text-slate-500 uppercase text-[10px] tracking-wider border-b border-slate-200/80 font-mono">
              <tr>
                <th className="px-5 py-3.5 font-bold">Timestamp</th>
                <th className="px-5 py-3.5 font-bold">Administrator</th>
                <th className="px-5 py-3.5 font-bold">Action</th>
                <th className="px-5 py-3.5 font-bold">Target</th>
                <th className="px-5 py-3.5 font-bold">Details</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan={5} className="px-5 py-12 text-center text-slate-500">
                    <RefreshCw className="w-5 h-5 animate-spin mx-auto mb-2 text-indigo-600" />
                    <span>Loading audit records...</span>
                  </td>
                </tr>
              ) : logs.length === 0 ? (
                <tr>
                  <td colSpan={5} className="px-5 py-12 text-center text-slate-400">
                    No administrative audit records logged yet.
                  </td>
                </tr>
              ) : (
                logs.map((log) => (
                  <tr key={log.id} className="hover:bg-slate-50/80 transition">
                    <td className="px-5 py-3.5 text-slate-400 font-mono text-[11px] whitespace-nowrap">
                      {new Date(log.createdAt).toLocaleString()}
                    </td>
                    <td className="px-5 py-3.5 font-bold text-slate-900">
                      @{log.actorUsername}
                    </td>
                    <td className="px-5 py-3.5">
                      <div className="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full bg-slate-100 border border-slate-200 font-mono text-[11px] font-bold text-slate-800">
                        {getActionIcon(log.action)}
                        <span>{log.action}</span>
                      </div>
                    </td>
                    <td className="px-5 py-3.5">
                      <div className="font-mono text-xs text-slate-800 font-bold">{log.targetId}</div>
                      <div className="text-[10px] text-slate-400 uppercase font-mono">{log.targetType}</div>
                    </td>
                    <td className="px-5 py-3.5 text-slate-600 max-w-md truncate">
                      {log.metadata || '—'}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
