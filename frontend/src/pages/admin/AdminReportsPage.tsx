import React, { useEffect, useState } from 'react';
import {
  Flag,
  CheckCircle,
  XCircle,
  RefreshCw,
  AlertTriangle,
  MessageSquare,
} from 'lucide-react';
import { adminService } from '../../services/adminService';
import { ReportDto } from '../../types/admin';

export const AdminReportsPage: React.FC = () => {
  const [reports, setReports] = useState<ReportDto[]>([]);
  const [statusFilter, setStatusFilter] = useState<string>('OPEN');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Resolution modal state
  const [activeReport, setActiveReport] = useState<ReportDto | null>(null);
  const [resolutionAction, setResolutionAction] = useState<'RESOLVED' | 'DISMISSED'>('RESOLVED');
  const [resolutionNotes, setResolutionNotes] = useState('');
  const [actionLoading, setActionLoading] = useState(false);

  const fetchReports = async (status?: string) => {
    setLoading(true);
    setError(null);
    try {
      const data = await adminService.getReports(status || undefined);
      setReports(data.content);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to fetch reports');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReports(statusFilter);
  }, [statusFilter]);

  const handleResolveSubmit = async () => {
    if (!activeReport) return;
    setActionLoading(true);
    try {
      const updated = await adminService.resolveReport(activeReport.id, resolutionAction, resolutionNotes);
      setReports((prev) => prev.map((r) => (r.id === updated.id ? updated : r)));
      setActiveReport(null);
      setResolutionNotes('');
    } catch (err: unknown) {
      alert(err instanceof Error ? err.message : 'Failed to update report');
    } finally {
      setActionLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold text-sandwich-100 tracking-tight flex items-center gap-2">
            <Flag className="w-5 h-5 text-sandwich-200" />
            <span>Community Moderation Queue</span>
          </h2>
          <p className="text-xs text-sandwich-400 mt-1">
            Review reported players, suspicious activity, harassment, and resolve disciplinary items.
          </p>
        </div>

        {/* Status Filter Tabs */}
        <div className="flex items-center gap-1.5 p-1 bg-sandwich-900 border border-sandwich-800 rounded-xl self-start sm:self-auto font-mono">
          {['OPEN', 'RESOLVED', 'DISMISSED', ''].map((st) => (
            <button
              key={st}
              onClick={() => setStatusFilter(st)}
              className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
                statusFilter === st
                  ? 'bg-sandwich-50 text-sandwich-950 shadow-glow-white'
                  : 'text-sandwich-400 hover:text-sandwich-200'
              }`}
            >
              {st || 'All'}
            </button>
          ))}
        </div>
      </div>

      {error && (
        <div className="p-4 rounded-xl bg-rose-950/20 border border-rose-900/50 text-rose-400 text-xs flex items-center gap-2">
          <AlertTriangle className="w-4 h-4" />
          <span>{error}</span>
        </div>
      )}

      {/* Reports Table */}
      <div className="border border-sandwich-800 rounded-2xl bg-sandwich-900/90 overflow-hidden shadow-luxury backdrop-blur-xl">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-sandwich-200">
            <thead className="bg-sandwich-950/80 text-sandwich-400 uppercase text-[10px] tracking-wider border-b border-sandwich-800 font-mono">
              <tr>
                <th className="px-5 py-3.5 font-semibold">Target</th>
                <th className="px-5 py-3.5 font-semibold">Reason</th>
                <th className="px-5 py-3.5 font-semibold">Description</th>
                <th className="px-5 py-3.5 font-semibold">Reporter</th>
                <th className="px-5 py-3.5 font-semibold">Status</th>
                <th className="px-5 py-3.5 font-semibold">Date</th>
                <th className="px-5 py-3.5 font-semibold text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-sandwich-800/60">
              {loading ? (
                <tr>
                  <td colSpan={7} className="px-5 py-12 text-center text-sandwich-500 font-mono">
                    <RefreshCw className="w-5 h-5 animate-spin mx-auto mb-2 text-sandwich-200" />
                    <span>Loading reports queue...</span>
                  </td>
                </tr>
              ) : reports.length === 0 ? (
                <tr>
                  <td colSpan={7} className="px-5 py-12 text-center text-sandwich-500 font-mono">
                    No reports in this queue.
                  </td>
                </tr>
              ) : (
                reports.map((r) => (
                  <tr key={r.id} className="hover:bg-sandwich-800/40 transition">
                    <td className="px-5 py-3.5">
                      <div className="font-semibold text-sandwich-50">{r.targetId}</div>
                      <div className="text-[10px] text-sandwich-500 font-mono">{r.targetType}</div>
                    </td>
                    <td className="px-5 py-3.5 font-medium text-sandwich-200 font-mono">
                      {r.reason}
                    </td>
                    <td className="px-5 py-3.5 text-sandwich-300 max-w-xs truncate">
                      {r.description || <span className="text-sandwich-500 italic">No notes</span>}
                    </td>
                    <td className="px-5 py-3.5 text-sandwich-400 font-mono">
                      @{r.reporterUsername}
                    </td>
                    <td className="px-5 py-3.5">
                      <span
                        className={`px-2 py-0.5 rounded-full text-[10px] font-mono font-semibold ${
                          r.status === 'OPEN'
                            ? 'bg-rose-950/40 text-rose-400 border border-rose-900/60'
                            : r.status === 'RESOLVED'
                            ? 'bg-sandwich-800 text-emerald-400 border border-sandwich-700'
                            : 'bg-sandwich-950 text-sandwich-500 border border-sandwich-800'
                        }`}
                      >
                        {r.status}
                      </span>
                    </td>
                    <td className="px-5 py-3.5 text-sandwich-500 text-[11px] font-mono">
                      {new Date(r.createdAt).toLocaleDateString()}
                    </td>
                    <td className="px-5 py-3.5 text-right">
                      {r.status === 'OPEN' ? (
                        <button
                          onClick={() => {
                            setActiveReport(r);
                            setResolutionAction('RESOLVED');
                            setResolutionNotes('');
                          }}
                          className="inline-flex items-center gap-1 px-3 py-1 rounded-lg bg-sandwich-800 hover:bg-sandwich-700 text-sandwich-200 border border-sandwich-700 text-xs font-medium transition shadow-sm"
                        >
                          <MessageSquare className="w-3.5 h-3.5" />
                          <span>Review</span>
                        </button>
                      ) : (
                        <span className="text-[11px] text-sandwich-500 italic font-mono">
                          Resolved by {r.resolvedByUsername || 'Admin'}
                        </span>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Resolution Dialog */}
      {activeReport && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-sandwich-950/80 backdrop-blur-md">
          <div className="w-full max-w-md bg-sandwich-900 border border-sandwich-700 rounded-2xl p-6 shadow-luxury space-y-4 text-sandwich-100">
            <h3 className="text-base font-semibold text-sandwich-100 flex items-center gap-2">
              <Flag className="w-4 h-4 text-rose-400" />
              <span>Resolve Report: {activeReport.targetId}</span>
            </h3>

            <div className="p-3 bg-sandwich-950 rounded-xl border border-sandwich-800 text-xs text-sandwich-300 space-y-1 font-mono">
              <div><strong className="text-sandwich-200">Reason:</strong> {activeReport.reason}</div>
              <div><strong className="text-sandwich-200">Description:</strong> {activeReport.description || 'N/A'}</div>
              <div><strong className="text-sandwich-200">Reported by:</strong> @{activeReport.reporterUsername}</div>
            </div>

            <div className="space-y-1.5">
              <label className="block text-xs font-medium text-sandwich-300 font-mono">Resolution Decision</label>
              <div className="grid grid-cols-2 gap-2">
                <button
                  type="button"
                  onClick={() => setResolutionAction('RESOLVED')}
                  className={`px-3 py-2 rounded-xl text-xs font-bold flex items-center justify-center gap-2 border transition ${
                    resolutionAction === 'RESOLVED'
                      ? 'bg-sandwich-50 text-sandwich-950 border-white shadow-glow-white'
                      : 'bg-sandwich-950 text-sandwich-400 border-sandwich-800 hover:bg-sandwich-800'
                  }`}
                >
                  <CheckCircle className="w-3.5 h-3.5" />
                  <span>Resolve / Action</span>
                </button>
                <button
                  type="button"
                  onClick={() => setResolutionAction('DISMISSED')}
                  className={`px-3 py-2 rounded-xl text-xs font-bold flex items-center justify-center gap-2 border transition ${
                    resolutionAction === 'DISMISSED'
                      ? 'bg-sandwich-700 text-white border-sandwich-600'
                      : 'bg-sandwich-950 text-sandwich-400 border-sandwich-800 hover:bg-sandwich-800'
                  }`}
                >
                  <XCircle className="w-3.5 h-3.5" />
                  <span>Dismiss Report</span>
                </button>
              </div>
            </div>

            <div>
              <label className="block text-xs font-medium text-sandwich-300 mb-1 font-mono">
                Resolution Notes (recorded in audit logs)
              </label>
              <textarea
                value={resolutionNotes}
                onChange={(e) => setResolutionNotes(e.target.value)}
                placeholder="e.g. Warning issued, solution reset, or dismissed as false positive..."
                rows={3}
                className="w-full bg-sandwich-950 border border-sandwich-700 rounded-xl p-3 text-xs text-sandwich-100 placeholder-sandwich-500 focus:outline-none focus:border-sandwich-400 transition resize-none"
              />
            </div>

            <div className="flex gap-3 pt-2">
              <button
                type="button"
                onClick={() => setActiveReport(null)}
                className="flex-1 px-4 py-2 rounded-xl border border-sandwich-700 text-sandwich-300 hover:bg-sandwich-800 text-xs font-medium transition"
              >
                Cancel
              </button>
              <button
                type="button"
                disabled={actionLoading}
                onClick={handleResolveSubmit}
                className="flex-1 px-4 py-2 rounded-xl bg-sandwich-50 hover:bg-white text-sandwich-950 text-xs font-bold transition disabled:opacity-50 shadow-glow-white"
              >
                {actionLoading ? 'Saving...' : 'Confirm Resolution'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
