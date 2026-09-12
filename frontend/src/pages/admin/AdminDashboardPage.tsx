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
      <div className="py-24 flex flex-col items-center justify-center text-sandwich-400 gap-3">
        <RefreshCw className="w-8 h-8 animate-spin text-sandwich-200" />
        <span className="text-xs font-mono">Aggregating platform telemetry...</span>
      </div>
    );
  }

  if (error || !overview) {
    return (
      <div className="p-6 rounded-2xl bg-rose-950/20 border border-rose-900/50 text-rose-300 text-sm flex items-center justify-between">
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
      color: 'border-sandwich-700 bg-sandwich-900/90 text-sandwich-100',
      link: '/admin/players',
    },
    {
      title: 'Challenges',
      value: overview.totalChallenges,
      sub: 'Algorithmic katas',
      icon: Code2,
      color: 'border-sandwich-700 bg-sandwich-900/90 text-sandwich-100',
      link: '/admin/challenges',
    },
    {
      title: 'Code Submissions',
      value: overview.totalSubmissions,
      sub: 'Tested & evaluated',
      icon: CheckCircle,
      color: 'border-sandwich-800 bg-sandwich-900/90 text-sandwich-100',
    },
    {
      title: '1v1 Battles',
      value: overview.totalBattles,
      sub: 'Multiplayer duels',
      icon: Swords,
      color: 'border-sandwich-800 bg-sandwich-900/90 text-sandwich-100',
    },
    {
      title: 'Open Reports',
      value: overview.openReports,
      sub: overview.openReports > 0 ? 'Requires attention' : 'Queue clear',
      icon: Flag,
      color: overview.openReports > 0
        ? 'border-rose-900/60 bg-sandwich-900/90 text-rose-400'
        : 'border-sandwich-800 bg-sandwich-900/90 text-sandwich-400',
      link: '/admin/reports',
    },
    {
      title: 'High-Risk Alerts',
      value: overview.highRiskIntegrityAlerts,
      sub: 'Anti-cheat triggers',
      icon: ShieldAlert,
      color: overview.highRiskIntegrityAlerts > 0
        ? 'border-rose-900/60 bg-sandwich-900/90 text-rose-400'
        : 'border-sandwich-800 bg-sandwich-900/90 text-sandwich-400',
      link: '/admin/integrity',
    },
    {
      title: 'AI Coach Invocations',
      value: overview.totalAiQueriesToday,
      sub: 'Queries today',
      icon: Bot,
      color: 'border-sandwich-800 bg-sandwich-900/90 text-sandwich-100',
    },
  ];

  return (
    <div className="space-y-8">
      {/* Welcome Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold text-sandwich-100 tracking-tight">Platform Telemetry & Governance</h2>
          <p className="text-xs text-sandwich-400 mt-1">
            Real-time platform metrics, security integrity, moderation reports, and game health.
          </p>
        </div>
        <button
          onClick={fetchOverview}
          className="flex items-center gap-2 px-3 py-1.5 rounded-xl border border-sandwich-700 bg-sandwich-900 hover:bg-sandwich-800 text-sandwich-200 text-xs font-medium transition self-start sm:self-auto shadow-sm"
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
              className={`p-5 rounded-2xl border ${kpi.color} shadow-luxury backdrop-blur-xl relative flex flex-col justify-between`}
            >
              <div className="flex items-center justify-between mb-3">
                <span className="text-xs font-medium text-sandwich-400 font-mono">{kpi.title}</span>
                <Icon className="w-5 h-5 text-sandwich-300" />
              </div>
              <div>
                <div className="text-2xl font-bold font-mono tracking-tight text-sandwich-50">{kpi.value.toLocaleString()}</div>
                <div className="text-[11px] text-sandwich-400 mt-1 font-mono">{kpi.sub}</div>
              </div>
              {kpi.link && (
                <Link
                  to={kpi.link}
                  className="mt-4 pt-3 border-t border-sandwich-800 flex items-center justify-between text-xs text-sandwich-300 hover:text-white transition font-medium"
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
        <div className="p-6 rounded-2xl bg-sandwich-900/90 border border-sandwich-800 shadow-luxury space-y-3">
          <div className="flex items-center gap-2 text-rose-400">
            <Flag className="w-5 h-5" />
            <h3 className="text-sm font-semibold text-sandwich-100">Community Moderation</h3>
          </div>
          <p className="text-xs text-sandwich-400 leading-relaxed">
            Review user reports on harassing behavior, cheating exploits, offensive handles, or match irregularities. Take decisive actions with resolution logs.
          </p>
          <Link
            to="/admin/reports"
            className="inline-flex items-center gap-2 text-xs font-semibold text-sandwich-200 hover:text-white transition mt-2"
          >
            <span>Go to Moderation Queue ({overview.openReports} open)</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </Link>
        </div>

        <div className="p-6 rounded-2xl bg-sandwich-900/90 border border-sandwich-800 shadow-luxury space-y-3">
          <div className="flex items-center gap-2 text-sandwich-200">
            <ShieldAlert className="w-5 h-5" />
            <h3 className="text-sm font-semibold text-sandwich-100">Anti-Cheat & Integrity Engine</h3>
          </div>
          <p className="text-xs text-sandwich-400 leading-relaxed">
            Inspect automated heuristics detecting impossible solve timing, rapid code execution bursts, code plagiarism patterns, and abnormal battle anomalies.
          </p>
          <Link
            to="/admin/integrity"
            className="inline-flex items-center gap-2 text-xs font-semibold text-sandwich-200 hover:text-white transition mt-2"
          >
            <span>Review Integrity Events ({overview.highRiskIntegrityAlerts} critical)</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </Link>
        </div>
      </div>
    </div>
  );
};
