import { Button } from '@/components/ui'
import { useAuth } from '@/context/AuthContext'
import { motion } from 'framer-motion'
import { Code2, LineChart, MessageSquare, Terminal, Trophy } from 'lucide-react'
import { Link } from 'react-router-dom'

const FEATURES = [
  {
    icon: <Code2 className="h-6 w-6 text-orange-400" />,
    title: 'Practice Problems',
    desc: 'Curated algorithmic challenges sorted by difficulty and topic.',
  },
  {
    icon: <Trophy className="h-6 w-6 text-blue-400" />,
    title: 'Live Contests',
    desc: 'Compete in time-bound contests with a real-time leaderboard.',
  },
  {
    icon: <LineChart className="h-6 w-6 text-emerald-400" />,
    title: 'Contribution Streaks',
    desc: 'Track your daily coding activity on a visual contribution graph.',
  },
  {
    icon: <MessageSquare className="h-6 w-6 text-rose-400" />,
    title: 'Community',
    desc: 'Discuss solutions, ask questions, and learn from others.',
  },
]

const STEPS = [
  {
    num: '1',
    title: 'Pick a Problem',
    desc: 'Choose from our library of arrays, strings, dynamic programming, and more.',
  },
  {
    num: '2',
    title: 'Write Code',
    desc: 'Use the integrated editor to test against sample cases and submit your solution.',
  },
  {
    num: '3',
    title: 'Track Progress',
    desc: 'Watch your streak grow and your global rating improve as you solve consistently.',
  },
]

export default function HomePage() {
  const { isAuthenticated } = useAuth()

  return (
    <div className="flex flex-col gap-24 py-10 md:py-16">
      {/* ── Hero ───────────────────────────────────────────────────────────── */}
      <section className="mx-auto flex max-w-4xl flex-col items-center text-center">
        <motion.div
          initial={{ opacity: 0, y: 15 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4 }}
          className="mb-6 inline-flex items-center gap-2 rounded-full border border-indigo-500/30 bg-indigo-500/10 px-3 py-1 text-sm font-medium text-indigo-300"
        >
          <Terminal className="h-4 w-4" />
          Ready for your next challenge?
        </motion.div>

        <motion.h1
          initial={{ opacity: 0, y: 15 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4, delay: 0.1 }}
          className="text-5xl font-extrabold tracking-tight text-white sm:text-6xl md:text-7xl"
        >
          Code. Compete. <br className="hidden sm:block" />
          <span className="text-transparent bg-clip-text bg-gradient-to-r from-indigo-400 to-cyan-400">
            Grow.
          </span>
        </motion.h1>

        <motion.p
          initial={{ opacity: 0, y: 15 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4, delay: 0.2 }}
          className="mt-6 max-w-2xl text-lg text-slate-400"
        >
          MiniForce brings problem-solving, competitive contests, and profile contributions together in one place. Practice your algorithms, compete in rated contests, and build your coding streak.
        </motion.p>

        <motion.div
          initial={{ opacity: 0, y: 15 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4, delay: 0.3 }}
          className="mt-10 flex flex-wrap items-center justify-center gap-4"
        >
          {isAuthenticated ? (
            <Link to="/problems">
              <Button size="lg" className="h-12 px-8 text-base">
                Go to Problems
              </Button>
            </Link>
          ) : (
            <Link to="/register">
              <Button size="lg" className="h-12 px-8 text-base">
                Start Coding Free
              </Button>
            </Link>
          )}
          <Link to="/contests">
            <Button variant="secondary" size="lg" className="h-12 px-8 text-base bg-slate-800 border border-slate-700">
              View Contests
            </Button>
          </Link>
        </motion.div>
      </section>

      {/* ── Features ───────────────────────────────────────────────────────── */}
      <section className="mx-auto w-full max-w-5xl">
        <div className="mb-12 text-center">
          <h2 className="text-3xl font-bold text-slate-100">Everything you need to level up</h2>
          <p className="mt-3 text-slate-400">One platform, inspired by the best coding communities</p>
        </div>

        <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
          {FEATURES.map((f, i) => (
            <motion.div
              key={f.title}
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true, margin: '-50px' }}
              transition={{ duration: 0.4, delay: i * 0.1 }}
              className="flex flex-col gap-4 rounded-xl border border-slate-800 bg-slate-900/50 p-6 transition-colors hover:border-slate-700 hover:bg-slate-900"
            >
              <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-slate-950 border border-slate-800">
                {f.icon}
              </div>
              <h3 className="text-lg font-semibold text-slate-200">{f.title}</h3>
              <p className="text-sm text-slate-400 leading-relaxed">{f.desc}</p>
            </motion.div>
          ))}
        </div>
      </section>

      {/* ── Steps ──────────────────────────────────────────────────────────── */}
      <section className="mx-auto w-full max-w-5xl rounded-2xl border border-slate-800 bg-slate-900/30 p-8 md:p-12">
        <div className="mb-10 md:text-center">
          <h2 className="text-3xl font-bold text-slate-100">How it works</h2>
          <p className="mt-3 text-slate-400">A simple path from practice to mastery</p>
        </div>

        <div className="grid gap-8 md:grid-cols-3 md:gap-12">
          {STEPS.map((s, i) => (
            <div key={s.num} className="relative flex flex-col gap-3">
              <div className="mb-2 flex h-10 w-10 items-center justify-center rounded-full bg-indigo-500/20 font-bold text-indigo-400 border border-indigo-500/30">
                {s.num}
              </div>
              <h3 className="text-xl font-semibold text-slate-200">{s.title}</h3>
              <p className="text-sm text-slate-400 leading-relaxed">{s.desc}</p>
              
              {/* Connector line for desktop */}
              {i < STEPS.length - 1 && (
                <div className="hidden md:block absolute top-5 left-16 h-[1px] w-[calc(100%-4rem)] bg-gradient-to-r from-indigo-500/30 to-transparent" />
              )}
            </div>
          ))}
        </div>
      </section>

      {/* ── CTA ────────────────────────────────────────────────────────────── */}
      {!isAuthenticated && (
        <section className="mx-auto w-full max-w-3xl text-center py-10">
          <h2 className="text-3xl font-bold text-white mb-4">Ready to start your streak?</h2>
          <p className="text-lg text-slate-400 mb-8">Join MiniForce and turn practice into progress.</p>
          <Link to="/register">
            <Button size="lg" className="px-10 py-3 text-base h-auto rounded-full">
              Create Free Account
            </Button>
          </Link>
        </section>
      )}
    </div>
  )
}
