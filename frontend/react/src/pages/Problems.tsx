import { Badge, ErrorState, Input, Loading } from '@/components/ui'
import { useApi } from '@/hooks/useApi'
import { api } from '@/services/api'
import type { Difficulty } from '@/types'
import { CheckCircle2, Search } from 'lucide-react'
import { useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'

export default function ProblemsPage() {
  const { data: allProblems, isLoading, error, refetch } = useApi(() => api.problems.list({}))

  const [search, setSearch] = useState('')
  const [selectedDifficulty, setSelectedDifficulty] = useState<Difficulty | 'ALL'>('ALL')
  const [selectedTopic, setSelectedTopic] = useState<string>('ALL')

  // Extract unique topics
  const topics = useMemo(() => {
    if (!allProblems) return []
    const set = new Set<string>()
    allProblems.forEach((p) => p.topics?.forEach((t) => set.add(t)))
    return Array.from(set).sort()
  }, [allProblems])

  // Filter and sort
  const filteredProblems = useMemo(() => {
    if (!allProblems) return []

    let result = [...allProblems]

    // Sort by problem ID ascending (1, 2, 3, ... 50)
    result.sort((a, b) => a.id - b.id)

    // Filter by search
    if (search.trim()) {
      const q = search.toLowerCase()
      result = result.filter(
        (p) => p.title.toLowerCase().includes(q) || String(p.id) === q,
      )
    }

    // Filter by difficulty
    if (selectedDifficulty !== 'ALL') {
      result = result.filter((p) => p.difficulty === selectedDifficulty)
    }

    // Filter by topic
    if (selectedTopic !== 'ALL') {
      result = result.filter((p) => p.topics?.includes(selectedTopic))
    }

    return result
  }, [allProblems, search, selectedDifficulty, selectedTopic])

  if (isLoading) return <Loading message="Loading challenges..." className="mt-20" />
  
  if (error) return (
    <div className="mx-auto max-w-4xl py-12">
      <ErrorState message={error} onRetry={refetch} />
    </div>
  )

  return (
    <div className="flex flex-col gap-6">
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-white tracking-tight">Problem Catalog</h1>
          <p className="mt-2 text-slate-400">Master algorithms and data structures.</p>
        </div>
        <div className="flex items-center gap-2">
          <Badge className="bg-indigo-500/10 text-indigo-400 border border-indigo-500/20 text-xs px-3 py-1 font-medium">
            50 Problems · Java 17 + Python 3
          </Badge>
        </div>
      </div>

      {/* ── Filters ────────────────────────────────────────────────────────── */}
      <div className="flex flex-col sm:flex-row gap-4 p-4 rounded-xl bg-slate-900 border border-slate-800">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-500" />
          <Input
            placeholder="Search problems..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="pl-9 w-full"
            aria-label="Search problems"
          />
        </div>
        
        <select
          value={selectedDifficulty}
          onChange={(e) => setSelectedDifficulty(e.target.value as any)}
          className="rounded-md border border-slate-700 bg-slate-950 px-3 py-2 text-sm text-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          aria-label="Filter by difficulty"
        >
          <option value="ALL">All Difficulties</option>
          <option value="EASY">Easy</option>
          <option value="MEDIUM">Medium</option>
          <option value="HARD">Hard</option>
        </select>

        <select
          value={selectedTopic}
          onChange={(e) => setSelectedTopic(e.target.value)}
          className="rounded-md border border-slate-700 bg-slate-950 px-3 py-2 text-sm text-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          aria-label="Filter by topic"
        >
          <option value="ALL">All Topics</option>
          {topics.map((t) => (
            <option key={t} value={t}>{t}</option>
          ))}
        </select>
      </div>

      {/* ── Table ──────────────────────────────────────────────────────────── */}
      <div className="rounded-xl border border-slate-800 bg-slate-900/50 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm whitespace-nowrap">
            <thead className="bg-slate-900 text-slate-400 border-b border-slate-800">
              <tr>
                <th className="px-6 py-4 font-medium w-12 text-center">Status</th>
                <th className="px-6 py-4 font-medium">Title</th>
                <th className="px-6 py-4 font-medium">Topics</th>
                <th className="px-6 py-4 font-medium">Acceptance</th>
                <th className="px-6 py-4 font-medium">Difficulty</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/50">
              {filteredProblems.length === 0 ? (
                <tr>
                  <td colSpan={5} className="px-6 py-12 text-center text-slate-500">
                    No problems match your filters.
                  </td>
                </tr>
              ) : (
                filteredProblems.map((p, i) => (
                  <motion.tr 
                    key={p.id}
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.2, delay: Math.min(i * 0.05, 0.5) }}
                    className="group hover:bg-slate-800/50 transition-colors"
                  >
                    <td className="px-6 py-4 text-center">
                      {p.solved ? (
                        <CheckCircle2 className="h-5 w-5 mx-auto text-emerald-500" />
                      ) : (
                        <CheckCircle2 className="h-5 w-5 mx-auto text-emerald-500 opacity-0 group-hover:opacity-10 transition-opacity" />
                      )}
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-3">
                        <Link 
                          to={`/problems/${p.id}`} 
                          className="font-medium text-slate-200 hover:text-indigo-400 transition-colors"
                        >
                          {p.id}. {p.title}
                        </Link>
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex flex-wrap gap-1.5">
                        {p.topics?.slice(0, 3).map((t) => (
                          <span key={t} className="inline-flex rounded-full bg-slate-800 px-2 py-0.5 text-[11px] font-medium text-slate-400">
                            {t}
                          </span>
                        ))}
                        {(p.topics?.length ?? 0) > 3 && (
                          <span className="inline-flex rounded-full bg-slate-800 px-2 py-0.5 text-[11px] font-medium text-slate-500">
                            +{(p.topics?.length ?? 0) - 3}
                          </span>
                        )}
                      </div>
                    </td>
                    <td className="px-6 py-4 text-slate-400 font-mono text-xs">
                      {p.acceptanceRate && p.acceptanceRate > 0
                        ? `${Number(p.acceptanceRate).toFixed(1)}%`
                        : '—'}
                    </td>
                    <td className="px-6 py-4">
                      <Badge variant={p.difficulty.toLowerCase() as any}>
                        {p.difficulty}
                      </Badge>
                    </td>
                  </motion.tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
