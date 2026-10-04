import { Card } from '@/components/ui'
import { motion } from 'framer-motion'
import { BookOpen, CheckCircle, Code2, LineChart, Mail, Phone } from 'lucide-react'

const FEATURES = [
  {
    icon: <Code2 className="h-5 w-5 text-indigo-400" />,
    title: 'Practice',
    desc: 'Solve structured programming problems across different topics and difficulties. Build your foundation one problem at a time.',
  },
  {
    icon: <LineChart className="h-5 w-5 text-indigo-400" />,
    title: 'Contests',
    desc: 'Participate in competitive programming contests and track your performance under time pressure against other developers.',
  },
  {
    icon: <CheckCircle className="h-5 w-5 text-indigo-400" />,
    title: 'Real-time Judging',
    desc: 'Submit solutions and receive immediate verdicts through our Docker-based judging system, which securely runs and validates your code.',
  },
  {
    icon: <BookOpen className="h-5 w-5 text-indigo-400" />,
    title: 'Progress Tracking',
    desc: 'Track your solved problems, daily streaks, submission history, and global rating to visualize your growth over time.',
  },
]

const TECH_STACK = [
  'React 19', 'TypeScript', 'Tailwind CSS', 'Vite', 'Java 26', 'Spring Boot 3.5', 'MySQL', 'JWT', 'Docker'
]

export default function AboutPage() {
  return (
    <div className="mx-auto max-w-4xl flex flex-col gap-12 pb-16 pt-8">
      {/* ── Hero ───────────────────────────────────────────────────────────── */}
      <section className="text-center">
        <motion.div
          initial={{ opacity: 0, y: 15 }}
          animate={{ opacity: 1, y: 0 }}
          className="inline-block mb-3 px-3 py-1 rounded-full border border-indigo-500/20 bg-indigo-500/10 text-xs font-semibold text-indigo-400 uppercase tracking-widest"
        >
          Practice. Compete. Improve.
        </motion.div>
        
        <motion.h1
          initial={{ opacity: 0, y: 15 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="text-4xl md:text-5xl font-extrabold text-white tracking-tight"
        >
          MiniForce
        </motion.h1>
        
        <motion.p
          initial={{ opacity: 0, y: 15 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
          className="mt-6 text-lg text-slate-400 max-w-2xl mx-auto leading-relaxed"
        >
          MiniForce is a competitive programming platform designed to help developers build algorithmic problem-solving skills through practice, contests, and community interaction.
        </motion.p>
      </section>

      {/* ── What is MiniForce? ───────────────────────────────────────────── */}
      <motion.section
        initial={{ opacity: 0, y: 15 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true, margin: '-50px' }}
      >
        <h2 className="text-2xl font-bold text-slate-100 mb-4 border-b border-slate-800 pb-3">
          What is MiniForce?
        </h2>
        <p className="text-slate-300 leading-relaxed mb-8">
          MiniForce provides a structured environment for developers to sharpen their Data Structures and Algorithms (DSA) skills. Whether you are preparing for technical interviews or simply aiming to improve your logical thinking, the platform offers a focused, distraction-free environment to write, test, and submit code.
        </p>

        <div className="grid sm:grid-cols-2 gap-6">
          {FEATURES.map((f, i) => (
            <Card key={i} className="flex flex-col gap-3 hover:border-slate-700 transition-colors bg-slate-900/50">
              <div className="h-10 w-10 flex items-center justify-center rounded-lg bg-slate-950 border border-slate-800">
                {f.icon}
              </div>
              <h3 className="text-lg font-semibold text-slate-200">{f.title}</h3>
              <p className="text-sm text-slate-400 leading-relaxed">{f.desc}</p>
            </Card>
          ))}
        </div>
      </motion.section>

      {/* ── Tech Stack ─────────────────────────────────────────────────────── */}
      <motion.section
        initial={{ opacity: 0, y: 15 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true, margin: '-50px' }}
      >
        <h2 className="text-2xl font-bold text-slate-100 mb-4 border-b border-slate-800 pb-3">
          Technology Stack
        </h2>
        <p className="text-slate-300 leading-relaxed mb-6">
          MiniForce is built using modern, reliable technologies optimized for stability, performance, and security.
        </p>
        
        <div className="flex flex-wrap gap-3">
          {TECH_STACK.map(tech => (
            <div key={tech} className="px-4 py-2 rounded-md border border-slate-800 bg-slate-900/80 text-sm font-medium text-slate-300 shadow-sm">
              {tech}
            </div>
          ))}
        </div>
      </motion.section>

      {/* ── Contact ────────────────────────────────────────────────────────── */}
      <motion.section
        initial={{ opacity: 0, y: 15 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true, margin: '-50px' }}
      >
        <h2 className="text-2xl font-bold text-slate-100 mb-4 border-b border-slate-800 pb-3">
          Contact
        </h2>
        <p className="text-slate-300 leading-relaxed mb-6">
          If you have any questions, feedback, or require support, please feel free to reach out.
        </p>

        <div className="flex flex-col sm:flex-row gap-6">
          <a href="tel:9967094876" className="flex items-center gap-4 p-4 rounded-xl border border-slate-800 bg-slate-900/50 hover:bg-slate-800 hover:border-slate-700 transition-colors group">
            <div className="h-12 w-12 rounded-full bg-slate-950 border border-slate-800 flex items-center justify-center group-hover:border-indigo-500/50 transition-colors">
              <Phone className="h-5 w-5 text-slate-400 group-hover:text-indigo-400 transition-colors" />
            </div>
            <div className="flex flex-col">
              <span className="text-sm font-medium text-slate-400">Phone</span>
              <span className="text-slate-200 font-semibold group-hover:text-white transition-colors">9967094876</span>
            </div>
          </a>

          <a href="mailto:deshmukh.yash365@gmail.com" className="flex items-center gap-4 p-4 rounded-xl border border-slate-800 bg-slate-900/50 hover:bg-slate-800 hover:border-slate-700 transition-colors group">
            <div className="h-12 w-12 rounded-full bg-slate-950 border border-slate-800 flex items-center justify-center group-hover:border-indigo-500/50 transition-colors">
              <Mail className="h-5 w-5 text-slate-400 group-hover:text-indigo-400 transition-colors" />
            </div>
            <div className="flex flex-col">
              <span className="text-sm font-medium text-slate-400">Email</span>
              <span className="text-slate-200 font-semibold group-hover:text-white transition-colors">deshmukh.yash365@gmail.com</span>
            </div>
          </a>
        </div>
      </motion.section>
    </div>
  )
}
