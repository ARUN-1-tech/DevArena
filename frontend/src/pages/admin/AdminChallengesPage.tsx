import React, { useEffect, useState } from 'react';
import {
  Code2,
  Plus,
  RefreshCw,
  Archive,
  CheckCircle,
  AlertTriangle,
  X,
} from 'lucide-react';
import { adminService } from '../../services/adminService';
import { AdminChallengeDto, UpsertChallengeRequest } from '../../types/admin';

export const AdminChallengesPage: React.FC = () => {
  const [challenges, setChallenges] = useState<AdminChallengeDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Modal state for creating new challenge
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [saving, setSaving] = useState(false);
  const [formData, setFormData] = useState<UpsertChallengeRequest>({
    title: '',
    slug: '',
    description: '',
    difficulty: 'MEDIUM',
    category: 'ALGORITHMS',
    xpReward: 100,
    estimatedMinutes: 20,
    tags: 'algorithm',
    status: 'PUBLISHED',
    testCases: [
      { input: '[1, 2, 3]', expectedOutput: '[3, 2, 1]', hidden: false, orderIndex: 1, explanation: 'Sample reverse' },
    ],
  });

  const fetchChallenges = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await adminService.getChallenges();
      setChallenges(data.content);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to load challenges');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchChallenges();
  }, []);

  const handleStatusToggle = async (challenge: AdminChallengeDto) => {
    const nextStatus = challenge.status === 'PUBLISHED' ? 'ARCHIVED' : 'PUBLISHED';
    try {
      const updated = await adminService.updateChallengeStatus(challenge.id, nextStatus);
      setChallenges((prev) => prev.map((c) => (c.id === updated.id ? updated : c)));
    } catch (err: unknown) {
      alert(err instanceof Error ? err.message : 'Failed to update challenge status');
    }
  };

  const handleCreateSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      const created = await adminService.createChallenge(formData);
      setChallenges((prev) => [created, ...prev]);
      setIsModalOpen(false);
      setFormData({
        title: '',
        slug: '',
        description: '',
        difficulty: 'MEDIUM',
        category: 'ALGORITHMS',
        xpReward: 100,
        estimatedMinutes: 20,
        tags: 'algorithm',
        status: 'PUBLISHED',
        testCases: [{ input: '', expectedOutput: '', hidden: false, orderIndex: 1 }],
      });
    } catch (err: unknown) {
      alert(err instanceof Error ? err.message : 'Failed to create challenge');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold text-white tracking-tight flex items-center gap-2">
            <Code2 className="w-5 h-5 text-emerald-400" />
            <span>Challenge Catalog Lifecycle</span>
          </h2>
          <p className="text-xs text-slate-400 mt-1">
            Author, edit, publish, and archive competitive programming katas and test suites.
          </p>
        </div>

        <button
          onClick={() => setIsModalOpen(true)}
          className="flex items-center gap-2 px-4 py-2 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-semibold shadow-md transition self-start sm:self-auto"
        >
          <Plus className="w-4 h-4" />
          <span>Create Challenge</span>
        </button>
      </div>

      {error && (
        <div className="p-4 rounded-xl bg-rose-500/10 border border-rose-500/20 text-rose-400 text-xs flex items-center gap-2">
          <AlertTriangle className="w-4 h-4" />
          <span>{error}</span>
        </div>
      )}

      {/* Challenges Table */}
      <div className="border border-slate-800 rounded-2xl bg-slate-900/60 overflow-hidden shadow-sm">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-300">
            <thead className="bg-slate-900/90 text-slate-400 uppercase text-[10px] tracking-wider border-b border-slate-800">
              <tr>
                <th className="px-5 py-3.5 font-semibold">Title</th>
                <th className="px-5 py-3.5 font-semibold">Difficulty</th>
                <th className="px-5 py-3.5 font-semibold">Category</th>
                <th className="px-5 py-3.5 font-semibold">Reward</th>
                <th className="px-5 py-3.5 font-semibold">Test Cases</th>
                <th className="px-5 py-3.5 font-semibold">Status</th>
                <th className="px-5 py-3.5 font-semibold text-right">Lifecycle</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60">
              {loading ? (
                <tr>
                  <td colSpan={7} className="px-5 py-12 text-center text-slate-500">
                    <RefreshCw className="w-5 h-5 animate-spin mx-auto mb-2 text-emerald-400" />
                    <span>Loading catalog...</span>
                  </td>
                </tr>
              ) : challenges.length === 0 ? (
                <tr>
                  <td colSpan={7} className="px-5 py-12 text-center text-slate-500">
                    No challenges registered.
                  </td>
                </tr>
              ) : (
                challenges.map((c) => (
                  <tr key={c.id} className="hover:bg-slate-800/40 transition">
                    <td className="px-5 py-3.5">
                      <div className="font-semibold text-white">{c.title}</div>
                      <div className="text-[11px] text-slate-500 font-mono">{c.slug}</div>
                    </td>
                    <td className="px-5 py-3.5">
                      <span
                        className={`px-2 py-0.5 rounded-full text-[10px] font-semibold ${
                          c.difficulty === 'EASY'
                            ? 'bg-emerald-500/10 text-emerald-400'
                            : c.difficulty === 'MEDIUM'
                            ? 'bg-amber-500/10 text-amber-300'
                            : 'bg-rose-500/10 text-rose-400'
                        }`}
                      >
                        {c.difficulty}
                      </span>
                    </td>
                    <td className="px-5 py-3.5 text-slate-400">{c.category}</td>
                    <td className="px-5 py-3.5 text-amber-400 font-medium">+{c.xpReward} XP</td>
                    <td className="px-5 py-3.5 text-slate-300">{c.testCaseCount} tests</td>
                    <td className="px-5 py-3.5">
                      <span
                        className={`px-2 py-0.5 rounded-full text-[10px] font-semibold ${
                          c.status === 'PUBLISHED'
                            ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20'
                            : c.status === 'DRAFT'
                            ? 'bg-amber-500/10 text-amber-300 border border-amber-500/20'
                            : 'bg-slate-800 text-slate-500 border border-slate-700'
                        }`}
                      >
                        {c.status}
                      </span>
                    </td>
                    <td className="px-5 py-3.5 text-right">
                      {c.status === 'PUBLISHED' ? (
                        <button
                          onClick={() => handleStatusToggle(c)}
                          className="inline-flex items-center gap-1 px-2.5 py-1 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-400 hover:text-slate-200 text-xs font-medium transition"
                        >
                          <Archive className="w-3.5 h-3.5" />
                          <span>Archive</span>
                        </button>
                      ) : (
                        <button
                          onClick={() => handleStatusToggle(c)}
                          className="inline-flex items-center gap-1 px-2.5 py-1 rounded-lg bg-emerald-600/10 hover:bg-emerald-600/20 text-emerald-400 border border-emerald-500/20 text-xs font-medium transition"
                        >
                          <CheckCircle className="w-3.5 h-3.5" />
                          <span>Publish</span>
                        </button>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Create Challenge Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm overflow-y-auto">
          <div className="w-full max-w-xl bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-2xl relative my-8">
            <button
              onClick={() => setIsModalOpen(false)}
              className="absolute top-4 right-4 p-1.5 rounded-lg text-slate-400 hover:text-slate-200 hover:bg-slate-800 transition"
            >
              <X className="w-5 h-5" />
            </button>

            <h3 className="text-base font-bold text-white mb-4">Create New Coding Challenge</h3>

            <form onSubmit={handleCreateSubmit} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">Title</label>
                  <input
                    type="text"
                    required
                    value={formData.title}
                    onChange={(e) => {
                      const title = e.target.value;
                      const slug = title.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, '');
                      setFormData({ ...formData, title, slug });
                    }}
                    placeholder="e.g. Invert Binary Tree"
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-200 focus:outline-none focus:border-emerald-500"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">Slug</label>
                  <input
                    type="text"
                    required
                    value={formData.slug}
                    onChange={(e) => setFormData({ ...formData, slug: e.target.value })}
                    placeholder="invert-binary-tree"
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-200 focus:outline-none focus:border-emerald-500"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-300 mb-1">Problem Description</label>
                <textarea
                  required
                  rows={4}
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  placeholder="Describe problem, inputs, outputs, constraints, and sample cases..."
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl p-3 text-xs text-slate-200 focus:outline-none focus:border-emerald-500 resize-none font-mono"
                />
              </div>

              <div className="grid grid-cols-3 gap-3">
                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">Difficulty</label>
                  <select
                    value={formData.difficulty}
                    onChange={(e) => setFormData({ ...formData, difficulty: e.target.value })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-200 focus:outline-none focus:border-emerald-500"
                  >
                    <option value="EASY">EASY</option>
                    <option value="MEDIUM">MEDIUM</option>
                    <option value="HARD">HARD</option>
                    <option value="EXPERT">EXPERT</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">Category</label>
                  <select
                    value={formData.category}
                    onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-200 focus:outline-none focus:border-emerald-500"
                  >
                    <option value="ARRAYS">ARRAYS</option>
                    <option value="STRINGS">STRINGS</option>
                    <option value="LINKED_LIST">LINKED_LIST</option>
                    <option value="TREES">TREES</option>
                    <option value="GRAPHS">GRAPHS</option>
                    <option value="DYNAMIC_PROGRAMMING">DYNAMIC_PROGRAMMING</option>
                    <option value="ALGORITHMS">ALGORITHMS</option>
                    <option value="STACK_QUEUE">STACK_QUEUE</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">XP Reward</label>
                  <input
                    type="number"
                    value={formData.xpReward}
                    onChange={(e) => setFormData({ ...formData, xpReward: parseInt(e.target.value) || 100 })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-200 focus:outline-none focus:border-emerald-500"
                  />
                </div>
              </div>

              <div className="flex gap-3 pt-2">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="flex-1 px-4 py-2.5 rounded-xl border border-slate-800 text-slate-300 hover:bg-slate-800 text-xs font-medium transition"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={saving}
                  className="flex-1 px-4 py-2.5 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-medium transition disabled:opacity-50"
                >
                  {saving ? 'Creating...' : 'Create & Publish'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
