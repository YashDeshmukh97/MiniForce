import { Badge, Button, ErrorState, Loading } from '@/components/ui'
import { api, ApiError } from '@/services/api'
import type { Language, ProblemDetail, Submission, TestCase } from '@/types'
import { storage } from '@/utils/storage'
import {
  AlertTriangle,
  CheckCircle,
  ChevronLeft,
  Clock,
  Code2,
  FileCode,
  Play,
  TerminalSquare,
  XCircle,
} from 'lucide-react'
import { useEffect, useRef, useState } from 'react'
import { Link, useParams } from 'react-router-dom'

function formatTime(seconds: number) {
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
}

export default function SolvePage() {
  const { slug } = useParams<{ slug: string }>()

  // Problem data
  const [problem, setProblem] = useState<ProblemDetail | null>(null)
  const [testcases, setTestcases] = useState<TestCase[]>([])
  const [isLoadingProblem, setIsLoadingProblem] = useState(true)
  const [errorProblem, setErrorProblem] = useState<string | null>(null)

  // Editor state
  const [language, setLanguage] = useState<Language>('JAVA_17')
  const [code, setCode] = useState('')
  const [timeSpent, setTimeSpent] = useState(0)

  // Submission state
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [isRun, setIsRun] = useState(false)
  const [pollMessage, setPollMessage] = useState('')
  const [result, setResult] = useState<Submission | null>(null)
  const [errorResult, setErrorResult] = useState<string | null>(null)

  const pollingRef = useRef(false)

  // Fetch problem details and test cases
  useEffect(() => {
    if (!slug) return
    let cancelled = false
    setIsLoadingProblem(true)
    setErrorProblem(null)

    const initData = async () => {
      try {
        const prob = await api.problems.getBySlug(slug)
        if (cancelled) return
        setProblem(prob)
        
        try {
          const tcs = await api.problems.tests(prob.id)
          if (!cancelled) setTestcases(tcs || [])
        } catch (e) {
          // ignore test case error, display what we have
        }

        // Initialize editor with Java draft or starter code
        const draft = storage.getJavaDraft(prob.id)
        setCode(draft || prob.starterCode || '')
      } catch (err) {
        if (!cancelled) {
          setErrorProblem(err instanceof ApiError ? err.message : 'Failed to load problem')
        }
      } finally {
        if (!cancelled) setIsLoadingProblem(false)
      }
    }

    initData()
    return () => {
      cancelled = true
    }
  }, [slug])

  // Timer
  useEffect(() => {
    const started = Date.now()
    const interval = setInterval(() => {
      setTimeSpent(Math.floor((Date.now() - started) / 1000))
    }, 1000)
    return () => clearInterval(interval)
  }, [])

  // Auto-save draft on code change
  useEffect(() => {
    if (!problem || !code) return
    // We only save if it's an active typing session (not when disabled for unsupported python)
    if (language === 'PYTHON_3' && !problem.starterCodePython) return

    if (language === 'PYTHON_3') {
      storage.setPythonDraft(problem.id, code)
    } else {
      storage.setJavaDraft(problem.id, code)
    }
  }, [code, language, problem])

  const handleLanguageChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    if (!problem) return
    const newLang = e.target.value as Language
    setLanguage(newLang)

    const draft = newLang === 'PYTHON_3' 
      ? storage.getPythonDraft(problem.id) 
      : storage.getJavaDraft(problem.id)
    
    if (newLang === 'PYTHON_3') {
      if (!problem.starterCodePython) {
        setCode(draft || '# Python support is not available for this problem yet.')
      } else {
        setCode(draft || problem.starterCodePython || '')
      }
    } else {
      setCode(draft || problem.starterCode || '')
    }
  }

  const handleAction = async (isRunAction: boolean) => {
    if (!problem) return
    setIsSubmitting(true)
    setIsRun(isRunAction)
    setResult(null)
    setErrorResult(null)
    setPollMessage(isRunAction ? 'Running safely...' : 'Submitting safely...')
    pollingRef.current = true

    try {
      const req = {
        problemId: problem.id,
        language: language,
        code: code,
      }

      if (isRunAction) {
        const outcome = await api.submissions.run(req)
        setResult(outcome)
      } else {
        let outcome = await api.submissions.submitAsync(req)
        let attempts = 0
        
        while (
          pollingRef.current &&
          attempts < 15 &&
          ['QUEUED', 'COMPILING', 'RUNNING'].includes(outcome.status)
        ) {
          await new Promise(r => setTimeout(r, 1000))
          outcome = await api.submissions.get(outcome.id)
          setPollMessage(`Judge pending / processing (${outcome.status})...`)
          attempts++
        }
        setResult(outcome)
      }
    } catch (err) {
      if (err instanceof ApiError && err.status === 429) {
        setErrorResult('Rate limit exceeded. Please wait a moment before trying again.')
      } else {
        setErrorResult(err instanceof ApiError ? err.message : 'An error occurred')
      }
    } finally {
      setIsSubmitting(false)
      pollingRef.current = false
    }
  }

  // Early return states
  if (isLoadingProblem) return <Loading message="Loading workspace..." className="mt-20" />
  if (errorProblem || !problem) {
    return (
      <div className="mx-auto max-w-4xl py-12">
        <ErrorState message={errorProblem || 'Problem not found'} onRetry={() => window.location.reload()} />
      </div>
    )
  }

  const isPythonUnsupported = language === 'PYTHON_3' && !problem.starterCodePython

  return (
    <div className="min-h-[calc(100vh-64px)] flex flex-col lg:flex-row bg-[#0b0f19]">
      
      {/* ── Problem Pane ───────────────────────────────────────────────────── */}
      <div className="lg:w-[45%] flex flex-col border-r border-slate-800 bg-[#0d1117] overflow-y-auto max-h-screen custom-scrollbar">
        <div className="p-6 flex flex-col gap-6">
          <Link to="/problems" className="inline-flex items-center gap-2 text-xs font-medium text-slate-400 hover:text-white self-start transition-colors">
            <ChevronLeft className="h-4 w-4" />
            All problems
          </Link>
          
          <div>
            <div className="flex items-center gap-3">
              <h1 className="text-2xl font-bold text-white tracking-tight">{problem.title}</h1>
              <Badge variant={problem.difficulty.toLowerCase() as any} className="text-xs px-2.5 py-0.5">
                {problem.difficulty}
              </Badge>
            </div>
            {problem.topics && problem.topics.length > 0 && (
              <p className="mt-2 text-xs font-medium text-slate-400">
                {problem.topics.join(' \u2022 ')}
              </p>
            )}
          </div>

          <div className="prose prose-invert max-w-none text-sm text-slate-300 leading-relaxed font-sans whitespace-pre-wrap prose-pre:bg-slate-900 prose-pre:border prose-pre:border-slate-800">
            <h2 className="text-lg font-semibold text-slate-100 mb-3 border-b border-slate-800/50 pb-2">Problem Statement</h2>
            {problem.description}
          </div>

          <div className="mt-2">
            <h2 className="text-lg font-semibold text-slate-100 mb-4 border-b border-slate-800/50 pb-2">Sample Test Cases</h2>
            {testcases.length === 0 ? (
              <p className="text-sm text-slate-500 italic">No sample test cases available.</p>
            ) : (
              <div className="flex flex-col gap-4">
                {testcases.map((tc, idx) => (
                  <div key={idx} className="bg-[#161b22] border border-slate-800 rounded-lg p-4">
                    <div className="text-xs font-bold text-slate-500 mb-2 uppercase tracking-wider">Input</div>
                    <pre className="text-sm font-mono text-slate-300 bg-slate-900 p-2 rounded mb-3 overflow-x-auto">
                      {tc.inputData}
                    </pre>
                    <div className="text-xs font-bold text-slate-500 mb-2 uppercase tracking-wider">Expected Output</div>
                    <pre className="text-sm font-mono text-emerald-400 bg-slate-900 p-2 rounded overflow-x-auto">
                      {tc.expectedOutput}
                    </pre>
                  </div>
                ))}
              </div>
            )}
          </div>

          {problem.constraintsText && (
            <div className="mt-2 mb-8">
              <h2 className="text-lg font-semibold text-slate-100 mb-4 border-b border-slate-800/50 pb-2">Constraints</h2>
              <div className="bg-amber-500/5 border border-amber-500/10 rounded-lg p-4">
                <div className="text-amber-200/80 font-mono text-sm whitespace-pre-wrap leading-relaxed">
                  {problem.constraintsText}
                </div>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* ── Editor Pane ────────────────────────────────────────────────────── */}
      <div className="lg:w-[55%] flex flex-col bg-[#0b0f19] relative max-h-screen">
        
        {/* Editor Toolbar */}
        <div className="flex items-center justify-between px-4 py-3 bg-[#161b22] border-b border-slate-800 shrink-0">
          <div className="flex items-center gap-3">
            <label htmlFor="language" className="text-xs font-bold text-slate-500 uppercase tracking-wider">Language</label>
            <select 
              id="language"
              value={language}
              onChange={handleLanguageChange}
              className="bg-[#0d1117] border border-slate-700 text-slate-200 text-sm rounded-md px-3 py-1.5 focus:outline-none focus:ring-2 focus:ring-indigo-500/50 focus:border-indigo-500 transition-all cursor-pointer"
            >
              <option value="JAVA_17">Java</option>
              <option value="PYTHON_3">Python</option>
            </select>
          </div>
          
          <div className="flex items-center gap-2 text-slate-400">
            <Clock className="h-4 w-4" />
            <span className="text-xs font-medium uppercase tracking-widest">Time</span>
            <span className="text-sm font-bold text-slate-200 tabular-nums bg-slate-900 px-2 py-0.5 rounded border border-slate-800">
              {formatTime(timeSpent)}
            </span>
          </div>
        </div>

        {isPythonUnsupported && (
          <div className="bg-amber-500/10 border-b border-amber-500/20 px-4 py-2.5 flex items-center gap-3 shrink-0">
            <AlertTriangle className="h-5 w-5 text-amber-500 shrink-0" />
            <span className="text-sm font-medium text-amber-200/90">
              <strong>Notice:</strong> Python support is not available for this problem yet.
            </span>
          </div>
        )}

        {/* Textarea Editor */}
        <div className="flex-1 flex flex-col bg-[#0d1117] relative min-h-[300px]">
          <div className="flex items-center gap-3 px-4 py-2 bg-[#161b22]/50 border-b border-slate-800/50 text-xs font-mono text-slate-400">
            <FileCode className="h-4 w-4 text-indigo-400" />
            {language === 'JAVA_17' ? 'Solution.java' : 'solution.py'}
            <span className="ml-auto text-[10px] uppercase tracking-wider text-slate-500">Draft saved locally</span>
          </div>
          <textarea
            className="flex-1 w-full bg-transparent text-slate-200 font-mono text-sm leading-relaxed p-4 resize-none focus:outline-none custom-scrollbar"
            spellCheck={false}
            value={code}
            onChange={e => setCode(e.target.value)}
            disabled={isPythonUnsupported}
            aria-label="Code editor"
            placeholder="Write your solution here..."
          />
        </div>

        {/* Console / Result Panel */}
        <div className="shrink-0 bg-[#161b22] border-t border-slate-800 flex flex-col">
          <div className="px-4 py-2 flex items-center justify-between border-b border-slate-800/50 bg-[#0d1117]">
            <div className="flex items-center gap-2 text-xs font-bold text-slate-500 uppercase tracking-wider">
              <TerminalSquare className="h-4 w-4" />
              Console
            </div>
            {result && (
              <span className="text-xs font-medium text-slate-400">
                Last result: <strong className="text-slate-300">{result.status}</strong>
                {result.runtimeMs !== null ? ` \u2022 ${result.runtimeMs}ms` : ''}
              </span>
            )}
          </div>
          
          <div className="p-4 max-h-[30vh] overflow-y-auto custom-scrollbar">
            {isSubmitting ? (
              <div className="flex items-center gap-3 text-indigo-400 text-sm font-medium animate-pulse">
                <Loading message="" className="w-5 h-5 text-indigo-500" />
                {pollMessage}
              </div>
            ) : errorResult ? (
              <div className="text-red-400 text-sm font-medium flex items-center gap-2">
                <XCircle className="h-4 w-4" />
                {errorResult}
              </div>
            ) : result ? (
              <div className="flex flex-col gap-4 text-sm">
                <div className={`font-semibold flex items-center gap-2 ${
                  result.status === 'ACCEPTED' ? 'text-emerald-400' : 'text-red-400'
                }`}>
                  {result.status === 'ACCEPTED' ? <CheckCircle className="h-5 w-5" /> : <XCircle className="h-5 w-5" />}
                  {result.status.replace(/_/g, ' ')}
                  <span className="text-slate-400 font-normal text-xs ml-2">
                    {isRun ? 'Tested against sample cases only.' : 'This submission was recorded.'}
                  </span>
                </div>

                {['QUEUED', 'COMPILING', 'RUNNING'].includes(result.status) && (
                  <div className="bg-[#0d1117] border border-slate-800 p-3 rounded-md text-slate-300">
                    <strong>Timeout:</strong> The submission timed out waiting for the judge. Please check your profile later.
                  </div>
                )}
                {result.status === 'COMPILATION_ERROR' && result.compilerOutput && (
                  <div className="bg-[#0d1117] border border-slate-800 p-3 rounded-md">
                    <strong className="text-slate-400 text-xs uppercase tracking-wider mb-2 block">Compiler Output</strong>
                    <pre className="font-mono text-red-300 whitespace-pre-wrap">{result.compilerOutput}</pre>
                  </div>
                )}
                {result.status === 'RUNTIME_ERROR' && result.runtimeErrorOutput && (
                  <div className="bg-[#0d1117] border border-slate-800 p-3 rounded-md">
                    <strong className="text-slate-400 text-xs uppercase tracking-wider mb-2 block">Runtime Error</strong>
                    <pre className="font-mono text-red-300 whitespace-pre-wrap">{result.runtimeErrorOutput}</pre>
                  </div>
                )}
                {result.status === 'SYSTEM_ERROR' && result.systemErrorMessage && (
                  <div className="bg-[#0d1117] border border-slate-800 p-3 rounded-md">
                    <strong className="text-slate-400 text-xs uppercase tracking-wider mb-2 block">System Error</strong>
                    <pre className="font-mono text-amber-300 whitespace-pre-wrap">{result.systemErrorMessage}</pre>
                  </div>
                )}
                {result.status === 'TIME_LIMIT_EXCEEDED' && (
                  <div className="bg-[#0d1117] border border-slate-800 p-3 rounded-md text-amber-300">
                    <strong>Time Limit Exceeded:</strong> Your code took too long to execute.
                  </div>
                )}
              </div>
            ) : (
              <div className="text-slate-500 text-sm font-medium italic">
                Write your code, then run sample test cases.
              </div>
            )}
          </div>
        </div>

        {/* Action Panel */}
        <div className="px-4 py-3 bg-[#0d1117] border-t border-slate-800 shrink-0 flex flex-col sm:flex-row items-center justify-between gap-4">
          <p className="text-xs text-slate-500 font-medium">
            Solutions are evaluated in an isolated Docker sandbox.
          </p>
          <div className="flex w-full sm:w-auto items-center gap-3">
            <Button 
              variant="secondary" 
              className="flex-1 sm:flex-none border-slate-700 bg-[#161b22] hover:bg-slate-800 text-slate-300"
              onClick={() => handleAction(true)}
              disabled={isSubmitting || isPythonUnsupported}
            >
              <Play className="h-4 w-4 mr-2" />
              Run demo tests
            </Button>
            <Button 
              className="flex-1 sm:flex-none"
              onClick={() => handleAction(false)}
              disabled={isSubmitting || isPythonUnsupported}
            >
              <Code2 className="h-4 w-4 mr-2" />
              Submit solution
            </Button>
          </div>
        </div>

      </div>
    </div>
  )
}
