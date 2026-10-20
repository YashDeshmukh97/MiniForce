import { useEffect, useState, type FormEvent } from 'react'
import { useSearchParams, Link, useNavigate } from 'react-router-dom'
import { api } from '@/services/api'
import type { DiscussionSummary, DiscussionDetail } from '@/types'
import { useAuth } from '@/context/AuthContext'
import { motion } from 'framer-motion'
import { Loading } from '@/components/ui/Loading'
import { ErrorState } from '@/components/ui/ErrorState'
import { Card } from '@/components/ui/Card'
import { Button } from '@/components/ui/Button'
import { Input } from '@/components/ui/Input'
import { getAvatarImage } from '@/config/avatars'

const fmt = (dateStr: string) =>
  new Intl.DateTimeFormat(undefined, {
    month: 'short',
    day: 'numeric',
  }).format(new Date(dateStr))

export default function DiscussPage() {
  const [searchParams] = useSearchParams()
  const idParam = searchParams.get('id')

  if (idParam) {
    return <DiscussionDetailView id={Number(idParam)} />
  }

  return <DiscussionListView />
}

function DiscussionListView() {
  const { isAuthenticated } = useAuth()
  const navigate = useNavigate()
  const [discussions, setDiscussions] = useState<DiscussionSummary[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  
  const [showForm, setShowForm] = useState(false)
  const [title, setTitle] = useState('')
  const [body, setBody] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [submitError, setSubmitError] = useState<string | null>(null)

  useEffect(() => {
    api.discussions
      .list()
      .then(setDiscussions)
      .catch((err: Error) => setError(err.message))
      .finally(() => setIsLoading(false))
  }, [])

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    if (!isAuthenticated) return

    setIsSubmitting(true)
    setSubmitError(null)

    try {
      const created = await api.discussions.create({ title, body })
      navigate(`/discuss?id=${created.id}`)
    } catch (err: unknown) {
      setSubmitError(err instanceof Error ? err.message : 'Error creating discussion')
      setIsSubmitting(false)
    }
  }

  return (
    <main className="max-w-6xl mx-auto py-12 px-4 lg:px-8">
      <header className="mb-12 text-center">
        <div className="text-sm font-semibold text-indigo-400 uppercase tracking-wider mb-2">
          Community Knowledge
        </div>
        <h1 className="text-4xl font-extrabold text-slate-100 tracking-tight">
          Ask. Share. <span className="text-indigo-400">Solve together.</span>
        </h1>
        <p className="mt-4 text-slate-400 max-w-2xl mx-auto text-lg">
          Exchange approaches, post clean solutions and help fellow coders get
          unstuck without giving away the whole fun.
        </p>
      </header>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
        <div className="lg:col-span-3 space-y-6">
          <div className="flex items-center justify-between border-b border-slate-800 pb-4">
            <div className="flex gap-4">
              <button className="text-slate-100 font-semibold border-b-2 border-indigo-500 pb-4 -mb-[17px]">
                Recent
              </button>
            </div>
            <Button
              onClick={() => setShowForm(!showForm)}
              variant={showForm ? 'secondary' : 'primary'}
            >
              {showForm ? 'Cancel' : '+ New post'}
            </Button>
          </div>

          {showForm && (
            <motion.form
              initial={{ opacity: 0, height: 0 }}
              animate={{ opacity: 1, height: 'auto' }}
              className="bg-slate-900/50 border border-slate-800 rounded-xl p-6 space-y-4"
              onSubmit={handleSubmit}
            >
              {!isAuthenticated ? (
                <div className="text-center py-4 text-slate-400">
                  Please <Link to="/login" className="text-indigo-400 hover:underline">log in</Link> to create a discussion.
                </div>
              ) : (
                <>
                  <div>
                    <Input
                      required
                      maxLength={220}
                      placeholder="Discussion title"
                      value={title}
                      onChange={(e) => setTitle(e.target.value)}
                    />
                  </div>
                  <div>
                    <textarea
                      required
                      maxLength={10000}
                      rows={4}
                      placeholder="Share a question, solution idea, or useful hint"
                      className="w-full bg-slate-800/50 border border-slate-700 rounded-lg px-4 py-3 text-slate-200 placeholder:text-slate-500 focus:outline-none focus:ring-2 focus:ring-indigo-500/50 focus:border-indigo-500 resize-y"
                      value={body}
                      onChange={(e) => setBody(e.target.value)}
                    />
                  </div>
                  <div className="flex items-center gap-4">
                    <Button type="submit" disabled={isSubmitting}>
                      {isSubmitting ? 'Publishing...' : 'Publish discussion'}
                    </Button>
                    {submitError && (
                      <span className="text-red-400 text-sm">{submitError}</span>
                    )}
                  </div>
                </>
              )}
            </motion.form>
          )}

          {isLoading ? (
            <div className="py-20 flex justify-center">
              <Loading />
            </div>
          ) : error ? (
            <ErrorState message={error} />
          ) : discussions.length === 0 ? (
            <Card className="py-12 text-center text-slate-400">
              No discussions yet. Start the conversation.
            </Card>
          ) : (
            <div className="space-y-4">
              {discussions.map((d) => (
                <motion.article
                  key={d.id}
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  className="bg-slate-900/50 border border-slate-800 rounded-xl p-6 flex gap-6 hover:border-slate-700 transition-colors"
                >
                  <div className="flex-shrink-0">
                    <img
                      src={getAvatarImage(d.authorAvatarKey)}
                      alt=""
                      className="w-12 h-12 rounded-full border border-slate-700 bg-slate-800"
                    />
                  </div>
                  <div className="flex-1 min-w-0">
                    <h3 className="text-lg font-bold text-slate-100 mb-1 line-clamp-1">
                      <Link
                        to={`/discuss?id=${d.id}`}
                        className="hover:text-indigo-400 focus:outline-none focus:underline"
                      >
                        {d.title}
                      </Link>
                    </h3>
                    <p className="text-slate-400 text-sm line-clamp-2 mb-3">
                      {d.body}
                    </p>
                    <div className="flex items-center gap-4 text-xs font-medium text-slate-500">
                      <span className="px-2 py-0.5 bg-indigo-500/10 text-indigo-400 rounded">
                        Community
                      </span>
                      <span>{fmt(d.createdAt)}</span>
                    </div>
                  </div>
                  <div className="flex-shrink-0 flex items-center justify-center text-sm font-medium text-slate-400 bg-slate-800/30 px-3 py-1 rounded-full h-8">
                    {d.replyCount} replies
                  </div>
                </motion.article>
              ))}
            </div>
          )}
        </div>

        <aside className="space-y-6">
          <Card className="p-6">
            <h3 className="text-lg font-bold text-slate-200 mb-4">Explore topics</h3>
            <div className="flex flex-col gap-2">
              {['Arrays', 'Dynamic Programming', 'Interview Prep', 'Java', 'Contests'].map(
                (topic) => (
                  <a
                    key={topic}
                    href="#"
                    className="text-slate-400 hover:text-indigo-400 transition-colors"
                  >
                    {topic}
                  </a>
                )
              )}
            </div>
          </Card>
          <Card className="p-6">
            <h3 className="text-lg font-bold text-slate-200 mb-4">
              Community guidelines
            </h3>
            <p className="text-sm text-slate-400 leading-relaxed">
              Give hints first. Mark spoilers clearly. Make the next coder stronger.
            </p>
          </Card>
        </aside>
      </div>
    </main>
  )
}

