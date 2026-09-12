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
    if (action.includes('SUSPEND')) return <UserX className="w-3.5 h-3.5 text-sandwich-300" />;
    if (action.includes('RESTORE')) return <UserCheck className="w-3.5 h-3.5 text-sandwich-200" />;
    if (action.includes('CHALLENGE')) return <Code2 className="w-3.5 h-3.5 text-sandwich-300" />;
    if (action.includes('REPORT')) return <Flag className="w-3.5 h-3.5 text-sandwich-300" />;
    return <History className="w-3.5 h-3.5 text-sandwich-400" />;
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold text-sandwich-50 tracking-tight flex items-center gap-2">
            <History className="w-5 h-5 text-sandwich-200" />
            <span>Immutable Admin Audit Trail</span>
          </h2>
          <p className="text-xs text-sandwich-400 mt-1">
            Permanent record of all governance decisions, suspensions, challenge publications, and report resolutions.
          </p>
        </div>

        <button
          onClick={fetchAuditLogs}
          className="flex items-center gap-2 px-3.5 py-1.5 rounded-xl border border-sandwich-700 bg-sandwich-900 hover:bg-sandwich-800 text-sandwich-200 text-xs font-medium transition self-start sm:self-auto shadow-sm"
        >
          <RefreshCw className="w-3.5 h-3.5" />
          <span>Refresh Logs</span>
        </button>
      </div>

      {error && (
        <div className="p-4 rounded-xl bg-sandwich-900 border border-sandwich-700 text-sandwich-200 text-xs flex items-center gap-2">
          <AlertTriangle className="w-4 h-4 text-sandwich-300" />
          <span>{error}</span>
        </div>
      )}

      {/* Logs Table */}
      <div className="border border-sandwich-800 rounded-2xl bg-sandwich-900/60 overflow-hidden shadow-luxury">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-sandwich-200">
            <thead className="bg-sandwich-950/80 text-sandwich-400 uppercase text-[10px] tracking-wider border-b border-sandwich-800">
              <tr>
                <th className="px-5 py-3.5 font-semibold">Timestamp</th>
                <th className="px-5 py-3.5 font-semibold">Administrator</th>
                <th className="px-5 py-3.5 font-semibold">Action</th>
                <th className="px-5 py-3.5 font-semibold">Target</th>
                <th className="px-5 py-3.5 font-semibold">Details</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-sandwich-800/60">
              {loading ? (
                <tr>
                  <td colSpan={5} className="px-5 py-12 text-center text-sandwich-500">
                    <RefreshCw className="w-5 h-5 animate-spin mx-auto mb-2 text-sandwich-300" />
                    <span>Loading audit records...</span>
                  </td>
                </tr>
              ) : logs.length === 0 ? (
                <tr>
                  <td colSpan={5} className="px-5 py-12 text-center text-sandwich-500">
                    No administrative audit records logged yet.
                  </td>
                </tr>
              ) : (
                logs.map((log) => (
                  <tr key={log.id} className="hover:bg-sandwich-800/40 transition">
                    <td className="px-5 py-3.5 text-sandwich-500 font-mono text-[11px] whitespace-nowrap">
                      {new Date(log.createdAt).toLocaleString()}
                    </td>
                    <td className="px-5 py-3.5 font-semibold text-sandwich-50">
                      @{log.actorUsername}
                    </td>
                    <td className="px-5 py-3.5">
                      <div className="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-md bg-sandwich-800 border border-sandwich-700 font-mono text-[11px] font-medium text-sandwich-200">
                        {getActionIcon(log.action)}
                        <span>{log.action}</span>
                      </div>
                    </td>
                    <td className="px-5 py-3.5">
                      <div className="font-mono text-xs text-sandwich-200">{log.targetId}</div>
                      <div className="text-[10px] text-sandwich-500 uppercase">{log.targetType}</div>
                    </td>
                    <td className="px-5 py-3.5 text-sandwich-400 max-w-md truncate">
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
