import { useEffect, useState } from 'react'
import { api } from '@/services/api'
import type { Contest } from '@/types'
import { useAuth } from '@/context/AuthContext'
import { useNavigate, Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { Loading } from '@/components/ui/Loading'
import { ErrorState } from '@/components/ui/ErrorState'
import { Button } from '@/components/ui/Button'
import { Card } from '@/components/ui/Card'
import { Badge } from '@/components/ui/Badge'

export default function ContestsPage() {
  const [contests, setContests] = useState<Contest[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const { isAuthenticated } = useAuth()
  const navigate = useNavigate()

  useEffect(() => {
    api.contests
      .list()
      .then(setContests)
      .catch((err: Error) => setError(err.message))
      .finally(() => setIsLoading(false))
  }, [])

  if (isLoading) {
    return (
      <div className="flex justify-center py-20">
        <Loading />
      </div>
    )
  }

  if (error) {
    return (
      <div className="max-w-4xl mx-auto py-12 px-4">
        <ErrorState message={error} />
      </div>
    )
  }

  const active = contests.filter((c) => c.status !== 'ENDED')
  const past = contests.filter((c) => c.status === 'ENDED')

  const handleJoin = async (id: number) => {
    if (!isAuthenticated) {
      navigate('/login')
      return
    }
    try {
      await api.contests.join(id)
      navigate(`/contests/${id}`)
    } catch (err: unknown) {
      alert(err instanceof Error ? err.message : 'Error joining contest')
    }
  }

  return (
    <main className="max-w-5xl mx-auto py-12 px-4 lg:px-8">
      <header className="mb-12">
        <div className="text-sm font-semibold text-indigo-400 uppercase tracking-wider mb-2">
          Compete
        </div>
        <h1 className="text-4xl font-extrabold text-slate-100 tracking-tight">
          Coding <span className="text-indigo-400">Contests</span>
        </h1>
        <p className="mt-4 text-slate-400 max-w-2xl text-lg">
          Test your skills against the community. Join live contests or practice
          with past problems to improve your rating.
        </p>
      </header>

      <div className="space-y-16">
        <section>
          <h2 className="text-2xl font-bold text-slate-200 mb-6 flex items-center gap-2">
            Active & Upcoming
          </h2>
          {active.length === 0 ? (
            <div className="p-8 text-center text-slate-500 bg-slate-900/30 rounded-xl border border-slate-800/50">
              No upcoming or live contests.
            </div>
          ) : (
            <div className="grid gap-4 md:grid-cols-2">
              {active.map((c) => (
                <ContestCard
                  key={c.id}
                  contest={c}
                  onJoin={() => handleJoin(c.id)}
                />
              ))}
            </div>
          )}
        </section>

        <section>
          <h2 className="text-xl font-bold text-slate-200 mb-6">Past Contests</h2>
          <Card className="overflow-hidden">
            <div className="divide-y divide-slate-800/50">
              {past.length === 0 ? (
                <div className="p-6 text-center text-slate-500">
                  No finished contests.
                </div>
              ) : (
                past.map((c) => (
                  <div
                    key={c.id}
                    className="p-4 flex items-center justify-between hover:bg-slate-800/30 transition-colors"
                  >
                    <div>
                      <Link
                        to={`/contests/${c.id}`}
                        className="text-slate-200 font-semibold hover:text-indigo-400 transition-colors"
                      >
                        {c.title}
                      </Link>
                    </div>
                    <span className="text-sm text-slate-500 font-medium px-3 py-1 bg-slate-800/50 rounded-full">
                      Ended
                    </span>
                  </div>
                ))
              )}
            </div>
          </Card>
        </section>
      </div>
    </main>
  )
}

function ContestCard({
  contest,
  onJoin,
}: {
  contest: Contest
  onJoin: () => void
}) {
  const d = new Date(contest.startsAt)
  const isLive = contest.status === 'LIVE'

  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      whileHover={{ scale: 1.01 }}
      className={`group relative overflow-hidden rounded-xl border p-6 flex flex-col sm:flex-row sm:items-center gap-6 ${
        isLive
          ? 'bg-indigo-950/20 border-indigo-500/30 shadow-[0_0_15px_rgba(99,102,241,0.1)]'
          : 'bg-slate-900/50 border-slate-800'
      }`}
    >
      <div className="flex-shrink-0 w-16 h-16 rounded-xl border border-slate-700 bg-slate-800 flex flex-col items-center justify-center">
        <span className="text-xs font-bold text-slate-400 uppercase tracking-wider">
          {d.toLocaleString(undefined, { month: 'short' })}
        </span>
        <span className="text-xl font-black text-slate-200">{d.getDate()}</span>
      </div>

      <div className="flex-1 min-w-0">
        <h3 className="text-lg font-bold text-slate-100 truncate mb-1">
          <Link
            to={`/contests/${contest.id}`}
            className="hover:text-indigo-400 focus:outline-none focus:underline"
          >
            {contest.title}
          </Link>
        </h3>
        <div className="flex flex-wrap items-center gap-2 text-sm">
          <Badge variant={contest.rated ? 'default' : 'default'}>
            {contest.rated ? '★ Rated' : 'Practice'}
          </Badge>
          <Badge
            variant={isLive ? 'success' : 'default'}
            className={isLive ? 'animate-pulse' : ''}
          >
            {contest.status}
          </Badge>
          <span className="text-slate-400">
            {d.toLocaleTimeString(undefined, {
              hour: 'numeric',
              minute: '2-digit',
            })}
          </span>
        </div>
      </div>

      <div className="flex-shrink-0 mt-4 sm:mt-0">
        <Button onClick={onJoin} variant={isLive ? 'primary' : 'secondary'}>
          {isLive ? 'Join / Enter' : 'Register'}
        </Button>
      </div>
    </motion.div>
  )
}