function DiscussionDetailView({ id }: { id: number }) {
  const { isAuthenticated } = useAuth()
  const [detail, setDetail] = useState<DiscussionDetail | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const [replyBody, setReplyBody] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [replyError, setReplyError] = useState<string | null>(null)

  useEffect(() => {
    api.discussions
      .get(id)
      .then(setDetail)
      .catch((err: Error) => setError(err.message))
      .finally(() => setIsLoading(false))
  }, [id])

  const handleReply = async (e: FormEvent) => {
    e.preventDefault()
    if (!isAuthenticated) return

    setIsSubmitting(true)
    setReplyError(null)

    try {
      const newReply = await api.discussions.reply(id, { body: replyBody })
      setDetail((prev) =>
        prev
          ? {
              ...prev,
              replies: [...prev.replies, newReply],
              replyCount: prev.replyCount + 1,
            }
          : null
      )
      setReplyBody('')
    } catch (err: unknown) {
      setReplyError(err instanceof Error ? err.message : 'Error posting reply')
    } finally {
      setIsSubmitting(false)
    }
  }

  if (isLoading) {
    return (
      <div className="flex justify-center py-20">
        <Loading />
      </div>
    )
  }

  if (error || !detail) {
    return (
      <div className="max-w-3xl mx-auto py-12 px-4">
        <Link to="/discuss" className="text-indigo-400 hover:underline mb-6 inline-block">
          ← All discussions
        </Link>
        <ErrorState message={error || 'Discussion not found'} />
      </div>
    )
  }

  return (
    <main className="max-w-3xl mx-auto py-12 px-4 lg:px-8">
      <Link
        to="/discuss"
        className="text-slate-400 hover:text-indigo-400 font-medium transition-colors mb-8 inline-block"
      >
        ← All discussions
      </Link>

      <Card className="p-8 mb-8 border-slate-700 bg-slate-900/60 shadow-xl">
        <h1 className="text-3xl font-extrabold text-slate-100 mb-6">
          {detail.title}
        </h1>
        <p className="text-slate-300 text-lg leading-relaxed mb-8 whitespace-pre-wrap">
          {detail.body}
        </p>
        <div className="flex items-center gap-4 text-sm text-slate-500 border-t border-slate-800 pt-6">
          <img
            src={getAvatarImage(detail.authorAvatarKey)}
            alt=""
            className="w-8 h-8 rounded-full border border-slate-700 bg-slate-800"
          />
          <div>
            Posted by <strong className="text-slate-300">{detail.authorUsername}</strong>{' '}
            on {fmt(detail.createdAt)}
          </div>
        </div>
      </Card>

      <div className="space-y-6 mb-12">
        <h3 className="text-xl font-bold text-slate-200">
          {detail.replies.length} Replies
        </h3>
        
        {detail.replies.length === 0 ? (
          <div className="text-slate-500 italic py-4">No replies yet.</div>
        ) : (
          <div className="space-y-4">
            {detail.replies.map((r, i) => (
              <motion.div
                key={r.id || i}
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                className="p-6 rounded-xl bg-slate-800/30 border border-slate-800"
              >
                <div className="flex items-center gap-3 mb-4">
                  <span className="font-semibold text-slate-200">
                    {r.authorUsername}
                  </span>
                  <span className="text-xs text-slate-500">
                    {fmt(r.createdAt)}
                  </span>
                </div>
                <p className="text-slate-300 whitespace-pre-wrap leading-relaxed">
                  {r.body}
                </p>
              </motion.div>
            ))}
          </div>
        )}
      </div>

      <div className="bg-slate-900/50 border border-slate-800 rounded-xl p-6">
        <h4 className="font-semibold text-slate-200 mb-4">Add a reply</h4>
        {!isAuthenticated ? (
          <div className="text-slate-400">
            Please <Link to="/login" className="text-indigo-400 hover:underline">log in</Link> to reply.
          </div>
        ) : (
          <form onSubmit={handleReply} className="space-y-4">
            <textarea
              required
              maxLength={5000}
              rows={4}
              placeholder="Add a helpful reply"
              className="w-full bg-slate-800/50 border border-slate-700 rounded-lg px-4 py-3 text-slate-200 placeholder:text-slate-500 focus:outline-none focus:ring-2 focus:ring-indigo-500/50 focus:border-indigo-500 resize-y"
              value={replyBody}
              onChange={(e) => setReplyBody(e.target.value)}
            />
            <div className="flex items-center gap-4">
              <Button type="submit" disabled={isSubmitting}>
                {isSubmitting ? 'Replying...' : 'Reply'}
              </Button>
              {replyError && <span className="text-red-400 text-sm">{replyError}</span>}
            </div>
          </form>
        )}
      </div>
    </main>
  )
}
