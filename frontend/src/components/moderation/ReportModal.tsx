import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Flag, X, AlertTriangle, CheckCircle2 } from 'lucide-react';
import { reportService } from '../../services/reportService';
import { ReportReason, ReportTargetType } from '../../types/moderation';

interface ReportModalProps {
  isOpen: boolean;
  onClose: () => void;
  targetType: ReportTargetType;
  targetId: string;
  targetName?: string;
}

export const ReportModal: React.FC<ReportModalProps> = ({
  isOpen,
  onClose,
  targetType,
  targetId,
  targetName,
}) => {
  const [reason, setReason] = useState<ReportReason>('HARASSMENT');
  const [description, setDescription] = useState('');
  const [loading, setLoading] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await reportService.createReport({
        targetType,
        targetId,
        reason,
        description: description.trim() || undefined,
      });
      setSubmitted(true);
      setTimeout(() => {
        setSubmitted(false);
        setDescription('');
        onClose();
      }, 1500);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to submit report. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <AnimatePresence>
      <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-sandwich-950/80 backdrop-blur-md">
        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          exit={{ opacity: 0, scale: 0.95 }}
          className="w-full max-w-md bg-sandwich-900 border border-sandwich-700 rounded-2xl p-6 shadow-luxury relative text-sandwich-100 backdrop-blur-xl"
        >
          <button
            onClick={onClose}
            className="absolute top-4 right-4 p-1.5 rounded-lg text-sandwich-400 hover:text-sandwich-100 hover:bg-sandwich-800 transition"
          >
            <X className="w-5 h-5" />
          </button>

          {submitted ? (
            <div className="py-8 flex flex-col items-center justify-center text-center">
              <CheckCircle2 className="w-12 h-12 text-emerald-400 mb-3" />
              <h3 className="text-lg font-semibold text-sandwich-100">Report Submitted</h3>
              <p className="text-xs text-sandwich-400 mt-1 max-w-xs">
                Thank you for helping maintain fair play and safety on DevArena. Our moderation team will review this promptly.
              </p>
            </div>
          ) : (
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-sandwich-800 text-rose-400 flex items-center justify-center border border-sandwich-700 shadow-sm">
                  <Flag className="w-5 h-5" />
                </div>
                <div>
                  <h3 className="text-base font-semibold text-sandwich-100">Report {targetType}</h3>
                  <p className="text-xs text-sandwich-400">
                    Target: <span className="font-medium text-sandwich-200">{targetName || targetId}</span>
                  </p>
                </div>
              </div>

              {error && (
                <div className="p-3 rounded-xl bg-rose-950/40 border border-rose-900/60 text-rose-400 text-xs flex items-center gap-2">
                  <AlertTriangle className="w-4 h-4 shrink-0" />
                  <span>{error}</span>
                </div>
              )}

              <div>
                <label className="block text-xs font-medium text-sandwich-300 mb-1.5 font-mono">
                  Reason for report
                </label>
                <select
                  value={reason}
                  onChange={(e) => setReason(e.target.value as ReportReason)}
                  className="w-full bg-sandwich-950 border border-sandwich-700 rounded-xl px-3.5 py-2.5 text-xs text-sandwich-100 focus:outline-none focus:border-sandwich-400 transition"
                >
                  <option value="CHEATING">Cheating / Automated Scripts</option>
                  <option value="HARASSMENT">Harassment / Abusive Behavior</option>
                  <option value="OFFENSIVE_NAME">Inappropriate / Offensive Username</option>
                  <option value="EXPLOIT">System Bug / Exploit Abuse</option>
                  <option value="OTHER">Other Violation</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-medium text-sandwich-300 mb-1.5 font-mono">
                  Additional Details (optional)
                </label>
                <textarea
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  rows={3}
                  placeholder="Provide context or description of what happened..."
                  className="w-full bg-sandwich-950 border border-sandwich-700 rounded-xl p-3 text-xs text-sandwich-100 placeholder-sandwich-500 focus:outline-none focus:border-sandwich-400 transition resize-none"
                />
              </div>

              <div className="flex gap-3 pt-2">
                <button
                  type="button"
                  onClick={onClose}
                  className="flex-1 px-4 py-2.5 rounded-xl border border-sandwich-700 text-sandwich-300 hover:bg-sandwich-800 text-xs font-medium transition"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={loading}
                  className="flex-1 px-4 py-2.5 rounded-xl bg-rose-600 hover:bg-rose-500 text-white text-xs font-bold transition disabled:opacity-50 shadow-sm"
                >
                  {loading ? 'Submitting...' : 'Submit Report'}
                </button>
              </div>
            </form>
          )}
        </motion.div>
      </div>
    </AnimatePresence>
  );
};
