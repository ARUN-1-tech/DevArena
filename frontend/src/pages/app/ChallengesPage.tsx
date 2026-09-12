import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { challengeService } from '../../services/challengeService';
import { Challenge, ChallengeDifficulty, ChallengeCategory } from '../../types/arena';
import { Card } from '../../components/ui/Card';
import { Badge } from '../../components/ui/Badge';
import { Input } from '../../components/ui/Input';
import { Button } from '../../components/ui/Button';
import {
  Code2,
  Search,
  CheckCircle2,
  Clock,
  Sparkles,
  Play,
  Filter,
  ArrowRight,
  Loader2,
} from 'lucide-react';
import { motion } from 'framer-motion';

const DIFFICULTIES: { label: string; value?: ChallengeDifficulty }[] = [
  { label: 'All Difficulties', value: undefined },
  { label: 'Easy', value: 'EASY' },
  { label: 'Medium', value: 'MEDIUM' },
  { label: 'Hard', value: 'HARD' },
  { label: 'Expert', value: 'EXPERT' },
];

const CATEGORIES: { label: string; value?: ChallengeCategory }[] = [
  { label: 'All Categories', value: undefined },
  { label: 'Arrays', value: 'ARRAYS' },
  { label: 'Strings', value: 'STRINGS' },
  { label: 'Linked List', value: 'LINKED_LIST' },
  { label: 'Stack & Queue', value: 'STACK_QUEUE' },
  { label: 'Trees', value: 'TREES' },
  { label: 'Graphs', value: 'GRAPHS' },
  { label: 'Dynamic Programming', value: 'DYNAMIC_PROGRAMMING' },
  { label: 'Algorithms', value: 'ALGORITHMS' },
];

