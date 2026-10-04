import { Badge, Button, Card, ErrorState, Input, Loading } from '@/components/ui'
import { useAuth } from '@/context/AuthContext'
import { useApi } from '@/hooks/useApi'
import { api } from '@/services/api'
import { motion } from 'framer-motion'
import { Calendar, Edit2, Check, X, Medal, Flame, Activity } from 'lucide-react'
import { useMemo, useState } from 'react'

const AVATARS = [
  "robot_01", "cat_01", "fox_01", "panda_01", "lion_01", 
  "frog_01", "koala_01", "tiger_01", "monkey_01", "unicorn_01", 
  "developer_01", "gaming_01", "rocket_01", "lightning_01", 
  "terminal_01", "controller_01", "avatar_default"
]

export default function ProfilePage() {
  const { user, refreshUser } = useAuth()
  const { data, isLoading, error, refetch } = useApi(() => api.users.profile())

  const [isEditing, setIsEditing] = useState(false)
  const [editForm, setEditForm] = useState({ fullName: '', bio: '' })
  const [isUpdating, setIsUpdating] = useState(false)

  const [isAvatarOpen, setIsAvatarOpen] = useState(false)
  const [selectedAvatar, setSelectedAvatar] = useState('')

  const startEdit = () => {
    setEditForm({ fullName: user?.fullName || '', bio: user?.bio || '' })
    setIsEditing(true)
  }

  const handleUpdate = async () => {
    setIsUpdating(true)
    try {
      await api.users.update(editForm)
      await refreshUser()
      setIsEditing(false)
    } catch (err) {
      alert('Failed to update profile')
    } finally {
      setIsUpdating(false)
    }
  }

  const handleAvatarUpdate = async () => {
    if (!selectedAvatar) return
    setIsUpdating(true)
    try {
      await api.users.updateAvatar(selectedAvatar)
      await refreshUser()
      setIsAvatarOpen(false)
    } catch (err) {
      alert('Failed to update avatar')
    } finally {
      setIsUpdating(false)
    }
  }

  const activityCells = useMemo(() => {
    if (!data?.activity) return []
    const today = new Date()
    const cells = []
    for (let i = 364; i >= 0; i--) {
      const d = new Date(today)
      d.setDate(today.getDate() - i)
      const key = d.toISOString().slice(0, 10)
      const count = data.activity[key] || 0
      cells.push({ date: key, count })
    }
    return cells
  }, [data?.activity])

  if (isLoading) return <Loading message="Loading profile..." className="mt-20" />
  if (error || !data || !user) return (
    <div className="mx-auto max-w-4xl py-12">
      <ErrorState message={error || 'Profile not found'} onRetry={refetch} />
    </div>
  )

  const totalActivity = activityCells.reduce((sum, cell) => sum + cell.count, 0)

  return (
    <div className="mx-auto max-w-5xl flex flex-col gap-8 pb-12">
      
      {/* ── Hero Profile ───────────────────────────────────────────────────── */}
      <Card className="p-0 overflow-hidden border-slate-800 bg-slate-900/50">
        <div className="h-32 bg-gradient-to-r from-indigo-900/40 via-purple-900/20 to-slate-900" />
        <div className="px-6 pb-6 sm:px-10">
          <div className="flex flex-col sm:flex-row gap-6 sm:items-end -mt-16 mb-4">
            <div className="relative">
              <div className="h-32 w-32 rounded-full border-4 border-slate-950 bg-slate-800 overflow-hidden">
                <img 
                  src={`/frontend/assets/avatars/${user.avatarKey || 'avatar_default'}.svg`} 
                  alt="Avatar" 
                  className="h-full w-full object-cover"
                />
              </div>
              <button 
                onClick={() => { setSelectedAvatar(user.avatarKey || 'avatar_default'); setIsAvatarOpen(true); }}
                className="absolute bottom-1 right-1 h-8 w-8 rounded-full bg-indigo-600 flex items-center justify-center text-white border-2 border-slate-950 hover:bg-indigo-500 transition-colors"
                title="Change Avatar"
              >
                <Edit2 className="h-4 w-4" />
              </button>
            </div>
            
            <div className="flex-1 pb-2">
              <h1 className="text-3xl font-bold text-white tracking-tight">{user.fullName}</h1>
              <p className="text-indigo-400 font-medium">@{user.username}</p>
            </div>
            
            <div className="flex items-center gap-4 pb-3">
              <div className="text-center">
                <div className="text-2xl font-bold text-white">{user.rating.toLocaleString()}</div>
                <div className="text-xs text-slate-400 font-medium uppercase tracking-wider">Rating</div>
              </div>
            </div>
          </div>

          <div className="mt-6 flex flex-col lg:flex-row gap-8">
            <div className="flex-1">
              <div className="flex items-center justify-between mb-2">
                <h3 className="text-sm font-semibold text-slate-300 uppercase tracking-wider">About</h3>
                {!isEditing && (
                  <button onClick={startEdit} className="text-xs text-indigo-400 hover:text-indigo-300 font-medium">
                    Edit Profile
                  </button>
                )}
              </div>
              
              {isEditing ? (
                <div className="flex flex-col gap-3 p-4 rounded-lg bg-slate-950 border border-slate-800">
                  <Input 
                    label="Full Name" 
                    value={editForm.fullName} 
                    onChange={e => setEditForm(prev => ({...prev, fullName: e.target.value}))} 
                  />
                  <Input 
                    label="Bio" 
                    value={editForm.bio} 
                    onChange={e => setEditForm(prev => ({...prev, bio: e.target.value}))} 
                  />
                  <div className="flex justify-end gap-2 mt-2">
                    <Button variant="ghost" size="sm" onClick={() => setIsEditing(false)}>Cancel</Button>
                    <Button size="sm" onClick={handleUpdate} isLoading={isUpdating}>Save</Button>
                  </div>
                </div>
              ) : (
                <p className="text-slate-300 leading-relaxed text-sm">
                  {user.bio || 'Add a bio from your account settings.'}
                </p>
              )}
            </div>

            <div className="flex gap-6 lg:border-l lg:border-slate-800 lg:pl-8">
              <div className="flex flex-col gap-1">
                <div className="flex items-center gap-2 text-slate-400">
                  <Check className="h-4 w-4" />
                  <span className="text-sm">Solved</span>
                </div>
                <div className="text-xl font-semibold text-slate-200">{user.solvedCount}</div>
              </div>
              <div className="flex flex-col gap-1">
                <div className="flex items-center gap-2 text-slate-400">
                  <Flame className="h-4 w-4" />
                  <span className="text-sm">Streak</span>
                </div>
                <div className="text-xl font-semibold text-slate-200">{user.streak} days</div>
              </div>
              <div className="flex flex-col gap-1">
                <div className="flex items-center gap-2 text-slate-400">
                  <Medal className="h-4 w-4" />
                  <span className="text-sm">Contests</span>
                </div>
                <div className="text-xl font-semibold text-slate-200">{data.contestsJoined}</div>
              </div>
            </div>
          </div>
        </div>
      </Card>

      {/* ── Avatar Modal ───────────────────────────────────────────────────── */}
      {isAvatarOpen && (
        <div className="fixed inset-0 z-[100] flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
          <motion.div 
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            className="w-full max-w-lg rounded-2xl bg-slate-900 p-6 border border-slate-800 shadow-2xl"
          >
            <div className="flex justify-between items-center mb-6">
              <h3 className="text-lg font-bold text-white">Choose Avatar</h3>
              <button onClick={() => setIsAvatarOpen(false)} className="text-slate-400 hover:text-white">
                <X className="h-5 w-5" />
              </button>
            </div>
            
            <div className="grid grid-cols-4 sm:grid-cols-5 gap-4 max-h-[60vh] overflow-y-auto p-1">
              {AVATARS.map(a => (
                <button
                  key={a}
                  onClick={() => setSelectedAvatar(a)}
                  className={`aspect-square rounded-full border-2 transition-all p-1 ${
                    selectedAvatar === a ? 'border-indigo-500 scale-110 bg-indigo-500/10' : 'border-transparent hover:border-slate-700'
                  }`}
                >
                  <img src={`/frontend/assets/avatars/${a}.svg`} className="w-full h-full" alt={a} />
                </button>
              ))}
            </div>

            <div className="flex justify-end gap-3 mt-8">
              <Button variant="ghost" onClick={() => setIsAvatarOpen(false)}>Cancel</Button>
              <Button onClick={handleAvatarUpdate} isLoading={isUpdating}>Save Avatar</Button>
            </div>
          </motion.div>
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* ── Activity Graph ─────────────────────────────────────────────────── */}
        <div className="lg:col-span-2 flex flex-col gap-4">
          <h2 className="text-lg font-semibold text-slate-200 flex items-center gap-2">
            <Activity className="h-5 w-5 text-indigo-400" />
            Activity
          </h2>
          <Card className="p-6">
            <div className="flex flex-wrap gap-1 max-h-[140px] overflow-hidden flex-col content-start">
              {activityCells.map((cell, i) => {
                let colorClass = 'bg-slate-800'
                if (cell.count === 1) colorClass = 'bg-emerald-900/60'
                else if (cell.count <= 3) colorClass = 'bg-emerald-700/80'
                else if (cell.count <= 5) colorClass = 'bg-emerald-500'
                else if (cell.count > 5) colorClass = 'bg-emerald-400'
                
                return (
                  <div 
                    key={i} 
                    title={`${cell.count} submissions on ${cell.date}`}
                    className={`h-[10px] w-[10px] rounded-sm ${colorClass} hover:ring-1 hover:ring-slate-300 transition-all`}
                  />
                )
              })}
            </div>
            <p className="text-sm text-slate-400 mt-4">
              {totalActivity} accepted submission{totalActivity !== 1 && 's'} in the last year
            </p>
          </Card>
        </div>

        {/* ── Recent Submissions ─────────────────────────────────────────────── */}
        <div className="flex flex-col gap-4">
          <h2 className="text-lg font-semibold text-slate-200 flex items-center gap-2">
            <Calendar className="h-5 w-5 text-indigo-400" />
            Recent Submissions
          </h2>
          <Card className="p-0 overflow-hidden">
            <ul className="divide-y divide-slate-800/50">
              {data.recentSubmissions?.length === 0 ? (
                <li className="p-4 text-sm text-slate-400 text-center">
                  No submissions yet. Start with an Easy problem.
                </li>
              ) : (
                data.recentSubmissions?.slice(0, 5).map((sub, i) => (
                  <li key={i} className="flex flex-col p-4 hover:bg-slate-800/30 transition-colors">
                    <span className="font-medium text-slate-200">{sub.problemTitle}</span>
                    <div className="flex justify-between items-center mt-2">
                      <span className="text-xs text-slate-500">{new Date(sub.submittedAt).toLocaleDateString()}</span>
                      <Badge variant={sub.status === 'ACCEPTED' ? 'success' : sub.status === 'COMPILATION_ERROR' ? 'warning' : 'error'}>
                        {sub.status.replace(/_/g, ' ')}
                      </Badge>
                    </div>
                  </li>
                ))
              )}
            </ul>
          </Card>
        </div>
      </div>
    </div>
  )
}
