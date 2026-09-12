import React, { useEffect, useState } from 'react';
import {
  Code2,
  Plus,
  RefreshCw,
  Archive,
  CheckCircle,
  AlertTriangle,
  X,
  UploadCloud,
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

  // Bulk import modal state
  const [isImportModalOpen, setIsImportModalOpen] = useState(false);
  const [importJsonText, setImportJsonText] = useState('');
  const [importing, setImporting] = useState(false);
  const [importResult, setImportResult] = useState<{ total: number; inserted: number; skipped: number; errors: string[] } | null>(null);

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

  const handleBulkImport = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!importJsonText.trim()) return;
    try {
      setImporting(true);
      setImportResult(null);
      let parsed = JSON.parse(importJsonText.trim());
      if (!Array.isArray(parsed)) {
        if (parsed.challenges && Array.isArray(parsed.challenges)) {
          parsed = parsed.challenges;
        } else {
          parsed = [parsed];
        }
      }
      const res = await adminService.bulkImportChallenges(parsed);
      setImportResult(res);
      await fetchChallenges();
    } catch (err: unknown) {
      alert(err instanceof Error ? err.message : 'Invalid JSON format or import failed');
    } finally {
      setImporting(false);
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
          <h2 className="text-2xl font-black text-slate-900 tracking-tight flex items-center gap-2">
            <Code2 className="w-5 h-5 text-emerald-600" />
            <span>Challenge Catalog Lifecycle</span>
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Author, edit, bulk import, and archive competitive programming katas and test suites.
          </p>
        </div>

        <div className="flex items-center gap-2.5 self-start sm:self-auto">
          <button
            onClick={() => {
              setIsImportModalOpen(true);
              setImportResult(null);
            }}
            className="flex items-center gap-2 px-3.5 py-2 rounded-xl border border-slate-200 bg-white hover:bg-slate-50 text-slate-700 text-xs font-bold shadow-2xs transition"
          >
            <UploadCloud className="w-4 h-4 text-indigo-600" />
            <span>Bulk Import</span>
          </button>

          <button
            onClick={() => setIsModalOpen(true)}
            className="flex items-center gap-2 px-4 py-2 rounded-xl bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-700 hover:to-teal-700 text-white text-xs font-bold shadow-sm shadow-emerald-500/25 transition"
          >
            <Plus className="w-4 h-4" />
            <span>Create Challenge</span>
          </button>
        </div>
      </div>

      {error && (
        <div className="p-4 rounded-xl bg-rose-50 border border-rose-200 text-rose-700 text-xs flex items-center gap-2">
          <AlertTriangle className="w-4 h-4" />
          <span>{error}</span>
        </div>
      )}

      {/* Challenges Table */}
      <div className="border border-slate-200/90 rounded-2xl bg-white overflow-hidden shadow-2xs">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-700">
            <thead className="bg-slate-50/90 text-slate-500 uppercase text-[10px] tracking-wider border-b border-slate-200/80 font-mono">
              <tr>
                <th className="px-5 py-3.5 font-bold">Title</th>
                <th className="px-5 py-3.5 font-bold">Difficulty</th>
                <th className="px-5 py-3.5 font-bold">Category</th>
                <th className="px-5 py-3.5 font-bold">Reward</th>
                <th className="px-5 py-3.5 font-bold">Test Cases</th>
                <th className="px-5 py-3.5 font-bold">Status</th>
                <th className="px-5 py-3.5 font-bold text-right">Lifecycle</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan={7} className="px-5 py-12 text-center text-slate-500">
                    <RefreshCw className="w-5 h-5 animate-spin mx-auto mb-2 text-emerald-600" />
                    <span>Loading catalog...</span>
                  </td>
                </tr>
              ) : challenges.length === 0 ? (
                <tr>
                  <td colSpan={7} className="px-5 py-12 text-center text-slate-400">
                    No challenges registered.
                  </td>
                </tr>
              ) : (
                challenges.map((c) => (
                  <tr key={c.id} className="hover:bg-slate-50/80 transition">
                    <td className="px-5 py-3.5">
                      <div className="font-bold text-slate-900">{c.title}</div>
                      <div className="text-[11px] text-slate-400 font-mono">{c.slug}</div>
                    </td>
                    <td className="px-5 py-3.5">
                      <span
                        className={`px-2.5 py-0.5 rounded-full text-[10px] font-bold ${
                          c.difficulty === 'EASY'
                            ? 'bg-emerald-50 text-emerald-800 border border-emerald-200'
                            : c.difficulty === 'MEDIUM'
                            ? 'bg-amber-50 text-amber-800 border border-amber-200'
                            : 'bg-rose-50 text-rose-800 border border-rose-200'
                        }`}
                      >
                        {c.difficulty}
                      </span>
                    </td>
                    <td className="px-5 py-3.5 text-slate-500">{c.category}</td>
                    <td className="px-5 py-3.5 text-amber-600 font-bold font-mono">+{c.xpReward} XP</td>
                    <td className="px-5 py-3.5 text-slate-700 font-mono">{c.testCaseCount} tests</td>
                    <td className="px-5 py-3.5">
                      <span
                        className={`px-2.5 py-0.5 rounded-full text-[10px] font-bold ${
                          c.status === 'PUBLISHED'
                            ? 'bg-emerald-50 text-emerald-800 border border-emerald-200'
                            : c.status === 'DRAFT'
                            ? 'bg-amber-50 text-amber-800 border border-amber-200'
                            : 'bg-slate-100 text-slate-600 border border-slate-200'
                        }`}
                      >
                        {c.status}
                      </span>
                    </td>
                    <td className="px-5 py-3.5 text-right">
                      {c.status === 'PUBLISHED' ? (
                        <button
                          onClick={() => handleStatusToggle(c)}
                          className="inline-flex items-center gap-1 px-3 py-1 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold transition shadow-2xs"
                        >
                          <Archive className="w-3.5 h-3.5" />
                          <span>Archive</span>
                        </button>
                      ) : (
                        <button
                          onClick={() => handleStatusToggle(c)}
                          className="inline-flex items-center gap-1 px-3 py-1 rounded-xl bg-emerald-50 hover:bg-emerald-100 text-emerald-700 border border-emerald-200 text-xs font-bold transition shadow-2xs"
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
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-sm overflow-y-auto">
          <div className="w-full max-w-xl bg-white border border-slate-200 rounded-3xl p-6 shadow-premium-hover relative my-8">
            <button
              onClick={() => setIsModalOpen(false)}
              className="absolute top-4 right-4 p-1.5 rounded-xl text-slate-400 hover:text-slate-800 hover:bg-slate-100 transition"
            >
              <X className="w-5 h-5" />
            </button>

            <h3 className="text-lg font-black text-slate-900 mb-4">Create New Coding Challenge</h3>

            <form onSubmit={handleCreateSubmit} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Title</label>
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
                    className="w-full bg-slate-50 border border-slate-200 rounded-xl px-3 py-2 text-xs text-slate-800 focus:outline-none focus:border-emerald-600"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Slug</label>
                  <input
                    type="text"
                    required
                    value={formData.slug}
                    onChange={(e) => setFormData({ ...formData, slug: e.target.value })}
                    placeholder="invert-binary-tree"
                    className="w-full bg-slate-50 border border-slate-200 rounded-xl px-3 py-2 text-xs text-slate-800 focus:outline-none focus:border-emerald-600 font-mono"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Problem Description</label>
                <textarea
                  required
                  rows={4}
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  placeholder="Describe problem, inputs, outputs, constraints, and sample cases..."
                  className="w-full bg-slate-50 border border-slate-200 rounded-xl p-3 text-xs text-slate-800 focus:outline-none focus:border-emerald-600 resize-none font-mono"
                />
              </div>

              <div className="grid grid-cols-3 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Difficulty</label>
                  <select
                    value={formData.difficulty}
                    onChange={(e) => setFormData({ ...formData, difficulty: e.target.value })}
                    className="w-full bg-slate-50 border border-slate-200 rounded-xl px-3 py-2 text-xs text-slate-800 focus:outline-none focus:border-emerald-600"
                  >
                    <option value="EASY">EASY</option>
                    <option value="MEDIUM">MEDIUM</option>
                    <option value="HARD">HARD</option>
                    <option value="EXPERT">EXPERT</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Category</label>
                  <select
                    value={formData.category}
                    onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                    className="w-full bg-slate-50 border border-slate-200 rounded-xl px-3 py-2 text-xs text-slate-800 focus:outline-none focus:border-emerald-600"
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
                  <label className="block text-xs font-semibold text-slate-700 mb-1">XP Reward</label>
                  <input
                    type="number"
                    value={formData.xpReward}
                    onChange={(e) => setFormData({ ...formData, xpReward: parseInt(e.target.value) || 100 })}
                    className="w-full bg-slate-50 border border-slate-200 rounded-xl px-3 py-2 text-xs text-slate-800 focus:outline-none focus:border-emerald-600 font-mono"
                  />
                </div>
              </div>

              <div className="flex gap-3 pt-2">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="flex-1 px-4 py-2.5 rounded-xl border border-slate-200 text-slate-700 hover:bg-slate-50 text-xs font-bold transition shadow-2xs"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={saving}
                  className="flex-1 px-4 py-2.5 rounded-xl bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-700 hover:to-teal-700 text-white text-xs font-bold transition disabled:opacity-50 shadow-sm"
                >
                  {saving ? 'Creating...' : 'Create & Publish'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Bulk Import Modal */}
      {isImportModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-sm overflow-y-auto">
          <div className="w-full max-w-2xl bg-white border border-slate-200 rounded-3xl p-6 shadow-premium-hover relative my-8">
            <button
              onClick={() => setIsImportModalOpen(false)}
              className="absolute top-4 right-4 p-1.5 rounded-xl text-slate-400 hover:text-slate-800 hover:bg-slate-100 transition"
            >
              <X className="w-5 h-5" />
            </button>

            <h3 className="text-lg font-black text-slate-900 mb-1 flex items-center gap-2">
              <UploadCloud className="w-5 h-5 text-indigo-600" />
              <span>Bulk Import Problem Dataset</span>
            </h3>
            <p className="text-xs text-slate-500 mb-4">
              Paste JSON dataset array or upload a dataset file. Existing problems with duplicate titles/slugs will be safely skipped.
            </p>

            <form onSubmit={handleBulkImport} className="space-y-4">
              <div>
                <div className="flex items-center justify-between mb-1">
                  <label className="text-xs font-semibold text-slate-700">JSON Dataset Array</label>
                  <label className="text-[11px] font-bold text-indigo-600 hover:text-indigo-800 cursor-pointer">
                    Upload .json file
                    <input
                      type="file"
                      accept=".json,.csv"
                      className="hidden"
                      onChange={(e) => {
                        const file = e.target.files?.[0];
                        if (file) {
                          const reader = new FileReader();
                          reader.onload = (event) => {
                            if (event.target?.result) {
                              setImportJsonText(event.target.result as string);
                            }
                          };
                          reader.readAsText(file);
                        }
                      }}
                    />
                  </label>
                </div>
                <textarea
                  required
                  rows={8}
                  value={importJsonText}
                  onChange={(e) => setImportJsonText(e.target.value)}
                  placeholder={`[\n  {\n    "title": "Median of Two Sorted Arrays",\n    "difficulty": "HARD",\n    "category": "ARRAYS",\n    "problemType": "CODING",\n    "description": "Given two sorted arrays...",\n    "xpReward": 250\n  }\n]`}
                  className="w-full bg-slate-50 border border-slate-200 rounded-xl p-3 text-xs text-slate-800 focus:outline-none focus:border-indigo-600 resize-none font-mono"
                />
              </div>

              {/* Import Results Banner */}
              {importResult && (
                <div className="p-4 rounded-2xl bg-indigo-50/80 border border-indigo-200 text-xs font-mono space-y-1">
                  <p className="font-bold text-indigo-900">
                    Import Finished: {importResult.inserted} inserted, {importResult.skipped} skipped (Total: {importResult.total})
                  </p>
                  {importResult.errors && importResult.errors.length > 0 && (
                    <div className="text-rose-600 text-[11px] pt-1">
                      {importResult.errors.map((err, i) => (
                        <p key={i}>• {err}</p>
                      ))}
                    </div>
                  )}
                </div>
              )}

              <div className="flex gap-3 pt-2">
                <button
                  type="button"
                  onClick={() => setIsImportModalOpen(false)}
                  className="flex-1 px-4 py-2.5 rounded-xl border border-slate-200 text-slate-700 hover:bg-slate-50 text-xs font-bold transition shadow-2xs"
                >
                  Close
                </button>
                <button
                  type="submit"
                  disabled={importing || !importJsonText.trim()}
                  className="flex-1 px-4 py-2.5 rounded-xl bg-gradient-to-r from-indigo-600 to-violet-600 hover:from-indigo-700 hover:to-violet-700 text-white text-xs font-bold transition disabled:opacity-50 shadow-sm"
                >
                  {importing ? 'Importing Dataset...' : 'Execute Import'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
