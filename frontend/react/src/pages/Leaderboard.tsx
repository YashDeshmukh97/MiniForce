import { useEffect, useState } from 'react'
import { api } from '@/services/api'
import type { LeaderboardEntry } from '@/types'
import { motion } from 'framer-motion'
import { Loading } from '@/components/ui/Loading'
import { ErrorState } from '@/components/ui/ErrorState'

export default function LeaderboardPage() {
  const [entries, setEntries] = useState<LeaderboardEntry[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    api.users
      .leaderboard()
      .then(setEntries)
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

  const top3 = entries.slice(0, 3)
  const rest = entries.slice(3)

  return (
    <main className="max-w-6xl mx-auto py-12 px-4 lg:px-8">
      <header className="mb-12 text-center">
        <div className="text-sm font-semibold text-blue-400 uppercase tracking-wider mb-2">
          Global Rankings
        </div>
        <h1 className="text-4xl font-extrabold text-slate-100 tracking-tight">
          Hall of <span className="text-blue-500">Fame</span>
        </h1>
      </header>

      {top3.length === 0 ? (
        <div className="text-center text-slate-400 py-12">
          Not enough ranked coders yet.
        </div>
      ) : (
        <>
          {/* Podium */}
          <div className="flex flex-row justify-center items-end gap-2 md:gap-8 mb-16 h-64">
            {top3[1] && <PodiumPerson entry={top3[1]} place="second" />}
            {top3[0] && <PodiumPerson entry={top3[0]} place="first" />}
            {top3[2] && <PodiumPerson entry={top3[2]} place="third" />}
          </div>

          {/* Table */}
          <div className="overflow-x-auto rounded-xl border border-slate-800 bg-slate-900/50 shadow-xl backdrop-blur-sm">
            <table className="w-full text-left text-sm text-slate-300">
              <thead className="bg-slate-900 text-xs uppercase text-slate-400 border-b border-slate-800">
                <tr>
                  <th className="px-6 py-4 font-semibold w-24">Rank</th>
                  <th className="px-6 py-4 font-semibold">User</th>
                  <th className="px-6 py-4 font-semibold">Rating</th>
                  <th className="px-6 py-4 font-semibold hidden md:table-cell">
                    Solved
                  </th>
                  <th className="px-6 py-4 font-semibold text-right hidden sm:table-cell">
                    Streak
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/50">
                {rest.length === 0 ? (
                  <tr>
                    <td
                      colSpan={5}
                      className="px-6 py-8 text-center text-slate-500"
                    >
                      No additional rankings yet.
                    </td>
                  </tr>
                ) : (
                  rest.map((e) => (
                    <motion.tr
                      key={e.user.username}
                      initial={{ opacity: 0 }}
                      animate={{ opacity: 1 }}
                      className="hover:bg-slate-800/30 transition-colors"
                    >
                      <td className="px-6 py-4 font-mono text-slate-500">
                        {String(e.rank).padStart(2, '0')}
                      </td>
                      <td className="px-6 py-4">
                        <div className="flex items-center gap-3">
                          <img
                            src={`/frontend/assets/avatars/${
                              e.user.avatarKey || 'avatar_default'
                            }.svg`}
                            alt=""
                            className="w-10 h-10 rounded-full border border-slate-700 bg-slate-800"
                          />
                          <div className="flex flex-col">
                            <strong className="text-slate-200">
                              {e.user.fullName}
                            </strong>
                            <span className="text-xs text-slate-500">
                              {e.user.username}
                            </span>
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-4 font-semibold text-blue-400">
                        {e.user.rating.toLocaleString()}
                      </td>
                      <td className="px-6 py-4 hidden md:table-cell">
                        {e.user.solvedCount}
                      </td>
                      <td className="px-6 py-4 text-right hidden sm:table-cell">
                        <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md bg-orange-500/10 text-orange-400 text-xs font-medium border border-orange-500/20">
                          <span className="text-orange-500">🔥</span> {e.user.streak}{' '}
                          days
                        </span>
                      </td>
                    </motion.tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </>
      )}
    </main>
  )
}

function PodiumPerson({
  entry,
  place,
}: {
  entry: LeaderboardEntry
  place: 'first' | 'second' | 'third'
}) {
  const heights = {
    first: 'h-48 md:h-56',
    second: 'h-40 md:h-48',
    third: 'h-32 md:h-40',
  }
  const colors = {
    first: 'bg-gradient-to-t from-yellow-500/20 to-yellow-500/5 border-yellow-500/30',
    second: 'bg-gradient-to-t from-slate-400/20 to-slate-400/5 border-slate-400/30',
    third: 'bg-gradient-to-t from-orange-700/20 to-orange-700/5 border-orange-700/30',
  }
  const rankColors = {
    first: 'text-yellow-400',
    second: 'text-slate-300',
    third: 'text-orange-600',
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      className="flex flex-col items-center w-24 md:w-32"
    >
      {place === 'first' && <div className="text-3xl mb-2">👑</div>}
      <img
        src={`/frontend/assets/avatars/${
          entry.user.avatarKey || 'avatar_default'
        }.svg`}
        alt=""
        className={`w-14 h-14 md:w-16 md:h-16 rounded-full border-2 mb-3 bg-slate-800 ${
          place === 'first'
            ? 'border-yellow-500'
            : place === 'second'
            ? 'border-slate-400'
            : 'border-orange-700'
        }`}
      />
      <strong className="text-slate-200 text-sm md:text-base text-center line-clamp-1">
        {entry.user.fullName}
      </strong>
      <span className="text-xs text-slate-500 mb-3">{entry.user.username}</span>
      <div
        className={`w-full flex items-center justify-center rounded-t-xl border-t border-x ${heights[place]} ${colors[place]}`}
      >
        <span className={`text-2xl md:text-4xl font-black ${rankColors[place]}`}>
          {entry.rank}
        </span>
      </div>
    </motion.div>
  )
}
