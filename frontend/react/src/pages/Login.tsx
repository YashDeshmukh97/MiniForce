import { Button, Card, CardHeader, CardTitle, Input } from '@/components/ui'
import { useAuth } from '@/context/AuthContext'
import { ApiError } from '@/services/api'
import { motion } from 'framer-motion'
import { LogIn } from 'lucide-react'
import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'

export default function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()

  const [identity, setIdentity] = useState('')
  const [password, setPassword] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  
  // To handle the `?next=` behavior or location state from RequireAuth
  const from = (location.state as any)?.from?.pathname || new URLSearchParams(location.search).get('next') || '/'

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!identity || !password) {
      setError('Please fill in both fields.')
      return
    }

    setIsLoading(true)
    setError(null)

    try {
      await login({ identity, password })
      navigate(from, { replace: true })
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'An unexpected error occurred.')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="flex min-h-[70vh] items-center justify-center">
      <motion.div
        initial={{ opacity: 0, y: 15 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3 }}
        className="w-full max-w-md"
      >
        <Card className="p-6 sm:p-8">
          <CardHeader className="text-center">
            <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-indigo-500/10">
              <LogIn className="h-6 w-6 text-indigo-400" />
            </div>
            <CardTitle className="text-2xl">Welcome Back</CardTitle>
            <p className="mt-2 text-sm text-slate-400">
              Sign in to MiniForce to continue
            </p>
          </CardHeader>

          {error && (
            <div className="mb-6 rounded-md bg-red-950/50 p-3 text-sm text-red-400 border border-red-900/50">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="flex flex-col gap-5">
            <Input
              label="Username or Email"
              id="identity"
              type="text"
              placeholder="e.g., coder123 or coder@example.com"
              value={identity}
              onChange={(e) => setIdentity(e.target.value)}
              disabled={isLoading}
              required
            />
            
            <Input
              label="Password"
              id="password"
              type="password"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              disabled={isLoading}
              required
            />

            <Button type="submit" className="mt-2 w-full" isLoading={isLoading}>
              Sign In
            </Button>
          </form>

          <p className="mt-6 text-center text-sm text-slate-400">
            Don't have an account?{' '}
            <Link to="/register" className="text-indigo-400 hover:text-indigo-300 font-medium">
              Create one
            </Link>
          </p>
        </Card>
      </motion.div>
    </div>
  )
}
