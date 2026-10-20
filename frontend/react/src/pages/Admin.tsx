import { useEffect, useState, type FormEvent } from 'react'
import { api } from '@/services/api'
import type { AdminDashboard, AdminUser, DiscussionSummary, Difficulty } from '@/types'
import { motion } from 'framer-motion'
import { Loading } from '@/components/ui/Loading'
import { ErrorState } from '@/components/ui/ErrorState'
import { Card } from '@/components/ui/Card'
import { Button } from '@/components/ui/Button'
import { Input } from '@/components/ui/Input'

export default function AdminPage() {
  const [stats, setStats] = useState<AdminDashboard | null>(null)
  const [users, setUsers] = useState<AdminUser[]>([])
  const [discussions, setDiscussions] = useState<DiscussionSummary[]>([])
  
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [search, setSearch] = useState('')

  // Problem Form state
  const [probSlug, setProbSlug] = useState('')
  const [probTitle, setProbTitle] = useState('')
  const [probDifficulty, setProbDifficulty] = useState<Difficulty>('EASY')
  const [probDescription, setProbDescription] = useState('')
  const [probTopics, setProbTopics] = useState('')
  const [isCreatingProb, setIsCreatingProb] = useState(false)
  const [probMsg, setProbMsg] = useState<{ type: 'success' | 'error'; text: string } | null>(null)

  const loadData = async (userSearch = '') => {
    try {
      const [dashRes, usersRes, discRes] = await Promise.all([
        api.admin.dashboard(),
        api.admin.users(userSearch),
        api.discussions.list(),
      ])
      setStats(dashRes)
      setUsers(usersRes)
      setDiscussions(discRes)
      setError(null)
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message + ' Admin access required.' : 'Access Denied')
    } finally {
      setIsLoading(false)
    }
  }

  useEffect(() => {
    loadData()
  }, [])

  // Handle user search debounce
  useEffect(() => {
    if (isLoading) return
    const timer = setTimeout(() => {
      api.admin.users(search).then(setUsers).catch(console.error)
    }, 400)
    return () => clearTimeout(timer)
  }, [search])

  const handleRoleChange = async (userId: number, newRole: 'USER' | 'ADMIN') => {
    try {
      await api.admin.setRole(userId, newRole)
      setUsers((prev) =>
        prev.map((u) => (u.id === userId ? { ...u, role: newRole } : u))
      )
    } catch (err: unknown) {
      alert(err instanceof Error ? err.message : 'Error changing role')
    }
  }

  const handleDeleteDiscussion = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this discussion?')) return
    try {
      await api.admin.removeDiscussion(id)
      setDiscussions((prev) => prev.filter((d) => d.id !== id))
    } catch (err: unknown) {
      alert(err instanceof Error ? err.message : 'Error deleting discussion')
    }
  }

  const handleCreateProblem = async (e: FormEvent) => {
    e.preventDefault()
    setIsCreatingProb(true)
    setProbMsg(null)
    try {
      await api.problems.create({
        slug: probSlug,
        title: probTitle,
        difficulty: probDifficulty,
        description: probDescription,
        starterCode: 'class Solution { }',
        constraintsText: 'See problem statement.',
        acceptanceRate: 0,
        topics: probTopics
          .split(',')
          .map((x) => x.trim())
          .filter(Boolean),
      })
      setProbMsg({ type: 'success', text: 'Problem created successfully.' })
      setProbSlug('')
      setProbTitle('')
      setProbDifficulty('EASY')
      setProbDescription('')
      setProbTopics('')
    } catch (err: unknown) {
      setProbMsg({
        type: 'error',
        text: err instanceof Error ? err.message : 'Error creating problem',
      })
    } finally {
      setIsCreatingProb(false)
    }
  }

  if (isLoading) {
    return (
      <div className="flex justify-center py-20">
        <Loading />
      </div>
    )
  }

  if (error || !stats) {
    return (
      <div className="max-w-4xl mx-auto py-12 px-4">
        <ErrorState message={error || 'Access Denied'} />
      </div>
    )
  }

  return (
    <main className="max-w-7xl mx-auto py-12 px-4 lg:px-8 space-y-12">
      <header>
        <div className="text-sm font-semibold text-indigo-400 uppercase tracking-wider mb-2">
          Admin Console
        </div>
        <h1 className="text-4xl font-extrabold text-slate-100 tracking-tight">
          Platform <span className="text-indigo-400">Management</span>
        </h1>
      </header>

      {/* Stats */}
      <section>
        <h2 className="text-2xl font-bold text-slate-200 mb-6">Platform Overview</h2>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          <StatCard value={stats.users} label="Total Users" />
          <StatCard value={stats.submissions} label="Submissions" />
          <StatCard value={stats.problems} label="Problems" />
          <StatCard value={stats.discussions} label="Discussions" />
        </div>
      </section>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* User Management */}
        <section>
          <Card className="flex flex-col h-[500px]">
            <div className="p-6 pb-4 border-b border-slate-800 flex justify-between items-center gap-4">
              <h2 className="text-xl font-bold text-slate-200 whitespace-nowrap">
                User Management
              </h2>
              <div className="w-full max-w-xs">
                <Input
                  placeholder="Search users..."
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                  className="h-9 text-sm"
                />
              </div>
            </div>
            <div className="flex-1 overflow-auto p-0">
              <table className="w-full text-left text-sm text-slate-300">
                <thead className="bg-slate-900 sticky top-0 text-xs uppercase text-slate-400 border-b border-slate-800 shadow-sm z-10">
                  <tr>
                    <th className="px-6 py-3 font-semibold">User</th>
                    <th className="px-6 py-3 font-semibold hidden sm:table-cell">Email</th>
                    <th className="px-6 py-3 font-semibold">Role</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800/50">
                  {users.length === 0 ? (
                    <tr>
                      <td colSpan={3} className="px-6 py-8 text-center text-slate-500">
                        No users found.
                      </td>
                    </tr>
                  ) : (
                    users.map((u) => (
                      <tr key={u.id} className="hover:bg-slate-800/30 transition-colors">
                        <td className="px-6 py-3">
                          <div className="font-medium text-slate-200">{u.username}</div>
                          <div className="text-xs text-slate-500 font-mono">ID: {u.id}</div>
                        </td>
                        <td className="px-6 py-3 hidden sm:table-cell text-slate-400">
                          {u.email}
                        </td>
                        <td className="px-6 py-3">
                          <select
                            className="bg-slate-800 border border-slate-700 text-slate-200 rounded px-2 py-1 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500/50"
                            value={u.role}
                            onChange={(e) =>
                              handleRoleChange(u.id, e.target.value as 'USER' | 'ADMIN')
                            }
                          >
                            <option value="USER">USER</option>
                            <option value="ADMIN">ADMIN</option>
                          </select>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </Card>
        </section>

        <div className="space-y-8">
          {/* Moderation */}
          <section>
            <Card className="flex flex-col h-[300px]">
              <div className="p-6 pb-4 border-b border-slate-800">
                <h2 className="text-xl font-bold text-slate-200">Moderation</h2>
              </div>
              <div className="flex-1 overflow-auto p-0">
                <table className="w-full text-left text-sm text-slate-300">
                  <thead className="bg-slate-900 sticky top-0 text-xs uppercase text-slate-400 border-b border-slate-800 shadow-sm z-10">
                    <tr>
                      <th className="px-6 py-3 font-semibold">Title</th>
                      <th className="px-6 py-3 font-semibold hidden sm:table-cell">Author</th>
                      <th className="px-6 py-3 font-semibold text-right">Actions</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-800/50">
                    {discussions.length === 0 ? (
                      <tr>
                        <td colSpan={3} className="px-6 py-8 text-center text-slate-500">
                          No discussions found.
                        </td>
                      </tr>
                    ) : (
                      discussions.map((d) => (
                        <motion.tr
                          key={d.id}
                          layout
                          className="hover:bg-slate-800/30 transition-colors"
                        >
                          <td className="px-6 py-3 font-medium text-slate-200 line-clamp-1 break-all">
                            {d.title}
                          </td>
                          <td className="px-6 py-3 hidden sm:table-cell text-slate-400">
                            {d.authorUsername}
                          </td>
                          <td className="px-6 py-3 text-right">
                            <Button
                              variant="danger"
                              className="h-7 text-xs px-2"
                              onClick={() => handleDeleteDiscussion(d.id)}
                            >
                              Delete
                            </Button>
                          </td>
                        </motion.tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            </Card>
          </section>

          {/* Create Problem */}
          <section>
            <Card className="p-6">
              <h2 className="text-xl font-bold text-slate-200 mb-4">
                Create Problem
              </h2>
              <form onSubmit={handleCreateProblem} className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <Input
                    required
                    placeholder="Problem slug (e.g. valid-palindrome)"
                    value={probSlug}
                    onChange={(e) => setProbSlug(e.target.value)}
                  />
                  <Input
                    required
                    placeholder="Problem Title"
                    value={probTitle}
                    onChange={(e) => setProbTitle(e.target.value)}
                  />
                </div>
                <div className="grid grid-cols-2 gap-4">
                  <select
                    className="flex h-10 w-full rounded-md border border-slate-800 bg-slate-900/50 px-3 py-2 text-sm text-slate-200 focus:outline-none focus:ring-2 focus:ring-indigo-500/50"
                    value={probDifficulty}
                    onChange={(e) => setProbDifficulty(e.target.value as Difficulty)}
                  >
                    <option value="EASY">EASY</option>
                    <option value="MEDIUM">MEDIUM</option>
                    <option value="HARD">HARD</option>
                  </select>
                  <Input
                    placeholder="Topics (comma separated)"
                    value={probTopics}
                    onChange={(e) => setProbTopics(e.target.value)}
                  />
                </div>
                <textarea
                  required
                  rows={3}
                  placeholder="Problem statement"
                  className="w-full bg-slate-900/50 border border-slate-800 rounded-lg px-4 py-3 text-slate-200 placeholder:text-slate-500 focus:outline-none focus:ring-2 focus:ring-indigo-500/50 resize-y"
                  value={probDescription}
                  onChange={(e) => setProbDescription(e.target.value)}
                />
                <div className="flex items-center gap-4">
                  <Button type="submit" disabled={isCreatingProb}>
                    {isCreatingProb ? 'Creating...' : 'Create problem'}
                  </Button>
                  {probMsg && (
                    <span
                      className={`text-sm ${
                        probMsg.type === 'success' ? 'text-green-400' : 'text-red-400'
                      }`}
                    >
                      {probMsg.text}
                    </span>
                  )}
                </div>
              </form>
            </Card>
          </section>
        </div>
      </div>
    </main>
  )
}

function StatCard({ label, value }: { label: string; value: number }) {
  return (
    <Card className="p-6 flex flex-col items-center justify-center bg-slate-900/50 border-slate-800">
      <span className="text-3xl font-black text-slate-100 mb-1">{value}</span>
      <span className="text-xs font-semibold uppercase tracking-wider text-slate-500">
        {label}
      </span>
    </Card>
  )
}
