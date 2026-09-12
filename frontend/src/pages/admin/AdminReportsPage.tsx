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
          <h2 className="text-2xl font-black text-slate-900 tracking-tight flex items-center gap-2">
            <Flag className="w-5 h-5 text-rose-600" />
            <span>Community Moderation Queue</span>
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Review reported players, suspicious activity, harassment, and resolve disciplinary items.
          </p>
        </div>

        {/* Status Filter Tabs */}
        <div className="flex items-center gap-1.5 p-1 bg-white border border-slate-200/80 rounded-xl shadow-2xs self-start sm:self-auto">
          {['OPEN', 'RESOLVED', 'DISMISSED', ''].map((st) => (
            <button
              key={st}
              onClick={() => setStatusFilter(st)}
              className={`px-3 py-1.5 rounded-lg text-xs font-bold transition ${
                statusFilter === st
                  ? 'bg-rose-600 text-white shadow-2xs'
                  : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
              }`}
            >
              {st || 'All'}
            </button>
          ))}
        </div>
      </div>

      {error && (
        <div className="p-4 rounded-xl bg-rose-50 border border-rose-200 text-rose-700 text-xs flex items-center gap-2">
          <AlertTriangle className="w-4 h-4" />
          <span>{error}</span>
        </div>
      )}

      {/* Reports Table */}
      <div className="border border-slate-200/90 rounded-2xl bg-white overflow-hidden shadow-2xs">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-700">
            <thead className="bg-slate-50/90 text-slate-500 uppercase text-[10px] tracking-wider border-b border-slate-200/80 font-mono">
              <tr>
                <th className="px-5 py-3.5 font-bold">Target</th>
                <th className="px-5 py-3.5 font-bold">Reason</th>
                <th className="px-5 py-3.5 font-bold">Description</th>
                <th className="px-5 py-3.5 font-bold">Reporter</th>
                <th className="px-5 py-3.5 font-bold">Status</th>
                <th className="px-5 py-3.5 font-bold">Date</th>
                <th className="px-5 py-3.5 font-bold text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan={7} className="px-5 py-12 text-center text-slate-500">
                    <RefreshCw className="w-5 h-5 animate-spin mx-auto mb-2 text-rose-600" />
                    <span>Loading reports queue...</span>
                  </td>
                </tr>
              ) : reports.length === 0 ? (
                <tr>
                  <td colSpan={7} className="px-5 py-12 text-center text-slate-400">
                    No reports in this queue.
                  </td>
                </tr>
              ) : (
                reports.map((r) => (
                  <tr key={r.id} className="hover:bg-slate-50/80 transition">
                    <td className="px-5 py-3.5">
                      <div className="font-bold text-slate-900 font-mono">{r.targetId}</div>
                      <div className="text-[10px] text-slate-400">{r.targetType}</div>
                    </td>
                    <td className="px-5 py-3.5 font-bold text-rose-600">
                      {r.reason}
                    </td>
                    <td className="px-5 py-3.5 text-slate-600 max-w-xs truncate">
                      {r.description || <span className="text-slate-400 italic">No notes</span>}
                    </td>
                    <td className="px-5 py-3.5 text-slate-500 font-semibold">
                      @{r.reporterUsername}
                    </td>
                    <td className="px-5 py-3.5">
                      <span
                        className={`px-2.5 py-0.5 rounded-full text-[10px] font-bold ${
                          r.status === 'OPEN'
                            ? 'bg-rose-50 text-rose-700 border border-rose-200'
                            : r.status === 'RESOLVED'
                            ? 'bg-emerald-50 text-emerald-800 border border-emerald-200'
                            : 'bg-slate-100 text-slate-600 border border-slate-200'
                        }`}
                      >
                        {r.status}
                      </span>
                    </td>
                    <td className="px-5 py-3.5 text-slate-400 text-[11px] font-mono">
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
                          className="inline-flex items-center gap-1 px-3 py-1 rounded-xl bg-rose-50 hover:bg-rose-100 text-rose-700 border border-rose-200 text-xs font-bold transition shadow-2xs"
                        >
                          <MessageSquare className="w-3.5 h-3.5" />
                          <span>Review</span>
                        </button>
                      ) : (
                        <span className="text-[11px] text-slate-400 italic">
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
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-sm">
          <div className="w-full max-w-md bg-white border border-slate-200 rounded-3xl p-6 shadow-premium-hover space-y-4">
            <h3 className="text-base font-bold text-slate-900 flex items-center gap-2">
              <Flag className="w-4 h-4 text-rose-600" />
              <span>Resolve Report: {activeReport.targetId}</span>
            </h3>

            <div className="p-3.5 bg-slate-50 rounded-2xl border border-slate-200/80 text-xs text-slate-700 space-y-1">
              <div><strong>Reason:</strong> {activeReport.reason}</div>
              <div><strong>Description:</strong> {activeReport.description || 'N/A'}</div>
              <div><strong>Reported by:</strong> @{activeReport.reporterUsername}</div>
            </div>

            <div className="space-y-1.5">
              <label className="block text-xs font-semibold text-slate-700">Resolution Decision</label>
              <div className="grid grid-cols-2 gap-2">
                <button
                  type="button"
                  onClick={() => setResolutionAction('RESOLVED')}
                  className={`px-3 py-2 rounded-xl text-xs font-bold flex items-center justify-center gap-2 border transition shadow-2xs ${
                    resolutionAction === 'RESOLVED'
                      ? 'bg-emerald-600 text-white border-emerald-500'
                      : 'bg-white text-slate-600 border-slate-200 hover:bg-slate-50'
                  }`}
                >
                  <CheckCircle className="w-3.5 h-3.5" />
                  <span>Resolve / Action</span>
                </button>
                <button
                  type="button"
                  onClick={() => setResolutionAction('DISMISSED')}
                  className={`px-3 py-2 rounded-xl text-xs font-bold flex items-center justify-center gap-2 border transition shadow-2xs ${
                    resolutionAction === 'DISMISSED'
                      ? 'bg-slate-800 text-white border-slate-700'
                      : 'bg-white text-slate-600 border-slate-200 hover:bg-slate-50'
                  }`}
                >
                  <XCircle className="w-3.5 h-3.5" />
                  <span>Dismiss Report</span>
                </button>
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">
                Resolution Notes (recorded in audit logs)
              </label>
              <textarea
                value={resolutionNotes}
                onChange={(e) => setResolutionNotes(e.target.value)}
                placeholder="e.g. Warning issued, solution reset, or dismissed as false positive..."
                rows={3}
                className="w-full bg-slate-50 border border-slate-200 rounded-xl p-3 text-xs text-slate-800 placeholder-slate-400 focus:outline-none focus:border-rose-500 transition resize-none"
              />
            </div>

            <div className="flex gap-3 pt-2">
              <button
                type="button"
                onClick={() => setActiveReport(null)}
                className="flex-1 px-4 py-2 rounded-xl border border-slate-200 text-slate-700 hover:bg-slate-50 text-xs font-bold transition shadow-2xs"
              >
                Cancel
              </button>
              <button
                type="button"
                disabled={actionLoading}
                onClick={handleResolveSubmit}
                className="flex-1 px-4 py-2 rounded-xl bg-gradient-to-r from-indigo-600 to-cyan-600 hover:from-indigo-700 hover:to-cyan-700 text-white text-xs font-bold transition disabled:opacity-50 shadow-sm"
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
