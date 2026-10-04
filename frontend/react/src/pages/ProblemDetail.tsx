import { Badge, Button, ErrorState, Loading } from '@/components/ui'
import { useApi } from '@/hooks/useApi'
import { api } from '@/services/api'
import { motion } from 'framer-motion'
import { ArrowLeft, CheckCircle2, Code2, Copy, Terminal } from 'lucide-react'
import { Link, useParams } from 'react-router-dom'
import { useState } from 'react'

export default function ProblemDetailPage() {
  const { id } = useParams()
  const { data: problem, isLoading, error, refetch } = useApi(() => api.problems.get(Number(id)), [id])
  const [copied, setCopied] = useState(false)

  if (isLoading) return <Loading message="Loading problem details..." className="mt-20" />
  
  if (error || !problem) return (
    <div className="mx-auto max-w-4xl py-12">
      <ErrorState message={error || 'Problem not found'} onRetry={refetch} />
      <div className="mt-6 text-center">
        <Link to="/problems" className="text-sm text-indigo-400 hover:text-indigo-300">
          ← Back to Problems
        </Link>
      </div>
    </div>
  )

  const copyStarter = () => {
    if (problem.starterCode) {
      navigator.clipboard.writeText(problem.starterCode)
      setCopied(true)
      setTimeout(() => setCopied(false), 2000)
    }
  }

  return (
    <div className="mx-auto max-w-5xl flex flex-col gap-8 pb-12">
      {/* ── Header ─────────────────────────────────────────────────────────── */}
      <motion.div 
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex flex-col gap-4 border-b border-slate-800 pb-8 pt-4"
      >
        <Link to="/problems" className="inline-flex items-center gap-2 text-sm font-medium text-slate-400 hover:text-white self-start transition-colors">
          <ArrowLeft className="h-4 w-4" />
          Back to Catalog
        </Link>
        
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-6 mt-2">
          <div>
            <h1 className="text-3xl font-bold text-white tracking-tight">
              {problem.id}. {problem.title}
            </h1>
            <div className="mt-4 flex flex-wrap items-center gap-3">
              <Badge variant={problem.difficulty.toLowerCase() as any} className="text-sm px-3 py-1">
                {problem.difficulty}
              </Badge>
              <div className="h-4 w-[1px] bg-slate-800" />
              <span className="text-sm text-slate-400 font-medium">
                Acceptance: {Number(problem.acceptanceRate || 0).toFixed(1)}%
              </span>
              <div className="h-4 w-[1px] bg-slate-800" />
              <div className="flex gap-1.5">
                {problem.topics?.map(t => (
                  <span key={t} className="text-xs bg-slate-800/80 text-slate-400 px-2 py-0.5 rounded-full">
                    {t}
                  </span>
                ))}
              </div>
            </div>
          </div>
          
          {/* Solve button routes to the existing solve page slug (which will be migrated later) */}
          <Link to={`/solve/${encodeURIComponent(problem.slug)}`}>
            <Button size="lg" className="px-8 shadow-lg shadow-indigo-900/20">
              <Code2 className="h-5 w-5 mr-1" />
              Solve Problem
            </Button>
          </Link>
        </div>
      </motion.div>

      {/* ── Content ────────────────────────────────────────────────────────── */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        {/* Left Col: Description & Constraints */}
        <motion.div 
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="lg:col-span-2 flex flex-col gap-8"
        >
          <section className="prose prose-invert max-w-none prose-pre:bg-slate-900 prose-pre:border prose-pre:border-slate-800 prose-headings:font-semibold">
            <h2 className="text-xl text-slate-100 font-semibold mb-4 border-b border-slate-800/50 pb-2">Description</h2>
            {/* Split description safely if there are linebreaks, or render normally. 
                Using whitespace-pre-wrap to respect basic formatting from DB */}
            <div className="text-slate-300 leading-relaxed whitespace-pre-wrap font-sans">
              {problem.description}
            </div>
          </section>

          {problem.constraintsText && (
            <section>
              <h2 className="text-xl text-slate-100 font-semibold mb-4 border-b border-slate-800/50 pb-2">Constraints</h2>
              <div className="bg-amber-500/5 border border-amber-500/10 rounded-xl p-5">
                <div className="text-amber-200/80 font-mono text-sm whitespace-pre-wrap leading-relaxed">
                  {problem.constraintsText}
                </div>
              </div>
            </section>
          )}
        </motion.div>

        {/* Right Col: Code Snippets & Metadata */}
        <motion.div 
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
          className="flex flex-col gap-6"
        >
          <div className="rounded-xl border border-slate-800 bg-slate-900/50 overflow-hidden flex flex-col">
            <div className="flex items-center justify-between bg-slate-900 px-4 py-3 border-b border-slate-800">
              <div className="flex items-center gap-2 text-sm font-medium text-slate-300">
                <Terminal className="h-4 w-4" />
                Java Starter Code
              </div>
              <button 
                onClick={copyStarter}
                className="text-slate-500 hover:text-slate-300 transition-colors"
                title="Copy code"
              >
                {copied ? <CheckCircle2 className="h-4 w-4 text-emerald-400" /> : <Copy className="h-4 w-4" />}
              </button>
            </div>
            <div className="p-4 overflow-x-auto bg-[#0d1117]">
              <pre className="text-sm text-slate-300 font-mono leading-relaxed">
                <code>{problem.starterCode || '// No starter code provided'}</code>
              </pre>
            </div>
          </div>
          
          {problem.starterCodePython && (
            <div className="rounded-xl border border-slate-800 bg-slate-900/50 overflow-hidden flex flex-col">
              <div className="flex items-center justify-between bg-slate-900 px-4 py-3 border-b border-slate-800">
                <div className="flex items-center gap-2 text-sm font-medium text-slate-300">
                  <Terminal className="h-4 w-4 text-yellow-500" />
                  Python 3 Starter
                </div>
              </div>
              <div className="p-4 overflow-x-auto bg-[#0d1117]">
                <pre className="text-sm text-slate-300 font-mono leading-relaxed">
                  <code>{problem.starterCodePython}</code>
                </pre>
              </div>
            </div>
          )}
        </motion.div>

      </div>
    </div>
  )
}