export const ChallengesPage: React.FC = () => {
  const navigate = useNavigate();

  const [challenges, setChallenges] = useState<Challenge[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [selectedDifficulty, setSelectedDifficulty] = useState<ChallengeDifficulty | undefined>(undefined);
  const [selectedCategory, setSelectedCategory] = useState<ChallengeCategory | undefined>(undefined);
  const [totalCount, setTotalCount] = useState(0);

  useEffect(() => {
    loadChallenges();
  }, [search, selectedDifficulty, selectedCategory]);

  const loadChallenges = async () => {
    try {
      setLoading(true);
      const res = await challengeService.getChallenges({
        search: search.trim() || undefined,
        difficulty: selectedDifficulty,
        category: selectedCategory,
        size: 50,
      });
      setChallenges(res.content);
      setTotalCount(res.totalElements);
    } catch (err) {
      console.error('Failed to load challenges', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-8 max-w-6xl mx-auto">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <Badge variant="cyan" size="sm" className="mb-2">
            <Code2 className="w-3.5 h-3.5 mr-1" />
            PRACTICE ARCHIVE
          </Badge>
          <h1 className="text-3xl sm:text-4xl font-black text-slate-900 tracking-tight">
            ALGORITHMIC KATAS
          </h1>
          <p className="text-sm text-slate-600 mt-1">
            Hone your problem-solving skills across foundational data structures and patterns.
          </p>
        </div>

        <div className="bg-white px-4 py-2.5 rounded-2xl border border-slate-200/90 shadow-sm shrink-0 font-mono">
          <p className="text-[10px] text-slate-400 uppercase font-semibold">AVAILABLE KATAS</p>
          <p className="text-lg font-black text-slate-900">{totalCount} Challenges</p>
        </div>
      </div>

      {/* Filter and Search Bar */}
      <div className="bg-white p-4 rounded-2xl border border-slate-200/90 shadow-sm space-y-4">
        <div className="flex flex-col sm:flex-row gap-3">
          <div className="flex-1">
            <Input
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search problems by title or concept tags..."
              leftIcon={<Search className="w-4 h-4 text-slate-400" />}
            />
          </div>

          <div className="sm:w-64">
            <select
              value={selectedCategory || ''}
              onChange={(e) =>
                setSelectedCategory(e.target.value ? (e.target.value as ChallengeCategory) : undefined)
              }
              aria-label="Filter by Topic Category"
              className="w-full h-11 px-3 rounded-xl border border-slate-200 bg-white text-sm text-slate-700 font-mono focus:outline-none focus:ring-2 focus:ring-cyan-500"
            >
              {CATEGORIES.map((cat) => (
                <option key={cat.label} value={cat.value || ''}>
                  {cat.label}
                </option>
              ))}
            </select>
          </div>
        </div>

        {/* Difficulty Filter Pills */}
        <div className="flex items-center gap-2 overflow-x-auto pb-1 font-mono text-xs">
          <Filter className="w-3.5 h-3.5 text-slate-400 shrink-0 mr-1" />
          {DIFFICULTIES.map((diff) => {
            const isSelected = selectedDifficulty === diff.value;
            return (
              <button
                key={diff.label}
                onClick={() => setSelectedDifficulty(diff.value)}
                className={`px-3.5 py-1.5 rounded-xl text-xs font-bold transition-all shrink-0 cursor-pointer ${
                  isSelected
                    ? 'bg-gradient-to-r from-indigo-600 to-violet-600 text-white shadow-sm shadow-indigo-500/25 border border-indigo-500/30'
                    : 'bg-white/90 backdrop-blur-sm border border-slate-200/90 hover:bg-slate-50 text-slate-700 hover:border-slate-300'
                }`}
              >
                {diff.label}
              </button>
            );
          })}
        </div>
      </div>

      {/* Challenges List */}
      {loading ? (
        <div className="flex flex-col items-center justify-center py-20 space-y-3">
          <Loader2 className="w-8 h-8 animate-spin text-cyan-600" />
          <p className="text-sm font-mono text-slate-500">Scanning arena catalog...</p>
        </div>
      ) : challenges.length === 0 ? (
        <Card className="p-12 text-center max-w-md mx-auto space-y-3">
          <Code2 className="w-10 h-10 text-slate-400 mx-auto" />
          <h3 className="text-base font-bold text-slate-800">No challenges match your criteria</h3>
          <p className="text-xs text-slate-500">
            Try adjusting your search terms or clearing your difficulty/category filters.
          </p>
          <Button
            variant="outline"
            size="sm"
            onClick={() => {
              setSearch('');
              setSelectedDifficulty(undefined);
              setSelectedCategory(undefined);
            }}
          >
            Clear Filters
          </Button>
        </Card>
      ) : (
        <div className="space-y-3">
          {challenges.map((ch, index) => {
            const isSolved = ch.progressStatus === 'SOLVED';
            const isAttempted = ch.progressStatus === 'ATTEMPTED';

            return (
              <motion.div
                key={ch.id}
                initial={{ opacity: 0, y: 8 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.25, delay: index * 0.03 }}
              >
                <Card
                  className="p-5 bg-white/95 backdrop-blur-xl border border-slate-200/80 shadow-premium hover:shadow-premium-hover hover:border-indigo-300/80 transition-all duration-300 rounded-2xl cursor-pointer flex flex-col sm:flex-row sm:items-center justify-between gap-4 group shimmer-card"
                  onClick={() => navigate(`/challenges/${ch.id}`)}
                >
                  <div className="space-y-1.5 flex-1">
                    <div className="flex items-center gap-2.5 flex-wrap">
                      <span className="text-base font-black text-slate-900 group-hover:text-indigo-600 transition-colors">
                        {ch.title}
                      </span>

                      {/* Difficulty Badge */}
                      <span
                        className={`text-[10px] font-mono px-2 py-0.5 rounded-full font-bold uppercase shadow-2xs ${
                          ch.difficulty === 'EASY'
                            ? 'bg-emerald-50 text-emerald-800 border border-emerald-200'
                            : ch.difficulty === 'MEDIUM'
                            ? 'bg-amber-50 text-amber-800 border border-amber-200'
                            : ch.difficulty === 'HARD'
                            ? 'bg-rose-50 text-rose-800 border border-rose-200'
                            : 'bg-purple-50 text-purple-800 border border-purple-200'
                        }`}
                      >
                        {ch.difficulty}
                      </span>

                      {/* Status indicator */}
                      {isSolved ? (
                        <span className="inline-flex items-center gap-1 text-[10px] font-mono font-bold text-emerald-700 bg-emerald-50 px-2.5 py-0.5 rounded-full border border-emerald-200 shadow-2xs">
                          <CheckCircle2 className="w-3 h-3 text-emerald-600" />
                          SOLVED
                        </span>
                      ) : isAttempted ? (
                        <span className="inline-flex items-center gap-1 text-[10px] font-mono font-bold text-amber-800 bg-amber-50 px-2.5 py-0.5 rounded-full border border-amber-200 shadow-2xs">
                          <Play className="w-2.5 h-2.5 fill-amber-600 text-amber-600" />
                          ATTEMPTED
                        </span>
                      ) : null}
                    </div>

                    <div className="flex items-center gap-3 text-xs font-mono text-slate-500">
                      <span className="font-semibold text-slate-700">{ch.category.replace('_', ' ')}</span>
                      {ch.tags && (
                        <>
                          <span>•</span>
                          <span className="text-slate-400 truncate max-w-[280px]">
                            {ch.tags.split(',').map((t) => `#${t.trim()}`).join(' ')}
                          </span>
                        </>
                      )}
                    </div>
                  </div>

                  {/* Right: XP, time, arrow */}
                  <div className="flex items-center justify-between sm:justify-end gap-6 shrink-0 font-mono">
                    <div className="text-right">
                      <span className="text-sm font-black text-indigo-600 flex items-center justify-end gap-1">
                        <Sparkles className="w-3.5 h-3.5 text-amber-500" />
                        +{ch.xpReward} XP
                      </span>
                      <span className="text-[11px] text-slate-400 flex items-center justify-end gap-1 mt-0.5 font-medium">
                        <Clock className="w-3 h-3" />
                        {ch.estimatedMinutes}m
                      </span>
                    </div>

                    <div className="w-9 h-9 rounded-xl bg-slate-100/90 group-hover:bg-indigo-50 group-hover:text-indigo-600 text-slate-400 flex items-center justify-center transition-all duration-200 group-hover:scale-105 group-hover:shadow-2xs">
                      <ArrowRight className="w-4 h-4 group-hover:translate-x-0.5 transition-transform" />
                    </div>
                  </div>
                </Card>
              </motion.div>
            );
          })}
        </div>
      )}
    </div>
  );
};
