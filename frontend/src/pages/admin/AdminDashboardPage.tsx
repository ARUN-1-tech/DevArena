import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import {
  Users,
  Code2,
  Swords,
  CheckCircle,
  Flag,
  ShieldAlert,
  Bot,
  RefreshCw,
  ArrowRight,
} from 'lucide-react';
import { adminService } from '../../services/adminService';
import { AdminOverviewDto } from '../../types/admin';

export const AdminDashboardPage: React.FC = () => {
  const [overview, setOverview] = useState<AdminOverviewDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchOverview = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await adminService.getOverview();
      setOverview(data);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to load admin overview');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOverview();
  }, []);

  if (loading) {
    return (
      <div className="py-24 flex flex-col items-center justify-center text-slate-400 gap-3">
        <RefreshCw className="w-8 h-8 animate-spin text-indigo-400" />
        <span className="text-xs font-mono">Aggregating platform telemetry...</span>
      </div>
    );
  }

  if (error || !overview) {
    return (
      <div className="p-6 rounded-2xl bg-rose-500/10 border border-rose-500/20 text-rose-300 text-sm flex items-center justify-between">
        <span>{error || 'Failed to load data'}</span>
        <button
          onClick={fetchOverview}
          className="px-3 py-1.5 rounded-lg bg-rose-600 hover:bg-rose-500 text-white text-xs font-medium transition"
        >
          Retry
        </button>
      </div>
    );
  }

  const kpis = [
    {
      title: 'Total Players',
      value: overview.totalUsers,
      sub: `${overview.activeUsers} active`,
      icon: Users,
      color: 'from-blue-500/20 to-cyan-500/20 text-blue-400 border-blue-500/30',
      link: '/admin/players',
    },
    {
      title: 'Challenges',
      value: overview.totalChallenges,
      sub: 'Algorithmic katas',
      icon: Code2,
      color: 'from-emerald-500/20 to-teal-500/20 text-emerald-400 border-emerald-500/30',
      link: '/admin/challenges',
    },
    {
      title: 'Code Submissions',
      value: overview.totalSubmissions,
      sub: 'Tested & evaluated',
      icon: CheckCircle,
      color: 'from-indigo-500/20 to-violet-500/20 text-indigo-400 border-indigo-500/30',
    },
    {
      title: '1v1 Battles',
      value: overview.totalBattles,
      sub: 'Multiplayer duels',
      icon: Swords,
      color: 'from-amber-500/20 to-orange-500/20 text-amber-400 border-amber-500/30',
    },
    {
      title: 'Open Reports',
      value: overview.openReports,
      sub: overview.openReports > 0 ? 'Requires attention' : 'Queue clear',
      icon: Flag,
      color: overview.openReports > 0
        ? 'from-rose-500/30 to-red-500/30 text-rose-400 border-rose-500/40'
        : 'from-slate-800 to-slate-800 text-slate-400 border-slate-700',
      link: '/admin/reports',
    },
    {
      title: 'High-Risk Alerts',
      value: overview.highRiskIntegrityAlerts,
      sub: 'Anti-cheat triggers',
      icon: ShieldAlert,
      color: overview.highRiskIntegrityAlerts > 0
        ? 'from-orange-500/30 to-rose-500/30 text-orange-400 border-orange-500/40'
        : 'from-slate-800 to-slate-800 text-slate-400 border-slate-700',
      link: '/admin/integrity',
    },
    {
      title: 'AI Coach Invocations',
      value: overview.totalAiQueriesToday,
      sub: 'Queries today',
      icon: Bot,
      color: 'from-cyan-500/20 to-sky-500/20 text-cyan-400 border-cyan-500/30',
    },
  ];

  return (
    <div className="space-y-8">
      {/* Welcome Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold text-white tracking-tight">Platform Telemetry & Governance</h2>
          <p className="text-xs text-slate-400 mt-1">
            Real-time platform metrics, security integrity, moderation reports, and game health.
          </p>
        </div>
        <button
          onClick={fetchOverview}
          className="flex items-center gap-2 px-3 py-1.5 rounded-xl border border-slate-800 bg-slate-900 hover:bg-slate-800 text-slate-300 text-xs font-medium transition self-start sm:self-auto"
        >
          <RefreshCw className="w-3.5 h-3.5" />
          <span>Refresh Telemetry</span>
        </button>
      </div>

      {/* KPI Cards Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
        {kpis.map((kpi, idx) => {
          const Icon = kpi.icon;
          return (
            <div
              key={idx}
              className={`p-5 rounded-2xl bg-gradient-to-br bg-slate-900 border ${kpi.color} shadow-sm relative flex flex-col justify-between`}
            >
              <div className="flex items-center justify-between mb-3">
                <span className="text-xs font-medium text-slate-400">{kpi.title}</span>
                <Icon className="w-5 h-5" />
              </div>
              <div>
                <div className="text-2xl font-bold tracking-tight text-white">{kpi.value.toLocaleString()}</div>
                <div className="text-[11px] text-slate-400 mt-1">{kpi.sub}</div>
              </div>
              {kpi.link && (
                <Link
                  to={kpi.link}
                  className="mt-4 pt-3 border-t border-slate-800 flex items-center justify-between text-xs text-slate-300 hover:text-white transition font-medium"
                >
                  <span>Manage</span>
                  <ArrowRight className="w-3.5 h-3.5" />
                </Link>
              )}
            </div>
          );
        })}
      </div>

      {/* Quick Governance Panels */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 pt-4">
        <div className="p-6 rounded-2xl bg-slate-900/60 border border-slate-800 space-y-3">
          <div className="flex items-center gap-2 text-rose-400">
            <Flag className="w-5 h-5" />
            <h3 className="text-sm font-semibold text-white">Community Moderation</h3>
          </div>
          <p className="text-xs text-slate-400 leading-relaxed">
            Review user reports on harassing behavior, cheating exploits, offensive handles, or match irregularities. Take decisive actions with resolution logs.
          </p>
          <Link
            to="/admin/reports"
            className="inline-flex items-center gap-2 text-xs font-semibold text-indigo-400 hover:text-indigo-300 transition mt-2"
          >
            <span>Go to Moderation Queue ({overview.openReports} open)</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </Link>
        </div>

        <div className="p-6 rounded-2xl bg-slate-900/60 border border-slate-800 space-y-3">
          <div className="flex items-center gap-2 text-orange-400">
            <ShieldAlert className="w-5 h-5" />
            <h3 className="text-sm font-semibold text-white">Anti-Cheat & Integrity Engine</h3>
          </div>
          <p className="text-xs text-slate-400 leading-relaxed">
            Inspect automated heuristics detecting impossible solve timing, rapid code execution bursts, code plagiarism patterns, and abnormal battle anomalies.
          </p>
          <Link
            to="/admin/integrity"
            className="inline-flex items-center gap-2 text-xs font-semibold text-indigo-400 hover:text-indigo-300 transition mt-2"
          >
            <span>Review Integrity Events ({overview.highRiskIntegrityAlerts} critical)</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </Link>
        </div>
      </div>
    </div>
  );
};
