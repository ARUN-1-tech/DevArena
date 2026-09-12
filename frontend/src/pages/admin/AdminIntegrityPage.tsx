import React, { useEffect, useState } from 'react';
import {
  ShieldAlert,
  ShieldCheck,
  RefreshCw,
  AlertTriangle,
  Clock,
  Zap,
} from 'lucide-react';
import { adminService } from '../../services/adminService';
import { IntegrityEventDto } from '../../types/admin';

export const AdminIntegrityPage: React.FC = () => {
  const [events, setEvents] = useState<IntegrityEventDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchEvents = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await adminService.getIntegrityEvents();
      setEvents(data.content);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to load integrity events');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchEvents();
  }, []);

  const handleReview = async (eventId: string) => {
    try {
      await adminService.reviewIntegrityEvent(eventId);
      setEvents((prev) => prev.filter((e) => e.id !== eventId));
    } catch (err: unknown) {
      alert(err instanceof Error ? err.message : 'Failed to mark reviewed');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-black text-slate-900 tracking-tight flex items-center gap-2">
            <ShieldAlert className="w-5 h-5 text-amber-600" />
            <span>Anti-Cheat & Integrity Engine</span>
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Automated anomaly detection for impossible solve speeds, code plagiarism, and bot-like execution bursts.
          </p>
        </div>

        <button
          onClick={fetchEvents}
          className="flex items-center gap-2 px-3.5 py-1.5 rounded-xl border border-slate-200 bg-white hover:bg-slate-50 text-slate-700 text-xs font-bold shadow-2xs transition self-start sm:self-auto"
        >
          <RefreshCw className="w-3.5 h-3.5 text-indigo-600" />
          <span>Refresh Queue</span>
        </button>
      </div>

      {error && (
        <div className="p-4 rounded-xl bg-rose-50 border border-rose-200 text-rose-700 text-xs flex items-center gap-2">
          <AlertTriangle className="w-4 h-4" />
          <span>{error}</span>
        </div>
      )}

      {/* Events Table */}
      <div className="border border-slate-200/90 rounded-2xl bg-white overflow-hidden shadow-2xs">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-700">
            <thead className="bg-slate-50/90 text-slate-500 uppercase text-[10px] tracking-wider border-b border-slate-200/80 font-mono">
              <tr>
                <th className="px-5 py-3.5 font-bold">User</th>
                <th className="px-5 py-3.5 font-bold">Event Pattern</th>
                <th className="px-5 py-3.5 font-bold">Severity</th>
                <th className="px-5 py-3.5 font-bold">Risk Score</th>
                <th className="px-5 py-3.5 font-bold">Telemetry & Metadata</th>
                <th className="px-5 py-3.5 font-bold">Detected At</th>
                <th className="px-5 py-3.5 font-bold text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan={7} className="px-5 py-12 text-center text-slate-500">
                    <RefreshCw className="w-5 h-5 animate-spin mx-auto mb-2 text-amber-600" />
                    <span>Scanning integrity telemetry...</span>
                  </td>
                </tr>
              ) : events.length === 0 ? (
                <tr>
                  <td colSpan={7} className="px-5 py-12 text-center text-slate-500">
                    <div className="flex flex-col items-center justify-center gap-2">
                      <ShieldCheck className="w-8 h-8 text-emerald-600" />
                      <span className="text-slate-900 font-bold">All clear</span>
                      <span className="text-xs text-slate-500">No active unreviewed integrity events found.</span>
                    </div>
                  </td>
                </tr>
              ) : (
                events.map((ev) => {
                  const isCritical = ev.severity === 'CRITICAL' || ev.severity === 'HIGH';

                  return (
                    <tr key={ev.id} className="hover:bg-slate-50/80 transition">
                      <td className="px-5 py-3.5 font-bold text-slate-900">
                        @{ev.username}
                      </td>
                      <td className="px-5 py-3.5">
                        <div className="flex items-center gap-1.5 font-mono text-[11px] text-slate-700">
                          {ev.eventType === 'IMPOSSIBLE_TIMING' && <Clock className="w-3.5 h-3.5 text-rose-600" />}
                          {ev.eventType === 'RAPID_EXECUTION' && <Zap className="w-3.5 h-3.5 text-amber-600" />}
                          <span>{ev.eventType}</span>
                        </div>
                      </td>
                      <td className="px-5 py-3.5">
                        <span
                          className={`px-2.5 py-0.5 rounded-full text-[10px] font-bold ${
                            ev.severity === 'CRITICAL'
                              ? 'bg-rose-50 text-rose-700 border border-rose-200'
                              : ev.severity === 'HIGH'
                              ? 'bg-amber-50 text-amber-800 border border-amber-200'
                              : ev.severity === 'MEDIUM'
                              ? 'bg-yellow-50 text-yellow-800 border border-yellow-200'
                              : 'bg-slate-100 text-slate-700'
                          }`}
                        >
                          {ev.severity}
                        </span>
                      </td>
                      <td className="px-5 py-3.5">
                        <span
                          className={`font-mono font-bold ${
                            isCritical ? 'text-rose-600' : 'text-slate-700'
                          }`}
                        >
                          +{ev.riskScore}
                        </span>
                      </td>
                      <td className="px-5 py-3.5 text-slate-500 max-w-sm font-mono text-[11px] truncate">
                        {ev.metadata || 'No details'}
                      </td>
                      <td className="px-5 py-3.5 text-slate-400 text-[11px] font-mono">
                        {new Date(ev.createdAt).toLocaleString()}
                      </td>
                      <td className="px-5 py-3.5 text-right">
                        <button
                          onClick={() => handleReview(ev.id)}
                          className="inline-flex items-center gap-1 px-3 py-1 rounded-xl bg-indigo-50 hover:bg-indigo-100 text-indigo-700 border border-indigo-200 text-xs font-bold transition shadow-2xs"
                        >
                          <ShieldCheck className="w-3.5 h-3.5" />
                          <span>Mark Reviewed</span>
                        </button>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
