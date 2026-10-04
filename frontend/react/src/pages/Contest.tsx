import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { api } from '@/services/api'
import type { Contest, ContestProblem, ContestLeaderboardEntry } from '@/types'
import { motion } from 'framer-motion'
import { Loading } from '@/components/ui/Loading'
import { ErrorState } from '@/components/ui/ErrorState'
import { Card } from '@/components/ui/Card'
import { Badge } from '@/components/ui/Badge'
import { Button } from '@/components/ui/Button'

export default function ContestPage() {
  const { id } = useParams<{ id: string }>()
  const contestId = Number(id)

  const [contest, setContest] = useState<Contest | null>(null)
  const [problems, setProblems] = useState<ContestProblem[]>([])
  const [leaderboard, setLeaderboard] = useState<ContestLeaderboardEntry[]>([])
  
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [problemsError, setProblemsError] = useState<string | null>(null)
  const [leaderboardError, setLeaderboardError] = useState<string | null>(null)

  useEffect(() => {
    if (!contestId) return

    setIsLoading(true)
    setError(null)
    setProblemsError(null)
    setLeaderboardError(null)

    // Fetch contest metadata
    api.contests
      .get(contestId)
      .then((c) => {
        setContest(c)
        // Fetch problems and leaderboard in parallel after we get the contest
        return Promise.allSettled([
          api.contests.problems(contestId),
          api.contests.leaderboard(contestId),
        ])
      })
      .then(([probsRes, lbRes]) => {
        if (probsRes?.status === 'fulfilled') {
          setProblems(probsRes.value)
        } else if (probsRes?.status === 'rejected') {
          setProblemsError(probsRes.reason.message)
        }

        if (lbRes?.status === 'fulfilled') {
          setLeaderboard(lbRes.value)
        } else if (lbRes?.status === 'rejected') {
          setLeaderboardError(lbRes.reason.message)
        }
      })
      .catch((err: Error) => setError(err.message))
      .finally(() => setIsLoading(false))
  }, [contestId])

  if (isLoading) {
    return (
      <div className="flex justify-center py-20">
        <Loading />
      </div>
    )
  }

  if (error || !contest) {
    return (
      <div className="max-w-4xl mx-auto py-12 px-4">
        <ErrorState message={error || 'Contest not found'} />
      </div>
    )
  }

  return (
    <main className="max-w-6xl mx-auto py-12 px-4 lg:px-8">
      <header className="mb-10">
        <Link
          to="/contests"
          className="text-slate-400 hover:text-blue-400 text-sm font-medium transition-colors mb-4 inline-block"
        >
          ← All Contests
        </Link>
        <h1 className="text-4xl font-extrabold text-slate-100 tracking-tight mb-3">
          {contest.title}
        </h1>
        <p className="text-lg text-slate-400 max-w-3xl">
          {contest.description || (contest.rated ? 'Rated' : 'Practice')}
        </p>
      </header>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Main Content (Problems) */}
        <div className="lg:col-span-2 space-y-6">
          <h2 className="text-2xl font-bold text-slate-200">Problems</h2>
          <Card className="overflow-hidden">
            {problemsError ? (
              <div className="p-6 text-red-400 bg-red-900/10 border-b border-red-900/20">
                {problemsError}
              </div>
            ) : null}
            
            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm text-slate-300">
                <thead className="bg-slate-900 text-xs uppercase text-slate-400 border-b border-slate-800">
                  <tr>
                    <th className="px-6 py-4 font-semibold">Title</th>
                    <th className="px-6 py-4 font-semibold">Difficulty</th>
                    <th className="px-6 py-4 font-semibold text-right">Points</th>
                    <th className="px-6 py-4 font-semibold text-right">Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800/50">
                  {problems.length === 0 ? (
                    <tr>
                      <td colSpan={4} className="px-6 py-8 text-center text-slate-500">
                        No problems listed for this contest.
                      </td>
                    </tr>
                  ) : (
                    problems.map((p) => (
                      <motion.tr
                        key={p.id}
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        className="hover:bg-slate-800/30 transition-colors"
                      >
                        <td className="px-6 py-4">
                          <Link
                            to={`/solve/${encodeURIComponent(p.slug)}?contest=${contestId}`}
                            className="font-bold text-slate-200 hover:text-blue-400 transition-colors focus:outline-none focus:underline"
                          >
                            {p.title}
                          </Link>
                        </td>
                        <td className="px-6 py-4">
                          <Badge
                            variant={
                              p.difficulty === 'EASY'
                                ? 'easy'
                                : p.difficulty === 'MEDIUM'
                                ? 'medium'
                                : 'hard'
                            }
                          >
                            {p.difficulty}
                          </Badge>
                        </td>
                        <td className="px-6 py-4 text-right font-mono text-slate-400">
                          {p.points}
                        </td>
                        <td className="px-6 py-4 text-right">
                          <Button
                            variant="secondary"
                            className="h-8 text-xs px-3"
                            onClick={() =>
                              window.location.assign(
                                `/solve/${encodeURIComponent(p.slug)}?contest=${contestId}`
                              )
                            }
                          >
                            Solve
                          </Button>
                        </td>
                      </motion.tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </Card>
        </div>

        {/* Sidebar (Leaderboard) */}
        <aside className="space-y-6">
          <h2 className="text-2xl font-bold text-slate-200">Leaderboard</h2>
          <Card className="overflow-hidden">
            {leaderboardError ? (
              <div className="p-6 text-red-400 bg-red-900/10 border-b border-red-900/20">
                {leaderboardError}
              </div>
            ) : null}

            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm text-slate-300">
                <thead className="bg-slate-900 text-xs uppercase text-slate-400 border-b border-slate-800">
                  <tr>
                    <th className="px-4 py-3 font-semibold">Rank</th>
                    <th className="px-4 py-3 font-semibold">User</th>
                    <th className="px-4 py-3 font-semibold text-right">Score</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800/50">
                  {leaderboard.length === 0 ? (
                    <tr>
                      <td colSpan={3} className="px-4 py-6 text-center text-slate-500">
                        No participants yet.
                      </td>
                    </tr>
                  ) : (
                    leaderboard.map((r, i) => (
                      <tr
                        key={r.user.id}
                        className="hover:bg-slate-800/30 transition-colors"
                      >
                        <td className="px-4 py-3 font-mono text-slate-500">
                          #{i + 1}
                        </td>
                        <td className="px-4 py-3">
                          <Link
                            to={`/profile?user=${encodeURIComponent(r.user.username)}`}
                            className="font-medium text-slate-200 hover:text-blue-400 transition-colors"
                          >
                            {r.user.username}
                          </Link>
                        </td>
                        <td className="px-4 py-3 text-right font-semibold text-blue-400">
                          {r.score}
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </Card>
        </aside>
      </div>
    </main>
  )
}
