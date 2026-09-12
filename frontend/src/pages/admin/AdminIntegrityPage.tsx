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
          <h2 className="text-xl font-bold text-sandwich-50 tracking-tight flex items-center gap-2">
            <ShieldAlert className="w-5 h-5 text-sandwich-200" />
            <span>Anti-Cheat & Integrity Engine</span>
          </h2>
          <p className="text-xs text-sandwich-400 mt-1">
            Automated anomaly detection for impossible solve speeds, code plagiarism, and bot-like execution bursts.
          </p>
        </div>

        <button
          onClick={fetchEvents}
          className="flex items-center gap-2 px-3.5 py-1.5 rounded-xl border border-sandwich-700 bg-sandwich-900 hover:bg-sandwich-800 text-sandwich-200 text-xs font-medium transition self-start sm:self-auto shadow-sm"
        >
          <RefreshCw className="w-3.5 h-3.5" />
          <span>Refresh Queue</span>
        </button>
      </div>

      {error && (
        <div className="p-4 rounded-xl bg-sandwich-900 border border-sandwich-700 text-sandwich-200 text-xs flex items-center gap-2">
          <AlertTriangle className="w-4 h-4 text-sandwich-300" />
          <span>{error}</span>
        </div>
      )}

      {/* Events Table */}
      <div className="border border-sandwich-800 rounded-2xl bg-sandwich-900/60 overflow-hidden shadow-luxury">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-sandwich-200">
            <thead className="bg-sandwich-950/80 text-sandwich-400 uppercase text-[10px] tracking-wider border-b border-sandwich-800">
              <tr>
                <th className="px-5 py-3.5 font-semibold">User</th>
                <th className="px-5 py-3.5 font-semibold">Event Pattern</th>
                <th className="px-5 py-3.5 font-semibold">Severity</th>
                <th className="px-5 py-3.5 font-semibold">Risk Score</th>
                <th className="px-5 py-3.5 font-semibold">Telemetry & Metadata</th>
                <th className="px-5 py-3.5 font-semibold">Detected At</th>
                <th className="px-5 py-3.5 font-semibold text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-sandwich-800/60">
              {loading ? (
                <tr>
                  <td colSpan={7} className="px-5 py-12 text-center text-sandwich-500">
                    <RefreshCw className="w-5 h-5 animate-spin mx-auto mb-2 text-sandwich-300" />
                    <span>Scanning integrity telemetry...</span>
                  </td>
                </tr>
              ) : events.length === 0 ? (
                <tr>
                  <td colSpan={7} className="px-5 py-12 text-center text-sandwich-400">
                    <div className="flex flex-col items-center justify-center gap-2">
                      <ShieldCheck className="w-8 h-8 text-sandwich-200" />
                      <span className="text-sandwich-100 font-medium">All clear</span>
                      <span className="text-xs text-sandwich-500">No active unreviewed integrity events found.</span>
                    </div>
                  </td>
                </tr>
              ) : (
                events.map((ev) => {
                  const isCritical = ev.severity === 'CRITICAL' || ev.severity === 'HIGH';

                  return (
                    <tr key={ev.id} className="hover:bg-sandwich-800/40 transition">
                      <td className="px-5 py-3.5 font-semibold text-sandwich-50">
                        @{ev.username}
                      </td>
                      <td className="px-5 py-3.5">
                        <div className="flex items-center gap-1.5 font-mono text-[11px] text-sandwich-200">
                          {ev.eventType === 'IMPOSSIBLE_TIMING' && <Clock className="w-3.5 h-3.5 text-sandwich-300" />}
                          {ev.eventType === 'RAPID_EXECUTION' && <Zap className="w-3.5 h-3.5 text-sandwich-300" />}
                          <span>{ev.eventType}</span>
                        </div>
                      </td>
                      <td className="px-5 py-3.5">
                        <span
                          className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${
                            ev.severity === 'CRITICAL'
                              ? 'bg-sandwich-800 text-sandwich-100 border border-sandwich-600 shadow-sm'
                              : ev.severity === 'HIGH'
                              ? 'bg-sandwich-800/80 text-sandwich-200 border border-sandwich-700'
                              : ev.severity === 'MEDIUM'
                              ? 'bg-sandwich-900 text-sandwich-300 border border-sandwich-800'
                              : 'bg-sandwich-900 text-sandwich-400 border border-sandwich-800'
                          }`}
                        >
                          {ev.severity}
                        </span>
                      </td>
                      <td className="px-5 py-3.5">
                        <span
                          className={`font-mono font-semibold ${
                            isCritical ? 'text-sandwich-100' : 'text-sandwich-300'
                          }`}
                        >
                          +{ev.riskScore}
                        </span>
                      </td>
                      <td className="px-5 py-3.5 text-sandwich-400 max-w-sm font-mono text-[11px] truncate">
                        {ev.metadata || 'No details'}
                      </td>
                      <td className="px-5 py-3.5 text-sandwich-500 text-[11px]">
                        {new Date(ev.createdAt).toLocaleString()}
                      </td>
                      <td className="px-5 py-3.5 text-right">
                        <button
                          onClick={() => handleReview(ev.id)}
                          className="inline-flex items-center gap-1 px-3 py-1 rounded-lg bg-sandwich-800 hover:bg-sandwich-700 text-sandwich-100 border border-sandwich-600 text-xs font-medium transition shadow-sm"
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
